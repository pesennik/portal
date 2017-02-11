package com.github.pesennik.component.signin;

import com.github.pesennik.Constants;
import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.BasePage;
import com.github.pesennik.component.HomePage;
import com.github.pesennik.component.form.CaptchaField;
import com.github.pesennik.component.form.Feedback;
import com.github.pesennik.component.form.InputField;
import com.github.pesennik.component.form.RefreshCaptchaLink;
import com.github.pesennik.component.parsley.LoginJsValidator;
import com.github.pesennik.component.parsley.ParsleyUtils;
import com.github.pesennik.component.parsley.RequiredFieldJsValidator;
import com.github.pesennik.component.parsley.ValidatingJsAjaxSubmitLink;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.model.User;
import com.github.pesennik.model.VerificationRecord;
import com.github.pesennik.model.VerificationRecordType;
import com.github.pesennik.util.JSUtils;
import com.github.pesennik.util.MailClient;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.WebUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MountPath("/password/restore")
public class ForgotPasswordPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordPage.class);

    public ForgotPasswordPage() {
        UserSessionUtils.redirectHomeIfSignedIn();

        WebMarkupContainer panel = new ContainerWithId("panel");
        add(panel);

        Feedback feedback = new Feedback("feedback");
        panel.add(feedback);

        BookmarkablePageLink backHomeLink = new BookmarkablePageLink("back_home_link", HomePage.class);
        backHomeLink.setVisible(false);
        panel.add(backHomeLink);

        Form form = new Form("reset_from");
        panel.add(form);

        WebMarkupContainer loginError = new WebMarkupContainer("login_error");
        form.add(loginError);
        InputField emailOrLoginField = new InputField("email_or_login_field");
        emailOrLoginField.add(new LoginJsValidator(loginError));
        form.add(emailOrLoginField);

        WebMarkupContainer captchaError = new WebMarkupContainer("captcha_error");
        form.add(captchaError);
        CaptchaField captchaField = new CaptchaField("captcha_value");
        captchaField.add(new RequiredFieldJsValidator(captchaError));
        form.add(captchaField);

        Image captchaImage = new NonCachingImage("captcha_image", captchaField.getCaptchaImageResource());
        captchaImage.setOutputMarkupId(true);
        form.add(captchaImage);
        form.add(new RefreshCaptchaLink("change_captcha", captchaField, captchaImage));

        AjaxSubmitLink resetLink = new ValidatingJsAjaxSubmitLink("submit", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                feedback.reset(target);

                String captcha = captchaField.getUserText();
                if (!captchaField.getOriginalText().equals(captcha)) {
                    ParsleyUtils.addParsleyError(target, captchaError, "Некорректный код!");
                    JSUtils.focus(target, captchaField);
                    return;
                }

                UsersDbi dao = Context.getUsersDbi();
                String emailOrLogin = emailOrLoginField.getInputString();
                User user = dao.getUserByLogin(emailOrLogin);
                if (user == null) {
                    user = dao.getUserByEmail(emailOrLogin);
                    if (user == null) {
                        ParsleyUtils.addParsleyError(target, loginError, "Пользователь не найден!");
                        JSUtils.focus(target, emailOrLoginField);
                        return;
                    }
                }

                VerificationRecord resetRequest = new VerificationRecord(user, VerificationRecordType.PasswordReset, "");
                Context.getUsersDbi().createVerificationRecord(resetRequest);

                feedback.info("На почтовый адрес '" + user.email + "' было выслано письмо с инструкцией о том, как изменить пароль");
                form.setVisible(false);
                backHomeLink.setVisible(true);
                target.add(panel);
                try {
                    String url = WebUtils.getFullPageUrl(ResetPasswordPage.class, ResetPasswordPage.getPageParams(resetRequest.hash));
                    String subject = Constants.BRAND_NAME + " - восстановление пароля";
                    String body = "Имя Вашего пользователя: " + user.login + "\n" +
                            "Для того, чтобы изменить пароль, используйте следующую ссылку: " + url;
                    MailClient.sendMail(user.email, subject, body);
                } catch (Exception e) {
                    log.error("Error sending email", e);
                    feedback.error("Внутренняя ошибка! Пожалуйста сообщите на " + MailClient.SUPPORT_EMAIL);
                }
            }
        };
        form.add(resetLink);

        JSUtils.addFocusOnEnter(emailOrLoginField, captchaField);
        JSUtils.addClickOnEnter(captchaField, resetLink);
    }

}


