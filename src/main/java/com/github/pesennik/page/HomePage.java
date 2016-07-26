package com.github.pesennik.page;

import com.github.pesennik.UserSession;
import com.github.pesennik.component.BootstrapModal;
import com.github.pesennik.component.ComponentFactory;
import com.github.pesennik.component.LoginPanel;
import com.github.pesennik.model.User;
import com.github.pesennik.page.signin.RegistrationPanel;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.jetbrains.annotations.NotNull;

public class HomePage extends BasePage {

    private final WebMarkupContainer modalBlock = new WebMarkupContainer("modal");

    public HomePage() {
        User user = UserSession.get().getUser();

        modalBlock.setOutputMarkupId(true);
        modalBlock.add(new EmptyPanel("modal_content"));
        add(modalBlock);

        WebMarkupContainer notLoggedInBlock = new WebMarkupContainer("not_logged_in_block");
        notLoggedInBlock.setVisible(user == null);
        add(notLoggedInBlock);

        if (notLoggedInBlock.isVisible()) {
            notLoggedInBlock.add(new ShowModalLink("login_link", (ComponentFactory) LoginPanel::new));
            notLoggedInBlock.add(new ShowModalLink("registration_link", (ComponentFactory) RegistrationPanel::new));
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

    private class ShowModalLink extends AjaxLink {
        @NotNull
        private final ComponentFactory factory;

        public ShowModalLink(@NotNull String id, @NotNull ComponentFactory factory) {
            super(id);
            this.factory = factory;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalBlock.removeAll();
            target.add(modalBlock);

            BootstrapModal modal = new BootstrapModal("modal_content", null, factory);
            modal.dialog.add(new AttributeAppender("style", "padding-left: 30px; padding-right: 30px; width:270px;"));
            modalBlock.add(modal);
            modal.show(target);

            target.appendJavaScript("$('#" + modal.getDataTargetId() + "').modal()");
        }
    }
}
