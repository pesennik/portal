package com.github.pesennik.component.song;

import com.github.pesennik.Context;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.event.UserSongChangedEvent;
import com.github.pesennik.event.dispatcher.OnPayload;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Collections;
import java.util.List;

public class SongListModalPanel extends Panel {

    private final ContainerWithId songsList = new ContainerWithId("songs_list");

    public SongListModalPanel(String id) {
        super(id);
        add(songsList);
        updateSongsList();
    }

    public void updateSongsList() {
        List<UserSongId> userSongs = Context.getUserSongsDbi().getUserSongs(UserSessionUtils.getUserIdOrRedirectHome());
        Collections.reverse(userSongs); // show last first


        songsList.add(new ListView<UserSongId>("song", userSongs) {
            @Override
            protected void populateItem(ListItem<UserSongId> item) {
                UserSongId songId = item.getModelObject();
                UserSong song = Context.getUserSongsDbi().getSong(songId);
                if (song == null) {
                    item.setVisible(false);
                    return;
                }
                WebMarkupContainer link = new WebMarkupContainer("song_link");
                link.add(new AttributeModifier("onclick", "$site.Utils.scrollToBlock('#song-block-" + songId.getDbValue() + "');"));
                link.add(new Label("song_title", song.title));
                item.add(link);
            }
        });
    }

    @OnPayload
    public void onUserSongModifiedEvent(UserSongChangedEvent e) {
        if (e.changeType == UserSongChangedEvent.ChangeType.Deleted || e.changeType == UserSongChangedEvent.ChangeType.Created) {
            updateSongsList();
            e.target.add(songsList);
        }
    }
}
