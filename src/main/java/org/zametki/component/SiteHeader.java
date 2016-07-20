package org.zametki.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.Nullable;
import org.zametki.Constants;
import org.zametki.UserSession;
import org.zametki.behavior.ClassAppender;
import org.zametki.model.TopMenu;
import org.zametki.model.User;
import org.zametki.page.HomePage;
import org.zametki.page.signin.LoginPage;
import org.zametki.page.signin.LogoutPage;
import org.zametki.page.signin.RegistrationPage;

/**
 *
 */
public class SiteHeader extends Panel {

    public SiteHeader(String id, @Nullable TopMenu menu) {
        super(id);

        BookmarkablePageLink brandLink = new BookmarkablePageLink("brand_link", HomePage.class);
        brandLink.add(new Label("brand_name", Constants.BRAND_NAME));
        add(brandLink);

        WebMarkupContainer homeMenu = new WebMarkupContainer("home_menu");
        homeMenu.add(new BookmarkablePageLink("home_page_link", HomePage.class));
        addActiveClass(homeMenu, menu, TopMenu.HOME);
        add(homeMenu);

        User user = UserSession.get().getUser();

        add(new BookmarkablePageLink("registration_link", RegistrationPage.class).setVisible(user == null));
        add(new BookmarkablePageLink("login_link", LoginPage.class).setVisible(user == null));

        WebMarkupContainer userMenu = new WebMarkupContainer("user_menu");
        userMenu.setVisible(user != null);
        add(userMenu);
        if (userMenu.isVisible()) {
            userMenu.add(new BookmarkablePageLink("logout_link", LogoutPage.class).setVisible(user != null));
        }
    }

    private void addActiveClass(WebMarkupContainer container, TopMenu activeMenu, TopMenu menu) {
        if (activeMenu == menu) {
            container.add(new ClassAppender("active"));
        }
    }
}