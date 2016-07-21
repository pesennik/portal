package com.github.pesennik.page.signin;

import com.github.pesennik.Context;
import com.github.pesennik.UserSession;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.Feedback;
import com.github.pesennik.model.SocialNetworkType;
import com.github.pesennik.model.User;
import com.github.pesennik.model.UserPersonalInfo;
import com.github.pesennik.page.BasePage;
import com.github.pesennik.page.HomePage;
import com.github.pesennik.util.RegistrationUtils;
import com.github.pesennik.util.TextUtils;
import com.github.pesennik.util.UDate;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.ValidatorUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.io.IClusterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.ALWAYS_REDIRECT;

/**
 *
 */
@MountPath("/registration-social")
public class RegistrationViaSocialNetworkPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(RegistrationViaSocialNetworkPage.class);

    // used by wicket
    @SuppressWarnings("unused")
    public RegistrationViaSocialNetworkPage() {
        throw new RestartResponseException(new PageProvider(LoginPage.class), ALWAYS_REDIRECT);
    }

    public RegistrationViaSocialNetworkPage(SocialRegData socialData) {
        if (UserSession.get().isSignedIn()) {
            throw new RestartResponseException(new PageProvider(HomePage.class), ALWAYS_REDIRECT);
        }
        Form form = new Form("form");
        add(form);

        Feedback feedback = new Feedback("feedback");
        form.add(feedback);

        String verifiedEmail = socialData.email;
        String login = verifiedEmail.split("@")[0];
        String password = RandomStringUtils.random(6, true, true);

        add(new BookmarkablePageLink<>("sign_in_link", LoginPage.class));

        TextField<String> firstNameField = new TextField<>("first_name", Model.of(socialData.firstName));
        form.add(firstNameField);

        TextField<String> lastNameField = new TextField<>("last_name", Model.of(socialData.lastName));
        form.add(lastNameField);

        TextField<String> loginField = new TextField<>("login", Model.of(login));
        form.add(loginField);

        TextField<String> emailField = new TextField<>("email", Model.of(verifiedEmail));
        emailField.setEnabled(verifiedEmail.isEmpty());
        form.add(emailField);

        PasswordTextField password1Field = new PasswordTextField("password1", Model.of(password));
        password1Field.setResetPassword(false);
        form.add(password1Field);

        PasswordTextField password2Field = new PasswordTextField("password2", Model.of(password));
        password2Field.setResetPassword(false);
        form.add(password2Field);

        form.add(new AjaxSubmitLink("submit", form) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    feedback.clear();
                    target.add(feedback);

                    String firstName = firstNameField.getModelObject();
                    if (!ValidatorUtils.isValidFirstOrLastName(firstName)) {
                        feedback.error("Недопустимое имя пользователя!");
                        return;
                    }
                    String lastName = lastNameField.getModelObject();
                    if (!ValidatorUtils.isValidFirstOrLastName(lastName)) {
                        feedback.error("Недопустимая фамилия пользователя!");
                        return;
                    }
                    String email = emailField.getModelObject();
                    User user = Context.getUsersDbi().getUserByEmail(email);
                    if (user != null) {
                        feedback.error("Пользователь с таким email уже существует!");
                        return;
                    }
                    String password1 = password1Field.getModelObject();
                    String password2 = password2Field.getModelObject();
                    String err = RegistrationUtils.validatePassword(password1, password2);
                    if (err != null) {
                        feedback.error(err);
                        return;
                    }

                    UDate now = UDate.now();
                    user = new User();
                    user.lastLoginDate = now;
                    user.registrationDate = now;
                    user.email = email;
                    user.passwordHash = UserSessionUtils.password2Hash(password1);
                    Context.getUsersDbi().createUser(user);

                    UserPersonalInfo pi = user.personalInfo;
                    if (TextUtils.isEmpty(pi.firstName)) {
                        pi.firstName = firstName;
                    }
                    if (TextUtils.isEmpty(pi.lastName)) {
                        pi.lastName = lastName;
                    }
                    pi.socialIds.put(socialData.network, socialData.socialId);
                    Context.getUsersDbi().updatePersonalInfo(user);

                    UserSessionUtils.login(user);
                    setResponsePage(HomePage.class);
                    RegistrationUtils.sendWelcomeEmail(user, password1);
                } catch (IOException e) {
                    log.error("Error during sending email! " + e.getMessage());
                    feedback.error("Не удалось отправить письмо для подтверждения регистрации, повторите попытку позже!");
                } catch (Exception e) {
                    log.error("Error during user registration using social network! " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static class SocialRegData implements IClusterable {
        public String firstName;
        public String lastName;
        public String email;
        public String socialId;
        public SocialNetworkType network;
    }
}
