package com.github.pesennik.page.signin;


import com.github.pesennik.Context;
import com.github.pesennik.UserSettings;
import com.github.pesennik.component.SocialLoginPanel;
import com.github.pesennik.component.parsley.PasswordJsValidator;
import com.github.pesennik.component.parsley.RequiredFieldJsValidator;
import com.github.pesennik.component.parsley.ValidatingJsAjaxSubmitLink;
import com.github.pesennik.page.BasePage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pesennik.UserSession;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.CaptchaField;
import com.github.pesennik.component.Feedback;
import com.github.pesennik.component.PasswordField;
import com.github.pesennik.component.RefreshCaptchaLink;
import com.github.pesennik.component.parsley.EmailJsValidator;
import com.github.pesennik.component.parsley.LoginJsValidator;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.model.User;
import com.github.pesennik.page.HomePage;
import com.github.pesennik.util.RegistrationUtils;
import com.github.pesennik.util.TextUtils;
import com.github.pesennik.util.UDate;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.ValidatorUtils;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;

@MountPath("/registration")
public class RegistrationPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(RegistrationPage.class);

    public RegistrationPage() {
        if (UserSession.get().isSignedIn()) {
            throw new RestartResponseException(new PageProvider(HomePage.class), NEVER_REDIRECT);
        }

        Form form = new Form("register_form");
        form.setOutputMarkupId(true);
        add(form);

        WebMarkupContainer fieldsBlock = new WebMarkupContainer("fields_block");
        fieldsBlock.setOutputMarkupId(true);
        form.add(fieldsBlock);

        fieldsBlock.add(new SocialLoginPanel("socials_panel"));

        Feedback feedback = new Feedback("feedback");
        form.add(feedback);

        CaptchaField captchaField = new CaptchaField("captcha_value");
        captchaField.add(new RequiredFieldJsValidator());
        fieldsBlock.add(captchaField);

        TextField<String> loginField = new TextField<>("login_field", Model.of(""));
        loginField.add(new LoginJsValidator());
        fieldsBlock.add(loginField);

        TextField<String> emailField = new TextField<>("email_field", Model.of(""));
        emailField.add(new EmailJsValidator());
        fieldsBlock.add(emailField);

        PasswordField password1Field = new PasswordField("password1_field", Model.of(""));
        password1Field.add(new PasswordJsValidator());
        fieldsBlock.add(password1Field);

        PasswordField password2Field = new PasswordField("password2_field", Model.of(""));
        password2Field.add(new PasswordJsValidator());
        fieldsBlock.add(password2Field);

        Image captchaImage = new NonCachingImage("captcha_image", captchaField.getCaptchaImageResource());
        captchaImage.setOutputMarkupId(true);
        fieldsBlock.add(captchaImage);

        fieldsBlock.add(new RefreshCaptchaLink("change_captcha", captchaField, captchaImage));

        fieldsBlock.add(new ValidatingJsAjaxSubmitLink("submit", form) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                feedback.reset(target);

                try {
                    String captcha = captchaField.getUserText();
                    if (!captchaField.getOriginalText().equals(captcha)) {
                        feedback.error("Некорректный код!");
                        return;
                    }

                    UsersDbi dbi = Context.getUsersDbi();
                    String login = TextUtils.nonNull(loginField.getModelObject()).trim();
                    if (TextUtils.isEmpty(login)) {
                        feedback.error("Не указано имя пользователя!");
                        return;
                    }
                    if (!ValidatorUtils.isValidLogin(login)) {
                        feedback.error("Недопустимое имя пользователя! Используйте буквы, цифры, пробел и символы '+-_-.'");
                        return;
                    }
                    User user = dbi.getUserByLogin(login);
                    if (user != null) {
                        feedback.error("Пользователь с таким именем уже существует!");
                        return;
                    }
                    String email = emailField.getModelObject();
                    if (TextUtils.isEmpty(email)) {
                        feedback.error("Необходимо указать email!");
                        return;
                    }
                    if (!ValidatorUtils.isValidEmail(email)) {
                        feedback.error("Некорректный формат email!");
                        return;
                    }
                    user = dbi.getUserByEmail(email);
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

                    user = new User();
                    user.lastLoginDate = UDate.now();
                    user.registrationDate = UDate.now();
                    user.email = email;
                    user.login = login;
                    user.passwordHash = UserSessionUtils.password2Hash(password1);
                    user.settings = UserSettings.get().toString();
                    dbi.createUser(user);

                    RegistrationUtils.sendVerificationEmail(user, RegistrationUtils.createEmailVerification(user));

                    fieldsBlock.setVisible(false);
                    target.add(fieldsBlock);

                    feedback.success("На почтовый адрес «" + user.email + "»  выслано письмо с кодом активации. Используйте его, чтобы активировать учетную запись.");
                } catch (Exception e) {
                    log.error("Error during user registration!", e);
                    feedback.error("При отправке письма с кодом активации произошла ошибка. Пожалуйста, сообщите администратору сайта!");
                }
            }
        });
    }

}
