package com.github.pesennik.page.signin;


import com.github.pesennik.Context;
import com.github.pesennik.UserSession;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.Feedback;
import com.github.pesennik.component.InputField;
import com.github.pesennik.component.PasswordField;
import com.github.pesennik.component.parsley.EmailJsValidator;
import com.github.pesennik.component.parsley.LoginJsValidator;
import com.github.pesennik.component.parsley.PasswordJsValidator;
import com.github.pesennik.component.parsley.ValidatingJsAjaxSubmitLink;
import com.github.pesennik.model.User;
import com.github.pesennik.page.BasePage;
import com.github.pesennik.page.HomePage;
import com.github.pesennik.util.RegistrationUtils;
import com.github.pesennik.util.TextUtils;
import com.github.pesennik.util.UDate;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.ValidatorUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


        Feedback feedback = new Feedback("feedback");
        form.add(feedback);

        InputField loginField = new InputField("login_field");
        loginField.add(new LoginJsValidator());
        fieldsBlock.add(loginField);


        InputField emailField = new InputField("email_field");
        emailField.add(new EmailJsValidator());
        fieldsBlock.add(emailField);

        PasswordField password1Field = new PasswordField("password1_field", Model.of(""));
        password1Field.add(new PasswordJsValidator());
        fieldsBlock.add(password1Field);

        PasswordField password2Field = new PasswordField("password2_field", Model.of(""));
        password2Field.add(new PasswordJsValidator());
        fieldsBlock.add(password2Field);

        fieldsBlock.add(new ValidatingJsAjaxSubmitLink("submit", form) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                feedback.reset(target);
                if (UserSession.get().isSignedIn()) {
                    throw new RestartResponseException(new PageProvider(HomePage.class), NEVER_REDIRECT);
                }

                String login = emailField.getInputString();
                if (ValidatorUtils.isValidLogin(login)) {
                    feedback.error("Некорректный псевдоним");
                    return;
                }

                String email = emailField.getInputString();
                if (TextUtils.isEmpty(email)) {
                    feedback.error("Необходимо указать email!");
                    return;
                }
                User user = Context.getUsersDbi().getUserByEmail(email);
                if (user != null) {
                    feedback.error("Пользователь с таким email уже существует!");
                    return;
                }
                if (!ValidatorUtils.isValidEmail(email)) {
                    feedback.error("Некорректный формат email!");
                    return;
                }
                user = Context.getUsersDbi().getUserByEmail(email);
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
                user.login = login;
                user.lastLoginDate = UDate.now();
                user.registrationDate = UDate.now();
                user.email = email;
                user.passwordHash = UserSessionUtils.password2Hash(password1);
                Context.getUsersDbi().createUser(user);

                fieldsBlock.setVisible(false);
                target.add(fieldsBlock);
                UserSession.get().setUser(user);
                setResponsePage(HomePage.class);
                try {
                    RegistrationUtils.sendWelcomeEmail(user, password1);
                } catch (Exception e) {
                    log.error("Error during user registration!", e);
                    feedback.error("При отправке письма с кодом активации произошла ошибка. Пожалуйста, сообщите администратору сайта!");
                }
            }
        });
    }

}
