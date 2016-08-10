package com.github.pesennik.component;

import com.github.pesennik.page.CreateUserSongPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

public class UserSongsListPageHeader extends Panel {
    public UserSongsListPageHeader(String id) {
        super(id);
        //TODO: replace with AJAX popup!
        add(new BookmarkablePageLink<>("add_song_link", CreateUserSongPage.class));
    }
}
