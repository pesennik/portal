package com.github.pesennik.page.signin;

import com.github.pesennik.Context;
import com.github.pesennik.UserSession;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.Feedback;
import com.github.pesennik.component.PasswordField;
import com.github.pesennik.component.SocialLoginPanel;
import com.github.pesennik.component.parsley.EmailJsValidator;
import com.github.pesennik.component.parsley.PasswordJsValidator;
import com.github.pesennik.component.parsley.ValidatingJsAjaxSubmitLink;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.model.User;
import com.github.pesennik.page.BasePage;
import com.github.pesennik.page.HomePage;
import com.github.pesennik.util.HttpUtils;
import com.github.pesennik.util.TextUtils;
import com.github.pesennik.util.UserSessionUtils;
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

        TextField<String> emailField = new TextField<>("email_field", Model.of(""));
        emailField.add(new EmailJsValidator());
        form.add(emailField);
        PasswordField passwordField = new PasswordField("password_field", Model.of(""));
        passwordField.add(new PasswordJsValidator());
        form.add(passwordField);

        form.add(new ValidatingJsAjaxSubmitLink("login_link", form) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                feedback.reset(target);

                UsersDbi dbi = Context.getUsersDbi();
                String login = TextUtils.trim(emailField.getModelObject());
                User user = dbi.getUserByEmail(login);
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
                UserSessionUtils.login(user);
                HttpUtils.redirectToLastViewedPage(LoginPage.this);
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

