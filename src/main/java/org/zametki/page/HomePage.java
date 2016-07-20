package org.zametki.page;

import org.zametki.model.TopMenu;
import org.zametki.UserSession;
import org.zametki.model.User;
import org.zametki.page.signin.LoginPage;
import org.zametki.page.signin.LogoutPage;
import org.zametki.page.signin.RegistrationPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 *
 */
public class HomePage extends BasePage {

    public HomePage() {
        super(TopMenu.HOME);

        User user = UserSession.get().getUser();

        WebMarkupContainer notLoggedInBlock = new WebMarkupContainer("not_logged_in_block");
        notLoggedInBlock.setVisible(user == null);
        add(notLoggedInBlock);
        if (notLoggedInBlock.isVisible()) {
            notLoggedInBlock.add(new BookmarkablePageLink("register_link", RegistrationPage.class));
            notLoggedInBlock.add(new BookmarkablePageLink("login_link", LoginPage.class));
        }

        WebMarkupContainer loggedInBlock = new WebMarkupContainer("logged_in_block");
        loggedInBlock.setVisible(user != null);
        add(loggedInBlock);
        if (loggedInBlock.isVisible()) {
            loggedInBlock.add(new BookmarkablePageLink("logout_link", LogoutPage.class));
        }
    }

}
