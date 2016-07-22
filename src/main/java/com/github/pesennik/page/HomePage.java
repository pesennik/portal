package com.github.pesennik.page;

import com.github.pesennik.UserSession;
import com.github.pesennik.component.LoginPanel;
import com.github.pesennik.model.User;
import com.github.pesennik.page.signin.LogoutPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class HomePage extends BasePage {

    public HomePage() {
        User user = UserSession.get().getUser();

        WebMarkupContainer notLoggedInBlock = new WebMarkupContainer("not_logged_in_block");
        notLoggedInBlock.setVisible(user == null);
        add(notLoggedInBlock);
        if (notLoggedInBlock.isVisible()) {
            notLoggedInBlock.add(new LoginPanel("login_panel"));
        }

        WebMarkupContainer loggedInBlock = new WebMarkupContainer("logged_in_block");
        loggedInBlock.setVisible(user != null);
        add(loggedInBlock);
        if (loggedInBlock.isVisible()) {
            loggedInBlock.add(new BookmarkablePageLink("logout_link", LogoutPage.class));
        }
    }

}
