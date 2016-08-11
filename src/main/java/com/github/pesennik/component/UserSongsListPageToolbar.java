package com.github.pesennik.component;

import com.github.pesennik.page.CreateUserSongPage;
import com.github.pesennik.page.TunerPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

public class UserSongsListPageToolbar extends Panel {
    public UserSongsListPageToolbar(String id) {
        super(id);
        //TODO: replace with AJAX popup!
        add(new BookmarkablePageLink<>("add_song_link", CreateUserSongPage.class));
        //TODO: replace with AJAX popup!
        add(new BookmarkablePageLink<>("tuner_link", TunerPage.class));
    }
}
