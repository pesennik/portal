package com.github.pesennik.page;

import com.github.pesennik.UserSession;
import com.github.pesennik.page.signin.LoginPage;
import org.apache.wicket.RestartResponseException;

/**
 * This page is visible only for signed in users;
 */
public abstract class BaseUserPage extends BasePage {

    public BaseUserPage() {
        this(false);
    }

    public BaseUserPage(boolean skipRedirect) {
        if (!UserSession.get().isSignedIn() && !skipRedirect) {
            throw new RestartResponseException(LoginPage.class);
        }
    }

}
