package com.github.pesennik.component.user;

import com.github.pesennik.UserSession;
import com.github.pesennik.component.BasePage;
import com.github.pesennik.component.HomePage;
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
            throw new RestartResponseException(HomePage.class);
        }
    }

}
