package org.zametki.page.signin;

import org.zametki.Context;
import org.zametki.UserSession;
import org.zametki.annotation.MountPath;
import org.zametki.component.Feedback;
import org.zametki.component.PasswordField;
import org.zametki.component.SocialLoginPanel;
import org.zametki.component.parsley.LoginJsValidator;
import org.zametki.component.parsley.PasswordJsValidator;
import org.zametki.component.parsley.ValidatingJsAjaxSubmitLink;
import org.zametki.db.dbi.UsersDbi;
import org.zametki.model.User;
import org.zametki.page.BasePage;
import org.zametki.page.HomePage;
import org.zametki.util.HttpUtils;
import org.zametki.util.TextUtils;
import org.zametki.util.UserSessionUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.jetbrains.annotations.NotNull;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;


/**
 */
@MountPath(value = "/signin", alt = "/login")
public class LoginPage extends BasePage {
    public static final String ERROR_PARAM = "error";
    private final Feedback feedback = new Feedback("feedback");

    public LoginPage(PageParameters params) {
        if (UserSession.get().isSignedIn()) {
            throw new RestartResponseException(new PageProvider(HomePage.class), NEVER_REDIRECT);
        }

        StringValue err = params.get(ERROR_PARAM);
        if (!err.isEmpty()) {
            feedback.error(err.toString());
        }

        Form form = new Form("login_form");
        add(form);

        form.add(new SocialLoginPanel("socials_panel"));

        form.add(feedback);
        form.add(new BookmarkablePageLink<WebPage>("restore_link", ForgotPasswordPage.class));
        form.add(new BookmarkablePageLink<WebPage>("signup_link", RegistrationPage.class));

        TextField<String> loginField = new TextField<>("login_field", Model.of(""));
        loginField.add(new LoginJsValidator());
        form.add(loginField);
        PasswordField passwordField = new PasswordField("password_field", Model.of(""));
        passwordField.add(new PasswordJsValidator());
        form.add(passwordField);

        form.add(new ValidatingJsAjaxSubmitLink("login_link", form) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                feedback.reset(target);

                UsersDbi dbi = Context.getUsersDbi();
                String login = TextUtils.trim(loginField.getModelObject());
                User user = dbi.getUserByLogin(login);
                if (user == null) {
                    feedback.error("Пользователь с таким именем не найден!");
                    return;
                }
                String password = passwordField.getModelObject();
                if (!UserSessionUtils.checkPassword(password, user.passwordHash)) {
                    feedback.error("Неверный пароль!");
                    return;
                }
                if (user.terminationDate != null) {
                    feedback.error("Пользователь заблокирован!");
                    return;

                }
                // do login for old user and check isEmailChecked for new user
                if (user.emailChecked) {
                    UserSessionUtils.login(user);
                    HttpUtils.redirectToLastViewedPage(LoginPage.this);
                } else {
                    setResponsePage(ManualAccountActivationPage.class, ManualAccountActivationPage.getParameters(user.login));
                }
            }
        });
    }

    @NotNull
    public static PageParameters errorParams(@NotNull String err) {
        PageParameters pp = new PageParameters();
        pp.set(ERROR_PARAM, err);
        return pp;
    }

}

