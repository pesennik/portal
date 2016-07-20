package org.zametki.page;

import org.zametki.UserSession;
import org.zametki.page.signin.LoginPage;
import org.zametki.util.HttpUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.core.request.handler.PageProvider;

import javax.servlet.http.HttpServletRequest;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;

/**
 *
 */
public abstract class BaseUserPage extends BasePage {

    public BaseUserPage() {
        this(false);
    }

    public BaseUserPage(boolean skipRedirect) {
        if (!UserSession.get().isSignedIn() && !skipRedirect) {
            HttpUtils.saveLastViewedPage((HttpServletRequest) getRequest().getContainerRequest());
            throw new RestartResponseException(new PageProvider(LoginPage.class), NEVER_REDIRECT);
        }
    }

}
