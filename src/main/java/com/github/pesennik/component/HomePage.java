package com.github.pesennik.component;

import com.github.pesennik.UserSession;
import com.github.pesennik.behavior.StyleAppender;
import com.github.pesennik.component.bootstrap.BootstrapModal;
import com.github.pesennik.component.bootstrap.BootstrapModal.BodyMode;
import com.github.pesennik.component.bootstrap.BootstrapModal.FooterMode;
import com.github.pesennik.component.bootstrap.BootstrapStaticModalLink;
import com.github.pesennik.component.signin.LoginPanel;
import com.github.pesennik.component.signin.RegistrationPanel;
import com.github.pesennik.component.song.CreateUserSongPage;
import com.github.pesennik.component.song.UserSongsListPage;
import com.github.pesennik.component.tuner.TunerPage;
import com.github.pesennik.model.User;
import com.github.pesennik.util.UDate;
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
            BootstrapModal loginModal = new BootstrapModal("login_modal", null, LoginPanel::new, BodyMode.Static, FooterMode.Hide);
            loginModal.dialog.add(new StyleAppender("padding-left: 30px; padding-right: 30px; width:270px;"));
            add(loginModal);

            BootstrapModal registrationModal = new BootstrapModal("registration_modal", null, RegistrationPanel::new, BodyMode.Static, FooterMode.Hide);
            registrationModal.dialog.add(new StyleAppender("padding-left: 30px; padding-right: 30px; width:350px;"));
            add(registrationModal);

            notLoggedInBlock.add(new BootstrapStaticModalLink("login_link", loginModal));
            notLoggedInBlock.add(new BootstrapStaticModalLink("registration_link", registrationModal));
            notLoggedInBlock.add(new BookmarkablePageLink("tuner_link", TunerPage.class));
        } else {
            add(new EmptyPanel("login_modal"));
            add(new EmptyPanel("registration_modal"));
        }

        WebMarkupContainer loggedInBlock = new WebMarkupContainer("logged_in_block");
        loggedInBlock.setVisible(user != null);
        add(loggedInBlock);
        if (loggedInBlock.isVisible()) {
            loggedInBlock.add(new BookmarkablePageLink("user_home_link", UserSongsListPage.class));
            loggedInBlock.add(new BookmarkablePageLink("add_song_link", CreateUserSongPage.class));
            loggedInBlock.add(new BookmarkablePageLink("tuner_link", TunerPage.class));
            loggedInBlock.add(new AjaxLink<Void>("logout_link") {
                @Override
                public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                    UserSessionUtils.logout();
                    setResponsePage(HomePage.class);
                }
            });
        }
    }

    public static final int MAX_BACKGROUNDS = 3;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        int day = UDate.now().getDayOfYear();
        int bg = 1 + day % MAX_BACKGROUNDS;
        String style = bg > 100 ? "" + bg : bg > 10 ? "0" + bg : "00" + bg;
        response.render(CssHeaderItem.forUrl("/backgrounds/" + style + ".css"));
    }

}
