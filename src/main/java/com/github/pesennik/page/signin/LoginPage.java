package com.github.pesennik.page.signin;

import com.github.pesennik.UserSession;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.LoginPanel;
import com.github.pesennik.page.BasePage;
import com.github.pesennik.page.HomePage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;


@MountPath(value = "/signin", alt = "/login")
public class LoginPage extends BasePage {
    public LoginPage() {
        if (UserSession.get().isSignedIn()) {
            throw new RestartResponseException(new PageProvider(HomePage.class), NEVER_REDIRECT);
        }

        add(new LoginPanel("login_panel"));
    }
}

