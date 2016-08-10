package com.github.pesennik.page;

import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.ContainerWithId;
import com.github.pesennik.component.UserSongPanel;
import com.github.pesennik.component.UserSongsListPageHeader;
import com.github.pesennik.event.UserSongChangedEvent;
import com.github.pesennik.event.UserSongChangedEvent.ChangeType;
import com.github.pesennik.event.dispatcher.OnPayload;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.Collections;
import java.util.List;

@MountPath("/home")
public class UserSongsListPage extends BaseUserPage {

    private final ContainerWithId songsList = new ContainerWithId("songs_list");

    public UserSongsListPage() {
        //noinspection WicketForgeJavaIdInspection
        replace(new UserSongsListPageHeader("footer"));

        add(songsList);

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