package org.zametki.page.signin;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.zametki.Context;
import org.zametki.UserSession;
import org.zametki.annotation.MountPath;
import org.zametki.component.Feedback;
import org.zametki.component.PasswordField;
import org.zametki.model.User;
import org.zametki.model.VerificationRecord;
import org.zametki.model.VerificationRecordId;
import org.zametki.model.VerificationRecordType;
import org.zametki.page.BasePage;
import org.zametki.page.HomePage;
import org.zametki.util.HashUtils;
import org.zametki.util.RegistrationUtils;
import org.zametki.util.UDate;
import org.zametki.util.UserSessionUtils;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;

@MountPath("/password/reset")
public class ResetPasswordPage extends BasePage {

    public static final String HASH_PARAM = "id";
    public static final int REQUEST_VALID_HOURS = 24;

    public ResetPasswordPage(PageParameters params) {
        if (UserSession.get().isSignedIn()) {
            throw new RestartResponseException(new PageProvider(HomePage.class), NEVER_REDIRECT);
        }
        Feedback feedback = new Feedback("feedback");
        add(feedback);

        WebMarkupContainer resetBlock = new WebMarkupContainer("reset_block");
        resetBlock.setOutputMarkupId(true);
        add(resetBlock);

        BookmarkablePageLink newRequestLink = new BookmarkablePageLink("new_request_link", ForgotPasswordPage.class);
        add(newRequestLink);

        StringValue hash = params.get(HASH_PARAM);
        if (hash.isEmpty() || !HashUtils.isValidHash(hash.toString())) {
            resetBlock.setVisible(false);
            feedback.error("Некорректный код!");
            return;
        }
        VerificationRecord r = Context.getUsersDbi().getVerificationRecordByHashAndType(hash.toString(), VerificationRecordType.PasswordReset);
        if (r == null) {
            resetBlock.setVisible(false);
            feedback.error("Запрос на изменение пароля не найден. Попробуйте создать его снова.");
            return;
        }

        if (r.verificationDate != null) {
            resetBlock.setVisible(false);
            feedback.error("Код уже был использован!");
            return;
        }

        boolean expired = r.creationDate.isBefore(UDate.now().minusHours(REQUEST_VALID_HOURS));
        if (expired) {
            resetBlock.setVisible(false);
            feedback.error("Время действия кода истекло. Создайте запрос снова!");
            return;
        }
        newRequestLink.setVisible(false);

        BookmarkablePageLink loginLink = new BookmarkablePageLink("login_link", LoginPage.class);
        loginLink.setVisible(false);
        resetBlock.add(loginLink);

        Form form = new Form("reset_from");
        form.setOutputMarkupId(true);
        resetBlock.add(form);

        PasswordField password1Field = new PasswordField("password1_field", Model.of(""));
        form.add(password1Field);

        PasswordField password2Field = new PasswordField("password2_field", Model.of(""));
        form.add(password2Field);

        VerificationRecordId recordId = r.id;
        form.add(new AjaxSubmitLink("submit_link", form) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                feedback.reset(target);
                VerificationRecord r1 = Context.getUsersDbi().getVerificationRecordById(recordId);
                if (r1 == null) {
                    feedback.error("Запрос на изменение пароля не найден. Попробуйте создать его снова.");
                    return;
                }
                String password1 = password1Field.getModelObject();
                String password2 = password2Field.getModelObject();
                String err = RegistrationUtils.validatePassword(password1, password2);
                if (err != null) {
                    feedback.error(err);
                    return;
                }
                User user = Context.getUsersDbi().getUserById(r1.userId);
                if (user == null) {
                    feedback.error("Пользователь не найден!");
                    return;
                }
                user.passwordHash = UserSessionUtils.password2Hash(password1);
                Context.getUsersDbi().updatePassword(user, r1);
                feedback.info("Пароль изменен. Используйте новый пароль чтобы войти в систему! Ваш логин: " + user.login);
                form.setVisible(false);
                loginLink.setVisible(true);
                target.add(resetBlock);
            }
        });
    }
}


