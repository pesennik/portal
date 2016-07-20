package com.github.pesennik.page.signin;

import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.Feedback;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.model.User;
import com.github.pesennik.model.VerificationRecord;
import com.github.pesennik.model.VerificationRecordType;
import com.github.pesennik.page.BasePage;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

/**
 *
 */
@MountPath("/signup-activate/code/${" + ByLinkAccountActivationPage.HASH_PARAM + "}")
public class ByLinkAccountActivationPage extends BasePage {

    public static final String HASH_PARAM = "hash";

    public ByLinkAccountActivationPage(PageParameters pp) {
        UserSessionUtils.redirectHomeIfSignedIn();

        Feedback feedback = new Feedback("feedback");
        add(feedback);

        StringValue hashValue = pp.get(HASH_PARAM);
        if (hashValue == null || hashValue.isEmpty()) {
            feedback.error("Не указан код проверки!");
            return;
        }
        UsersDbi userDao = Context.getUsersDbi();
        VerificationRecord r = userDao.getVerificationRecordByHashAndType(hashValue.toString(), VerificationRecordType.EmailValidation);
        if (r == null) {
            feedback.error("Код проверки не найден!");
            return;
        }

        User user = userDao.getUserById(r.userId);
        if (user == null) {
            feedback.error("Пользователь не найден!");
            return;
        }

        if (!r.value.equalsIgnoreCase(user.email)) {
            feedback.error("Код проверки должен быть использован для того же почтового адреса, на который был выслан!");
            return;
        }

        user.emailChecked = true;
        userDao.updateEmailCheckedFlag(user, r);
        UserSessionUtils.login(user);

        setResponsePage(RegistrationCompletePage.class);
    }
}
