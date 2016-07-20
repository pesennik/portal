package org.zametki.page.signin;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.MapModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zametki.Context;
import org.zametki.Mounts;
import org.zametki.annotation.MountPath;
import org.zametki.component.CaptchaField;
import org.zametki.component.Feedback;
import org.zametki.component.RefreshCaptchaLink;
import org.zametki.db.dbi.UsersDbi;
import org.zametki.model.User;
import org.zametki.model.VerificationRecord;
import org.zametki.model.VerificationRecordType;
import org.zametki.page.BasePage;
import org.zametki.util.MailClient;
import org.zametki.util.UserSessionUtils;

import java.io.IOException;
import java.util.HashMap;

@MountPath("/password/restore")
public class ForgotPasswordPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordPage.class);

    public ForgotPasswordPage() {
        UserSessionUtils.redirectHomeIfSignedIn();

        Feedback feedback = new Feedback("feedback");
        add(feedback);

        Form form = new Form("reset_from");
        add(form);

        CaptchaField captchaField = new CaptchaField("captcha_value");
        TextField<String> loginOrEmailField = new TextField<>("login_or_email_field", Model.of(""));
        form.add(captchaField);
        form.add(loginOrEmailField);
        Image captchaImage = new NonCachingImage("captcha_image", captchaField.getCaptchaImageResource());
        captchaImage.setOutputMarkupId(true);
        form.add(captchaImage);
        form.add(new RefreshCaptchaLink("change_captcha", captchaField, captchaImage));

        form.add(new AjaxSubmitLink("submit", form) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                feedback.reset(target);

                String captcha = captchaField.getUserText();
                if (!captchaField.getOriginalText().equals(captcha)) {
                    feedback.error("Некорректный код!");
                    return;
                }
                UsersDbi dao = Context.getUsersDbi();
                String loginOrEmail = loginOrEmailField.getModelObject();
                User user = dao.getUserByLoginOrEmail(loginOrEmail);
                if (user == null) {
                    user = dao.getUserByEmail(loginOrEmail);
                }
                if (user == null) {
                    feedback.error("Пользователь не найден!");
                    return;
                }
                VerificationRecord resetRequest = new VerificationRecord(user, VerificationRecordType.PasswordReset, "");
                Context.getUsersDbi().createVerificationRecord(resetRequest);

                feedback.info("На почтовый адрес '" + user.email + "' было выслано письмо с инструкцией о том, как изменить пароль");
                sendEmail(user, resetRequest);
            }
        });
    }

    private void sendEmail(User user, VerificationRecord resetRequest) {
        try {
            MailClient.sendMail(user.email, getString("info_password_reset_email_subject"),
                    getString("info_password_reset_email_body", new MapModel<>(new HashMap<String, String>() {{
                        put("username", user.login);
                        put("link", Mounts.urlFor(ResetPasswordPage.class) + "?" + ResetPasswordPage.HASH_PARAM + "=" + resetRequest.hash);
                    }})));
        } catch (IOException e) {
            log.error("", e);
            //todo:
        }

    }

}


