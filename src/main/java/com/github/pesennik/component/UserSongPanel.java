package com.github.pesennik.component;

import com.github.pesennik.Context;
import com.github.pesennik.behavior.ToggleDisplayBehavior;
import com.github.pesennik.event.UserSongChangedEvent;
import com.github.pesennik.event.dispatcher.OnPayload;
import com.github.pesennik.model.ChordsViewMode;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.Formatters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.io.IClusterable;
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

        WebMarkupContainer titleLink = new WebMarkupContainer("title_link");
        viewPanel.add(titleLink);
        titleLink.add(new Label("title", song.title));

        viewPanel.add(new Label("author", song.author));
        viewPanel.add(new UserSongTextView("text_view", songId));
        viewPanel.add(new Label("date", Formatters.SONG_DATE_FORMAT.format(song.creationDate)));

        ContainerWithId toolbar = new ContainerWithId("toolbar");
        viewPanel.add(toolbar);

        toolbar.add(new AjaxLink<Void>("edit_link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                switchToEditMode(target);
            }
        });
        titleLink.add(new ToggleDisplayBehavior(toolbar, "none"));
        toolbar.add(new ChangeSongViewModeLink("chords_view_inlined_link", song, ChordsViewMode.Inlined));
        toolbar.add(new ChangeSongViewModeLink("chords_view_hidden_link", song, ChordsViewMode.Hidden));
    }

    private void switchToEditMode(AjaxRequestTarget target) {
        viewPanel.setVisible(false);
        mainPanel.remove(editPanel);
        editPanel = new UserSongEditPanel("edit_panel", songId, (AjaxCallback & IClusterable) this::switchToViewMode);
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
    public void onUserSongModifiedEvent(UserSongChangedEvent e) {
        if (e.songId.equals(songId) && e.changeType != UserSongChangedEvent.ChangeType.Deleted) {
            switchToViewMode(e.target);
        }
    }

    private class ChangeSongViewModeLink extends AjaxLink<Void> {

        @NotNull
        private final ChordsViewMode mode;

        public ChangeSongViewModeLink(@NotNull String id, @NotNull UserSong song, @NotNull ChordsViewMode mode) {
            super(id);
            this.mode = mode;
            setVisible(song.extra.chordsViewMode != mode);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            UserSong song = Context.getUserSongsDbi().getSong(songId);
            if (song == null) {
                return;
            }
            song.extra.chordsViewMode = mode;
            Context.getUserSongsDbi().updateSong(song);
            updateSongView();
            target.add(mainPanel);
        }
    }
}
