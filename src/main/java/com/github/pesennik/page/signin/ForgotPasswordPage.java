package com.github.pesennik.page.signin;

import com.github.pesennik.Constants;
import com.github.pesennik.Context;
import com.github.pesennik.Mounts;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.CaptchaField;
import com.github.pesennik.component.Feedback;
import com.github.pesennik.component.InputField;
import com.github.pesennik.component.RefreshCaptchaLink;
import com.github.pesennik.component.parsley.LoginJsValidator;
import com.github.pesennik.component.parsley.ParsleyUtils;
import com.github.pesennik.component.parsley.RequiredFieldJsValidator;
import com.github.pesennik.component.parsley.ValidatingJsAjaxSubmitLink;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.model.User;
import com.github.pesennik.model.VerificationRecord;
import com.github.pesennik.model.VerificationRecordType;
import com.github.pesennik.page.BasePage;
import com.github.pesennik.util.MailClient;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.WebUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MountPath("/password/restore")
public class ForgotPasswordPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordPage.class);

    public ForgotPasswordPage() {
        UserSessionUtils.redirectHomeIfSignedIn();

        Feedback feedback = new Feedback("feedback");
        add(feedback);

        Form form = new Form("reset_from");
        add(form);

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

        ValidatingJsAjaxSubmitLink resetLink = new ValidatingJsAjaxSubmitLink("submit", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                feedback.reset(target);

                UsersDbi dao = Context.getUsersDbi();
                String emailOrLogin = emailOrLoginField.getInputString();
                User user = dao.getUserByLogin(emailOrLogin);
                if (user == null) {
                    user = dao.getUserByEmail(emailOrLogin);
                    if (user == null) {
                        ParsleyUtils.addParsleyError(target, loginError, "Пользователь не найден!");
                        WebUtils.focus(target, emailOrLoginField);
                        return;
                    }
                }

                String captcha = captchaField.getUserText();
                if (!captchaField.getOriginalText().equals(captcha)) {
                    ParsleyUtils.addParsleyError(target, captchaError, "Некорректный код!");
                    WebUtils.focus(target, captchaField);
                    return;
                }

                VerificationRecord resetRequest = new VerificationRecord(user, VerificationRecordType.PasswordReset, "");
                Context.getUsersDbi().createVerificationRecord(resetRequest);

                feedback.info("На почтовый адрес '" + user.email + "' было выслано письмо с инструкцией о том, как изменить пароль");
                try {
                    MailClient.sendMail(user.email, Constants.BRAND_NAME + " - восстановление пароля",
                            "Имя вашего пользователя: " + user.login + "\n" +
                                    "Для того, чтобы изменить пароль, используйте следующую ссылку: " +
                                    Mounts.urlFor(ResetPasswordPage.class) + "?" + ResetPasswordPage.HASH_PARAM + "=" + resetRequest.hash);

                } catch (Exception e) {
                    log.error("", e);
                    feedback.error("Внутренняя ошибка! Пожалуйста сообщите на " + MailClient.SUPPORT);
                }
            }
        };
        form.add(resetLink);

        WebUtils.addFocusOnEnter(emailOrLoginField, captchaField);
        WebUtils.addClickOnEnter(captchaField, resetLink);

    }

}


