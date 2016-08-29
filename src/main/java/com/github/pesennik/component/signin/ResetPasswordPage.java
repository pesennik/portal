package com.github.pesennik.component.signin;

import com.github.pesennik.Context;
import com.github.pesennik.UserSession;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.form.Feedback;
import com.github.pesennik.component.form.PasswordField;
import com.github.pesennik.component.parsley.ParsleyUtils;
import com.github.pesennik.component.parsley.PasswordJsValidator;
import com.github.pesennik.model.User;
import com.github.pesennik.model.VerificationRecord;
import com.github.pesennik.model.VerificationRecordId;
import com.github.pesennik.model.VerificationRecordType;
import com.github.pesennik.component.BasePage;
import com.github.pesennik.component.HomePage;
import com.github.pesennik.util.DigestUtils;
import com.github.pesennik.util.RegistrationUtils;
import com.github.pesennik.util.UDate;
import com.github.pesennik.util.UserSessionUtils;
import com.github.pesennik.util.WebUtils;
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
        if (hash.isEmpty() || !DigestUtils.isValidUUID(hash.toString())) {
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

        BookmarkablePageLink loginLink = new BookmarkablePageLink("home_link", HomePage.class);
        loginLink.setVisible(false);
        resetBlock.add(loginLink);

        Form form = new Form("reset_from");
        form.setOutputMarkupId(true);
        resetBlock.add(form);

        WebMarkupContainer password1Error = new WebMarkupContainer("password1_error");
        form.add(password1Error);

        WebMarkupContainer password2Error = new WebMarkupContainer("password2_error");
        form.add(password2Error);


        PasswordField password1Field = new PasswordField("password1_field", Model.of(""));
        password1Field.add(new PasswordJsValidator(password1Error));
        form.add(password1Field);

        PasswordField password2Field = new PasswordField("password2_field", Model.of(""));
        password2Field.add(new PasswordJsValidator(password2Error));
        form.add(password2Field);


        VerificationRecordId recordId = r.id;
        form.add(new AjaxSubmitLink("submit_link", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                feedback.reset(target);
                VerificationRecord r1 = Context.getUsersDbi().getVerificationRecordById(recordId);
                if (r1 == null) {
                    ParsleyUtils.addParsleyError(target, password1Error, "Запрос на изменение пароля не найден. Попробуйте создать его снова!");
                    WebUtils.focus(target, password1Field);
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
                User user = Context.getUsersDbi().getUserById(r1.userId);
                if (user == null) {
                    feedback.error("Пользователь не найден!");
                    return;
                }
                user.passwordHash = UserSessionUtils.password2Hash(password1);
                Context.getUsersDbi().updatePassword(user, r1);
                feedback.success("Пароль изменен. Используйте новый пароль чтобы войти в систему! Ваш email: " + user.email);
                form.setVisible(false);
                loginLink.setVisible(true);
                target.add(resetBlock);
            }
        });
    }
}

