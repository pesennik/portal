package org.zametki.page.signin;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.zametki.Context;
import org.zametki.UserSession;
import org.zametki.annotation.MountPath;
import org.zametki.component.Feedback;
import org.zametki.db.dbi.UsersDbi;
import org.zametki.model.User;
import org.zametki.model.VerificationRecord;
import org.zametki.model.VerificationRecordType;
import org.zametki.page.BaseUserPage;

/**
 *
 */
@MountPath("/registration/email-confirmation")
public class EmailConfirmationPage extends BaseUserPage {

    public static final String EMAIL_HASH_PARAM = "hash";

    public EmailConfirmationPage(PageParameters pp) {
        Feedback feedback = new Feedback("feedback");
        add(feedback);

        //TODO: add(new BookmarkablePageLink<>("profile_link", UserProfileSettingsPage.class));
        User user = UserSession.get().getUser();
        assert user != null;
        StringValue hashValue = pp.get(EMAIL_HASH_PARAM);
        if (hashValue == null || hashValue.isEmpty() || user.emailChecked) {
            feedback.error("Не указан код проверки!");
            return;
        }
        UsersDbi userStorage = Context.getUsersDbi();
        VerificationRecord r = userStorage.getVerificationRecordByHashAndType(hashValue.toString(), VerificationRecordType.EmailValidation);
        if (r == null || !r.userId.equals(user.id)) {
            feedback.error("Код проверки не найден!");
            return;
        }
        if (!r.value.equals(user.email)) {
            feedback.error("Код проверки должен быть использован для того же почтового адреса, на который был выслан!");
            return;
        }

        user.emailChecked = true;
        userStorage.updateEmailCheckedFlag(user, r);

        feedback.success("Email подтвержден!");
    }

}

