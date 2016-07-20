package com.github.pesennik.page.signin;

import com.github.pesennik.component.Feedback;
import com.github.pesennik.page.BasePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import com.github.pesennik.UserSession;
import com.github.pesennik.annotation.MountPath;

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
