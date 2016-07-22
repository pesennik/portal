package com.github.pesennik.page;

import com.github.pesennik.UserSession;
import com.github.pesennik.page.signin.LoginPage;
import com.github.pesennik.util.HttpUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.core.request.handler.PageProvider;

import javax.servlet.http.HttpServletRequest;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;

/**
 * This page is visible only for signed in users;
 */
public abstract class BaseUserPage extends BasePage {

    public BaseUserPage() {
        this(false);
    }

    public BaseUserPage(boolean skipRedirect) {
        if (!UserSession.get().isSignedIn() && !skipRedirect) {
            throw new RestartResponseException(new PageProvider(LoginPage.class), NEVER_REDIRECT);
        }
    }

}
