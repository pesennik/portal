package com.github.pesennik.page;

import com.github.pesennik.UserSession;
import com.github.pesennik.behavior.StyleAppender;
import com.github.pesennik.component.BootstrapModal;
import com.github.pesennik.component.BootstrapModalStaticLink;
import com.github.pesennik.component.LoginPanel;
import com.github.pesennik.model.User;
import com.github.pesennik.page.signin.RegistrationPanel;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;

public class HomePage extends BasePage {

    public HomePage() {
        User user = UserSession.get().getUser();

        WebMarkupContainer notLoggedInBlock = new WebMarkupContainer("not_logged_in_block");
        notLoggedInBlock.setVisible(user == null);
        add(notLoggedInBlock);

        if (notLoggedInBlock.isVisible()) {
            BootstrapModal loginModal = new BootstrapModal("login_modal", null, LoginPanel::new, true, true);
            loginModal.dialog.add(new StyleAppender("padding-left: 30px; padding-right: 30px; width:270px;"));
            add(loginModal);

            BootstrapModal registrationModal = new BootstrapModal("registration_modal", null, RegistrationPanel::new, true, true);
            registrationModal.dialog.add(new StyleAppender("padding-left: 30px; padding-right: 30px; width:270px;"));
            add(registrationModal);

            notLoggedInBlock.add(new BootstrapModalStaticLink("login_link", loginModal));
            notLoggedInBlock.add(new BootstrapModalStaticLink("registration_link", registrationModal));
        } else {
            add(new EmptyPanel("login_modal"));
            add(new EmptyPanel("registration_modal"));
        }

        WebMarkupContainer loggedInBlock = new WebMarkupContainer("logged_in_block");
        loggedInBlock.setVisible(user != null);
        add(loggedInBlock);
        if (loggedInBlock.isVisible()) {
            loggedInBlock.add(new BookmarkablePageLink("user_home_link", UserHomePage.class));
            loggedInBlock.add(new AjaxLink("logout_link") {
                @Override
                public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                    UserSessionUtils.logout();
                    setResponsePage(HomePage.class);
                }
            });
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        //todo: roll the whole set of backgrounds
        response.render(CssHeaderItem.forUrl("/backgrounds/001.css"));
    }

}
