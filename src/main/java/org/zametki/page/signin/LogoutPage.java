package org.zametki.page.signin;

import org.zametki.model.TopMenu;
import org.zametki.UserSession;
import org.zametki.annotation.MountPath;
import org.zametki.page.BasePage;
import org.zametki.page.HomePage;
import org.zametki.util.UserSessionUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.core.request.handler.PageProvider;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;

/**
 */
@MountPath(value = "/signout", alt = "/logout")
public class LogoutPage extends BasePage {
    public LogoutPage() {
        super(TopMenu.HOME);
    }

    @Override
    protected void pageInitCallback() {
        if (!UserSession.get().isSignedIn()) {
            throw new RestartResponseException(new PageProvider(HomePage.class), NEVER_REDIRECT);
        }
        UserSessionUtils.logout();
    }
}
