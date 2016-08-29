package com.github.pesennik.component.signin;

import com.github.pesennik.Context;
import com.github.pesennik.UserSession;
import com.github.pesennik.component.HomePage;
import com.github.pesennik.component.form.PasswordField;
import com.github.pesennik.component.form.InputField;
import com.github.pesennik.component.parsley.LoginJsValidator;
import com.github.pesennik.component.parsley.ParsleyUtils;
import com.github.pesennik.component.parsley.PasswordJsValidator;
import com.github.pesennik.component.parsley.ValidatingJsAjaxSubmitLink;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.model.User;
import com.github.pesennik.component.signin.ForgotPasswordPage;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.WebUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class LoginPanel extends Panel {

    public LoginPanel(String id) {
        super(id);

        if (UserSession.get().isSignedIn()) {
            setVisible(false);
            return;
        }

        Form form = new Form("login_form");
        form.setOutputMarkupId(true);
        add(form);

        form.add(new BookmarkablePageLink<WebPage>("restore_link", ForgotPasswordPage.class));

        WebMarkupContainer loginError = new WebMarkupContainer("login_error");
        form.add(loginError);
        InputField emailOrLoginField = new InputField("email_or_login_field");
        emailOrLoginField.add(new LoginJsValidator(loginError));
        form.add(emailOrLoginField);

        WebMarkupContainer passwordError = new WebMarkupContainer("password_error");
        form.add(passwordError);
        PasswordField passwordField = new PasswordField("password_field", Model.of(""));
        passwordField.add(new PasswordJsValidator(passwordError));
        form.add(passwordField);

        ValidatingJsAjaxSubmitLink loginButton = new ValidatingJsAjaxSubmitLink("login_link", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                UsersDbi dbi = Context.getUsersDbi();
                String emailOrLogin = emailOrLoginField.getInputString().trim();
                User user = dbi.getUserByLogin(emailOrLogin);
                if (user == null && emailOrLogin.contains("@")) {
                    user = dbi.getUserByEmail(emailOrLogin);
                }
                if (user == null) {
                    ParsleyUtils.addParsleyError(target, loginError, "Пользователь не найден!");
                    WebUtils.focus(target, emailOrLoginField);
                    return;
                }
                String password = passwordField.getModelObject();
                if (!UserSessionUtils.checkPassword(password, user.passwordHash)) {
                    ParsleyUtils.addParsleyError(target, passwordError, "Неверный пароль!");
                    WebUtils.focus(target, passwordError);
                    return;
                }
                if (user.terminationDate != null) {
                    ParsleyUtils.addParsleyError(target, loginError, "Пользователь заблокирован!");
                    WebUtils.focus(target, emailOrLoginField);
                    return;

                }
                UserSessionUtils.login(user);
                setResponsePage(HomePage.class);
            }
        };
        form.add(loginButton);

        WebUtils.addFocusOnEnter(emailOrLoginField, passwordField);
        WebUtils.addClickOnEnter(passwordField, loginButton);
    }

}
