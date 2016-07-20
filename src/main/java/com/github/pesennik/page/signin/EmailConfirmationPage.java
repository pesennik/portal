package com.github.pesennik.page.signin;

import com.github.pesennik.Context;
import com.github.pesennik.model.User;
import com.github.pesennik.model.VerificationRecord;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import com.github.pesennik.UserSession;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.Feedback;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.model.VerificationRecordType;
import com.github.pesennik.page.BaseUserPage;

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

