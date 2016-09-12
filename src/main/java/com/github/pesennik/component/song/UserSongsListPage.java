package com.github.pesennik.component.song;

import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.bootstrap.BootstrapLazyModalLink;
import com.github.pesennik.component.bootstrap.BootstrapModal;
import com.github.pesennik.component.tuner.TunerPanel;
import com.github.pesennik.component.user.BaseUserPage;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.event.UserSongChangedEvent;
import com.github.pesennik.event.UserSongChangedEvent.ChangeType;
import com.github.pesennik.event.dispatcher.OnPayload;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.Collections;
import java.util.List;

@MountPath("/home")
public class UserSongsListPage extends BaseUserPage {

    private final ContainerWithId songsList = new ContainerWithId("songs_list");

    public UserSongsListPage() {
        add(songsList);

        BootstrapModal tunerPopup = new BootstrapModal("tuner_popup", null, TunerPanel::new, BootstrapModal.BodyMode.Lazy, BootstrapModal.FooterMode.Show);
        add(tunerPopup);

        //TODO: replace with AJAX popup!
        add(new BookmarkablePageLink<>("add_song_link", CreateUserSongPage.class));

        add(new BootstrapLazyModalLink("tuner_link", tunerPopup));

        updateSongsList();
    }

    private void updateSongsList() {
        songsList.removeAll();

        List<UserSongId> userSongs = Context.getUserSongsDbi().getUserSongs(UserSessionUtils.getUserIdOrRedirectHome());
        Collections.reverse(userSongs); // show last first

        songsList.add(new ListView<UserSongId>("song", userSongs) {
            @Override
            protected void populateItem(ListItem<UserSongId> item) {
                UserSongId songId = item.getModelObject();
                item.add(new UserSongPanel("song_panel", songId));
            }
        });
    }

    @OnPayload
    public void onUserSongModifiedEvent(UserSongChangedEvent e) {
        if (e.changeType == ChangeType.Deleted) {
            updateSongsList();
            e.target.add(songsList);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS("body {padding-bottom:70px;}", "body-header.css"));
    }
}