package com.github.pesennik.component.song;

import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.BasePage;
import com.github.pesennik.component.HomePage;
import com.github.pesennik.component.bootstrap.BootstrapModal;
import com.github.pesennik.component.bootstrap.BootstrapStaticModalLink;
import com.github.pesennik.component.sidebar.SidebarRandomSongButton;
import com.github.pesennik.component.sidebar.SidebarTunerButton;
import com.github.pesennik.component.sidebar.SidebarZoomButtons;
import com.github.pesennik.component.tuner.TunerPanel;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.AbstractListProvider;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Collections;
import java.util.List;

@MountPath("/lenta")
public class LentaPage extends BasePage {

    private final ContainerWithId songsList = new ContainerWithId("songs_list");

    public LentaPage() {
        add(songsList);

        BootstrapModal tunerPopup = new BootstrapModal("tuner_popup", null, TunerPanel::new, BootstrapModal.BodyMode.Lazy, BootstrapModal.FooterMode.Show);
        add(tunerPopup);

        add(new SidebarTunerButton("tuner_button", tunerPopup));
        add(new SidebarZoomButtons("zoom_buttons"));
        add(new SidebarRandomSongButton("random_song_button"));
        add(new BookmarkablePageLink("home_link", HomePage.class));

        BootstrapModal listPopup = new BootstrapModal("list_popup", "Список песен", SongListModalPanel::new, BootstrapModal.BodyMode.Static, BootstrapModal.FooterMode.Show);
        add(listPopup);
        add(new BootstrapStaticModalLink("list_link", listPopup));

        songsList.add(new DataView<UserSongId>("song", new LentaSongListProvider()) {
            @Override
            protected void populateItem(Item<UserSongId> item) {
                UserSong song = Context.getUserSongsDbi().getSong(item.getModelObject());
                if (song == null) {
                    item.setVisible(false);
                    return;
                }
                item.add(new SongPanel("song_panel", song));
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS("body {padding-bottom:70px;}", "body-header.css"));
    }

    private class LentaSongListProvider extends AbstractListProvider<UserSongId> {
        @Override
        public List<UserSongId> getList() {
            List<UserSongId> userSongs = Context.getSharingDbi().getSharedSongs();
            Collections.reverse(userSongs); // show last first
            return userSongs;
        }

        @Override
        public IModel<UserSongId> model(UserSongId songId) {
            return Model.of(songId);
        }
    }
}
