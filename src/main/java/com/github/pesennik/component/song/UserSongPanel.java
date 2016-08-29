package com.github.pesennik.component.song;

import com.github.pesennik.Context;
import com.github.pesennik.behavior.ToggleDisplayBehavior;
import com.github.pesennik.component.util.AjaxCallback;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.event.UserSongChangedEvent;
import com.github.pesennik.event.dispatcher.OnPayload;
import com.github.pesennik.model.ChordsViewMode;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.Formatters;
import org.apache.wicket.Component;
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

    private UserSongTextView songView;

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
        viewPanel.add(new Label("date", Formatters.SONG_DATE_FORMAT.format(song.creationDate)));
        viewPanel.add(new SongLinksPanel("links", song.extra.links));

        songView = new UserSongTextView("text_view", songId);
        viewPanel.add(songView);

        ContainerWithId toolbar = new ContainerWithId("toolbar");
        viewPanel.add(toolbar);

        toolbar.add(new AjaxLink<Void>("edit_link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                switchToEditMode(target);
            }
        });
        titleLink.add(new ToggleDisplayBehavior(toolbar, "none"));
        toolbar.add(new ChangeSongViewChordsModeLink("chords_view_inlined_link", ChordsViewMode.Inlined));
        toolbar.add(new ChangeSongViewChordsModeLink("chords_view_hidden_link", ChordsViewMode.Hidden));
        toolbar.add(new TransposeAjaxLink("tone_down_link", songId, -1));
        toolbar.add(new TransposeAjaxLink("tone_up_link", songId, +1));

        updateSongViewChordsMode(toolbar, song);
    }

    private void updateSongViewChordsMode(@NotNull Component toolbar, @NotNull UserSong song) {
        toolbar.get("chords_view_inlined_link").setVisible(song.extra.chordsViewMode != ChordsViewMode.Inlined);
        toolbar.get("chords_view_hidden_link").setVisible(song.extra.chordsViewMode != ChordsViewMode.Hidden);
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
        if (!e.songId.equals(songId) || e.changeType == UserSongChangedEvent.ChangeType.Deleted) {
            return;
        }
        if (e.changeType == UserSongChangedEvent.ChangeType.ChordsChanged) {
            e.target.add(songView);
            return;
        }
        switchToViewMode(e.target);
    }

    private class ChangeSongViewChordsModeLink extends AjaxLink<Void> {

        @NotNull
        private final ChordsViewMode mode;

        public ChangeSongViewChordsModeLink(@NotNull String id, @NotNull ChordsViewMode mode) {
            super(id);
            this.mode = mode;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            UserSong song = Context.getUserSongsDbi().getSong(songId);
            if (song == null) {
                return;
            }
            song.extra.chordsViewMode = mode;

            Context.getUserSongsDbi().updateSong(song);

            Component toolbar = viewPanel.get("toolbar");
            updateSongViewChordsMode(toolbar, song);
            target.add(toolbar);

            target.add(songView);
        }
    }
}
