package com.github.pesennik.page.signin;

import com.github.pesennik.Context;
import com.github.pesennik.Mounts;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.CaptchaField;
import com.github.pesennik.component.Feedback;
import com.github.pesennik.component.RefreshCaptchaLink;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.model.User;
import com.github.pesennik.model.VerificationRecord;
import com.github.pesennik.model.VerificationRecordType;
import com.github.pesennik.page.BasePage;
import com.github.pesennik.util.MailClient;
import com.github.pesennik.util.UserSessionUtils;
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
        TextField<String> emailField = new TextField<>("email_field", Model.of(""));
        form.add(captchaField);
        form.add(emailField);
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
                String email = emailField.getModelObject();
                User user = dao.getUserByEmail(email);
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
                        put("username", user.email);
                        put("link", Mounts.urlFor(ResetPasswordPage.class) + "?" + ResetPasswordPage.HASH_PARAM + "=" + resetRequest.hash);
                    }})));
        } catch (IOException e) {
            log.error("", e);
            //todo:
        }

    }

}


