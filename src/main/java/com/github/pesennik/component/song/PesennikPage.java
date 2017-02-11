package com.github.pesennik.component.song;

import com.github.pesennik.Context;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.HomePage;
import com.github.pesennik.component.bootstrap.BootstrapLazyModalLink;
import com.github.pesennik.component.bootstrap.BootstrapModal;
import com.github.pesennik.component.bootstrap.BootstrapStaticModalLink;
import com.github.pesennik.component.tuner.TunerPanel;
import com.github.pesennik.component.user.BaseUserPage;
import com.github.pesennik.component.util.AjaxCallback;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.event.UserSongChangedEvent;
import com.github.pesennik.event.UserSongChangedEvent.ChangeType;
import com.github.pesennik.event.dispatcher.OnPayload;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.AbstractListProvider;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Collections;
import java.util.List;

import static com.github.pesennik.util.JSUtils.scrollTo;

@MountPath("/home")
public class PesennikPage extends BaseUserPage {

    private final ContainerWithId songsList = new ContainerWithId("songs_list");

    public PesennikPage() {
        add(songsList);

        BootstrapModal tunerPopup = new BootstrapModal("tuner_popup", null, TunerPanel::new, BootstrapModal.BodyMode.Lazy, BootstrapModal.FooterMode.Show);
        add(tunerPopup);
        add(new BootstrapLazyModalLink("tuner_link", tunerPopup));

        WebMarkupContainer newSongBlock = new ContainerWithId("new_song_block");
        newSongBlock.add(new WebMarkupContainer("new_song_panel"));
        songsList.add(newSongBlock);
        add(new AjaxLink<Void>("add_song_link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Component c = newSongBlock.get("new_song_panel");
                if (c instanceof SongEditPanel) {
                    scrollTo(c, target);
                    return;
                }
                SongEditPanel p = new SongEditPanel("new_song_panel", null, (AjaxCallback) t -> {
                    newSongBlock.addOrReplace(new WebMarkupContainer("new_song_panel"));
                    t.add(newSongBlock);
                });
                p.setOutputMarkupId(true);
                newSongBlock.addOrReplace(p);
                scrollTo(c, target);
                target.add(newSongBlock);
            }
        });

        add(new BookmarkablePageLink("home_link", HomePage.class));

        BootstrapModal listPopup = new BootstrapModal("list_popup", "Список песен", SongListModalPanel::new, BootstrapModal.BodyMode.Static, BootstrapModal.FooterMode.Show);
        add(listPopup);
        add(new BootstrapStaticModalLink("list_link", listPopup));

        songsList.add(new DataView<UserSongId>("song", new SongListProvider()) {
            @Override
            protected void populateItem(Item<UserSongId> item) {
                item.add(new SongPanel("song_panel", item.getModelObject()));
            }
        });
    }

    @OnPayload
    public void onUserSongModifiedEvent(UserSongChangedEvent e) {
        if (e.changeType == ChangeType.Deleted) {
            songsList.detach();
            e.target.add(songsList);
        } else if (e.changeType == ChangeType.Created) {
            songsList.detach();
            e.target.add(songsList);
            e.target.appendJavaScript("$('body,html').animate({scrollTop: 0}, 500)");
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS("body {padding-bottom:70px;}", "body-header.css"));
    }

    private class SongListProvider extends AbstractListProvider<UserSongId> {
        @Override
        public List<UserSongId> getList() {
            List<UserSongId> userSongs = Context.getUserSongsDbi().getUserSongs(UserSessionUtils.getUserIdOrRedirectHome());
            Collections.reverse(userSongs); // show last first
            return userSongs;
        }

        @Override
        public IModel<UserSongId> model(UserSongId songId) {
            return Model.of(songId);
        }
    }
}