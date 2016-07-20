package org.zametki.page.signin;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.zametki.UserSession;
import org.zametki.annotation.MountPath;
import org.zametki.component.Feedback;
import org.zametki.page.BasePage;

@MountPath("/registration-complete")
public class RegistrationCompletePage extends BasePage {
    public RegistrationCompletePage() {
        Feedback feedback = new Feedback("feedback");
        add(feedback);

        WebMarkupContainer successBlock = new WebMarkupContainer("success");
        add(successBlock);

        if (!UserSession.get().isSignedIn()) {
            feedback.error("Пользователь не найден!");
            successBlock.setVisible(false);
            add(new Label("js_var-success", "<script>var success = false;</script>").setEscapeModelStrings(false));
        } else {
            add(new Label("js_var-success", "<script>var success = true;</script>").setEscapeModelStrings(false));
        }
    }
}
