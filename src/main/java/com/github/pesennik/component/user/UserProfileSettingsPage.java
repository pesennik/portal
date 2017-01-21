package com.github.pesennik.component.user;

import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.HomePage;
import com.github.pesennik.component.form.CaptchaField;
import com.github.pesennik.component.form.Feedback;
import com.github.pesennik.component.form.PasswordField;
import com.github.pesennik.component.form.RefreshCaptchaLink;
import com.github.pesennik.component.parsley.ParsleyUtils;
import com.github.pesennik.component.parsley.PasswordJsValidator;
import com.github.pesennik.component.parsley.RequiredFieldJsValidator;
import com.github.pesennik.component.parsley.ValidatingJsAjaxSubmitLink;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.model.User;
import com.github.pesennik.util.RegistrationUtils;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.WebUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;

@MountPath("/my/profile")
public class UserProfileSettingsPage extends BaseUserPage {

    public UserProfileSettingsPage() {
        setTitleAndDesc("Профиль пользователя", "Редактирование персонального профиля пользователя");
        WebMarkupContainer panel = new ContainerWithId("panel");
        add(panel);

        Feedback feedback = new Feedback("feedback");
        panel.add(feedback);

        BookmarkablePageLink homePageLink = new BookmarkablePageLink("home_link", HomePage.class);
        homePageLink.setVisible(false);
        panel.add(homePageLink);

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        panel.add(form);

        WebMarkupContainer oldPasswordError = new WebMarkupContainer("old_password_error");
        form.add(oldPasswordError);

        WebMarkupContainer password1Error = new WebMarkupContainer("password1_error");
        form.add(password1Error);

        WebMarkupContainer password2Error = new WebMarkupContainer("password2_error");
        form.add(password2Error);

        PasswordField oldPasswordField = new PasswordField("old_password_field", Model.of(""));
        oldPasswordField.add(new RequiredFieldJsValidator(oldPasswordError));
        form.add(oldPasswordField);

        PasswordField password1Field = new PasswordField("password1_field", Model.of(""));
        password1Field.add(new PasswordJsValidator(password1Error));
        form.add(password1Field);

        PasswordField password2Field = new PasswordField("password2_field", Model.of(""));
        password2Field.add(new PasswordJsValidator(password2Error));
        form.add(password2Field);

        WebMarkupContainer captchaError = new WebMarkupContainer("captcha_error");
        form.add(captchaError);
        CaptchaField captchaField = new CaptchaField("captcha_value");
        captchaField.add(new RequiredFieldJsValidator(captchaError));
        form.add(captchaField);

        Image captchaImage = new NonCachingImage("captcha_image", captchaField.getCaptchaImageResource());
        captchaImage.setOutputMarkupId(true);
        form.add(captchaImage);
        form.add(new RefreshCaptchaLink("change_captcha", captchaField, captchaImage));

        form.add(new ValidatingJsAjaxSubmitLink("update_link", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                feedback.reset(target);
                target.add(panel);

                String captcha = captchaField.getUserText();
                if (!captchaField.getOriginalText().equals(captcha)) {
                    ParsleyUtils.addParsleyError(target, captchaError, "Некорректный код!");
                    WebUtils.focus(target, captchaField);
                    return;
                }

                User user = WebUtils.getUserOrRedirectHome();
                String oldPassword = oldPasswordField.getModelObject();
                String oldPasswordHash = UserSessionUtils.password2Hash(oldPassword);
                if (!user.passwordHash.equals(oldPasswordHash)) {
                    ParsleyUtils.addParsleyError(target, oldPasswordError, "Неверный пароль");
                    WebUtils.focus(target, oldPasswordField);
                    return;
                }
                String password1 = password1Field.getModelObject();
                String password2 = password2Field.getModelObject();
                String err = RegistrationUtils.validatePassword(password1, password2);
                if (err != null) {
                    ParsleyUtils.addParsleyError(target, password1Error, err);
                    WebUtils.focus(target, password1Field);
                    return;
                }
                user.passwordHash = UserSessionUtils.password2Hash(password1);
                Context.getUsersDbi().updatePassword(user, null);
                feedback.success("Пароль изменен");
                homePageLink.setVisible(true);
                WebUtils.scrollTo(feedback, target);
                target.add(panel);
            }
        });
    }
}
