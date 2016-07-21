package com.github.pesennik.page.signin;

import com.github.pesennik.UserSession;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.model.TopMenu;
import com.github.pesennik.page.BasePage;
import com.github.pesennik.page.HomePage;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.core.request.handler.PageProvider;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;

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
