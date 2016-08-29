package com.github.pesennik.component.song;

import com.github.pesennik.component.bootstrap.BootstrapLazyModalLink;
import com.github.pesennik.component.bootstrap.BootstrapModal;
import com.github.pesennik.component.bootstrap.BootstrapModal.BodyMode;
import com.github.pesennik.component.bootstrap.BootstrapModal.FooterMode;
import com.github.pesennik.component.tuner.TunerPanel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

public class UserSongsListPageToolbar extends Panel {

    public UserSongsListPageToolbar(String id) {
        super(id);

        BootstrapModal tunerPopup = new BootstrapModal("tuner_popup", null, TunerPanel::new, BodyMode.Lazy, FooterMode.Show);
        add(tunerPopup);

        //TODO: replace with AJAX popup!
        add(new BookmarkablePageLink<>("add_song_link", CreateUserSongPage.class));

        add(new BootstrapLazyModalLink("tuner_link", tunerPopup));
    }
}
