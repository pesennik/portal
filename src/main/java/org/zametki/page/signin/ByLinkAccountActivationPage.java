package org.zametki.page.signin;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.zametki.Context;
import org.zametki.annotation.MountPath;
import org.zametki.component.Feedback;
import org.zametki.db.dbi.UsersDbi;
import org.zametki.model.User;
import org.zametki.model.VerificationRecord;
import org.zametki.model.VerificationRecordType;
import org.zametki.page.BasePage;
import org.zametki.util.UserSessionUtils;

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
