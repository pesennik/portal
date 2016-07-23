package com.github.pesennik.page;

import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.Formatters;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.Collections;
import java.util.List;

@MountPath("/home")
public class UserHomePage extends BaseUserPage {

    public UserHomePage() {

        add(new BookmarkablePageLink("add_song_link", CreateUserSongPage.class));

        List<UserSongId> userSongs = Context.getUserSongsDbi().getUserSongs(UserSessionUtils.getUserIdOrRedirectHome());
        Collections.reverse(userSongs); // show last first

        add(new ListView<UserSongId>("song", userSongs) {
            @Override
            protected void populateItem(ListItem<UserSongId> item) {
                UserSongId songId = item.getModelObject();
                UserSong song = Context.getUserSongsDbi().getSong(songId);
                if (song == null) {
                    item.setVisible(false);
                    return;
                }
                item.add(new Label("title", song.title));
                item.add(new Label("author", song.author));
                item.add(new Label("text", song.text));
                item.add(new Label("date", Formatters.SONG_DATE_FORMAT.format(song.creationDate)));
            }
        });
    }
}
