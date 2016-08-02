package com.github.pesennik.page;

import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.UserSongPanel;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.Collections;
import java.util.List;

@MountPath("/home")
public class UserHomePage extends BaseUserPage {

    public UserHomePage() {

        List<UserSongId> userSongs = Context.getUserSongsDbi().getUserSongs(UserSessionUtils.getUserIdOrRedirectHome());
        Collections.reverse(userSongs); // show last first

        add(new ListView<UserSongId>("song", userSongs) {
            @Override
            protected void populateItem(ListItem<UserSongId> item) {
                UserSongId songId = item.getModelObject();
                item.add(new UserSongPanel("song_panel", songId));
            }
        });
    }
}
