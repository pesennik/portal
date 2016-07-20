package org.zametki.page.signin;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zametki.Context;
import org.zametki.UserSession;
import org.zametki.annotation.MountPath;
import org.zametki.component.Feedback;
import org.zametki.component.SocialLoginPanel;
import org.zametki.db.dbi.UsersDbi;
import org.zametki.model.User;
import org.zametki.model.VerificationRecord;
import org.zametki.model.VerificationRecordType;
import org.zametki.page.BasePage;
import org.zametki.page.HomePage;
import org.zametki.util.RegistrationUtils;
import org.zametki.util.TextUtils;
import org.zametki.util.UserSessionUtils;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;

@MountPath("/signup-activate/user/${" + ManualAccountActivationPage.LOGIN_PARAM + "}")
public class ManualAccountActivationPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(ManualAccountActivationPage.class);
    public static final String LOGIN_PARAM = "login";

    public ManualAccountActivationPage(PageParameters pp) {
        if (UserSession.get().isSignedIn()) {
            throw new RestartResponseException(new PageProvider(HomePage.class), NEVER_REDIRECT);
        }

        Form form = new Form("input_form");
        add(form);

        form.add(new SocialLoginPanel("socials_panel"));

        Feedback feedback = new Feedback("feedback");
        form.add(feedback);

        String login = pp.get(LOGIN_PARAM).toString();
        if (TextUtils.isEmpty(login)) {
            feedback.error("Отсутствует логин пользователя!");
        }

        form.add(new AjaxLink("resend_email") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                feedback.reset(target);

                if (TextUtils.isEmpty(login)) {
                    feedback.error("Отсутствует логин пользователя!");
                    return;
                }
                UsersDbi userDao = Context.getUsersDbi();

                User user = userDao.getUserByLogin(login);
                if (user == null) {
                    feedback.error("Пользователь не найден!");
                    return;
                }
                VerificationRecord verification = RegistrationUtils.createEmailVerification(user);
                try {
                    RegistrationUtils.sendVerificationEmail(user, verification);
                    feedback.success("Письмо c новым кодом отправлено! Проверьте почту.");
                } catch (Exception e) {
                    feedback.error("Не удалось отправить письмо, повторите попытку позже!");
                    log.error("Error during sending email! " + e.getMessage());
                }
            }
        });

        TextField<String> emailHash = new RequiredTextField<>("email_hash", Model.of(""));
        form.add(emailHash);

        form.add(new AjaxSubmitLink("submit_email_hash", form) {
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                feedback.reset(target);

                if (TextUtils.isEmpty(login)) {
                    feedback.error("Отсутствует логин пользователя!");
                    return;
                }

                UsersDbi userDao = Context.getUsersDbi();
                String hash = TextUtils.trim(emailHash.getModelObject());

                if (TextUtils.isEmpty(hash)) {
                    feedback.error("Введите код подтверждения регистрации");
                    return;
                }

                VerificationRecord r = userDao.getVerificationRecordByHashAndType(hash, VerificationRecordType.EmailValidation);
                if (r == null) {
                    feedback.error("Код проверки не найден!");
                    return;
                }

                User user = userDao.getUserById(r.userId);
                if (user == null) {
                    feedback.error("Пользователь не найден!");
                    return;
                }

                if (!user.login.equals(login)) {
                    feedback.error("Код проверки не найден!");
                    return;
                }

                if (!r.value.equals(user.email)) {
                    feedback.error("Код проверки должен быть использован для того же почтового адреса, на который был выслан!");
                    return;
                }

                user.emailChecked = true;
                userDao.updateEmailCheckedFlag(user, r);
                UserSessionUtils.login(user);

                setResponsePage(RegistrationCompletePage.class);
            }
        });
    }

    public static PageParameters getParameters(@NotNull String login) {
        return new PageParameters().add(LOGIN_PARAM, login);
    }
}
