package com.github.pesennik.component;

import com.github.pesennik.Context;
import com.github.pesennik.event.UserSongModifiedEvent;
import com.github.pesennik.event.dispatcher.OnPayload;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.Formatters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;

public class UserSongPanel extends Panel {


    @NotNull
    private final UserSongId songId;

    @NotNull
    private final WebMarkupContainer mainPanel;

    @NotNull
    private final WebMarkupContainer viewPanel;

    @NotNull
    private Panel editPanel;

    public UserSongPanel(String id, @NotNull UserSongId songId) {
        super(id);
        this.songId = songId;

        mainPanel = new WebMarkupContainer("panel");
        mainPanel.setOutputMarkupId(true);
        add(mainPanel);

        viewPanel = new WebMarkupContainer("song");
        viewPanel.setOutputMarkupId(true);
        mainPanel.add(viewPanel);
        updateSongView();

        editPanel = new EmptyPanel("edit_panel");
        mainPanel.add(editPanel);
    }

    private void updateSongView() {
        UserSong song = Context.getUserSongsDbi().getSong(songId);
        if (song == null) {
            viewPanel.setVisible(false);
            return;
        }
        viewPanel.removeAll();
        viewPanel.add(new Label("author", song.author));
        viewPanel.add(new Label("text", song.text));
        viewPanel.add(new Label("date", Formatters.SONG_DATE_FORMAT.format(song.creationDate)));


        AjaxLink editLink = new AjaxLink("edit_link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                switchToEditMode(target);
            }


        };
        viewPanel.add(editLink);
        editLink.add(new Label("title", song.title));
    }

    private void switchToEditMode(AjaxRequestTarget target) {
        viewPanel.setVisible(false);
        mainPanel.remove(editPanel);
        editPanel = new UserSongEditPanel("edit_panel", songId);
        mainPanel.add(editPanel);
        target.add(mainPanel);
    }

    private void switchToViewMode(AjaxRequestTarget target) {
        viewPanel.setVisible(true);
        editPanel.setVisible(false);
        updateSongView();
        target.add(mainPanel);
    }

    @OnPayload
    public void onUserSongModifiedEvent(UserSongModifiedEvent e) {
        if (e.songId.equals(songId)) {
            switchToViewMode(e.target);
        }
    }
}
