package com.github.pesennik.component.song;

import com.github.pesennik.Context;
import com.github.pesennik.behavior.ToggleDisplayBehavior;
import com.github.pesennik.component.util.AjaxCallback;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.event.UserSongChangedEvent;
import com.github.pesennik.event.dispatcher.OnPayload;
import com.github.pesennik.model.SongChordsViewMode;
import com.github.pesennik.model.SongTextViewMode;
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

import java.util.Objects;

import static com.github.pesennik.util.TextUtils.addSeparator;
import static com.github.pesennik.util.TextUtils.isEmpty;

public class SongPanel extends Panel {

    @NotNull
    private final UserSongId songId;

    @NotNull
    private final WebMarkupContainer mainPanel;

    @NotNull
    private final WebMarkupContainer songBlock;

    private SongTextView songView;

    @NotNull
    private Panel editPanel;

    public SongPanel(String id, @NotNull UserSongId songId) {
        super(id);
        this.songId = songId;

        mainPanel = new WebMarkupContainer("main_panel");
        mainPanel.setOutputMarkupId(true);
        add(mainPanel);

        songBlock = new WebMarkupContainer("song");
        songBlock.setOutputMarkupId(true);
        songBlock.setMarkupId("song-block-" + songId.getDbValue());
        mainPanel.add(songBlock);
        updateSongView();

        editPanel = new EmptyPanel("edit_panel");
        mainPanel.add(editPanel);
    }

    private void updateSongView() {
        UserSong song = Context.getUserSongsDbi().getSong(songId);
        if (song == null) {
            songBlock.setVisible(false);
            return;
        }
        songBlock.removeAll();

        WebMarkupContainer titleLink = new WebMarkupContainer("title_link");
        songBlock.add(titleLink);
        titleLink.add(new Label("title", song.title));

        songBlock.add(new Label("author_info", prepareAuthorInfo(song)));
        songBlock.add(new Label("date", Formatters.SONG_DATE_FORMAT.format(song.creationDate)));
        songBlock.add(new SongLinksPanel("links", song.extra.links));

        songView = new SongTextView("text_view", songId);
        songBlock.add(songView);

        ContainerWithId toolbar = new ContainerWithId("toolbar");
        songBlock.add(toolbar);

        toolbar.add(new AjaxLink<Void>("edit_link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                switchToEditMode(target);
            }
        });
        titleLink.add(new ToggleDisplayBehavior(toolbar, "none"));

        toolbar.add(new ChangeSongTextViewModeLink("text_view_visible_link", SongTextViewMode.Visible));
        toolbar.add(new ChangeSongTextViewModeLink("text_view_hidden_link", SongTextViewMode.Hidden));
        updateSongTextModeControls(toolbar, song);

        toolbar.add(new ChangeSongChordsViewModeLink("chords_view_inlined_link", SongChordsViewMode.Inlined));
        toolbar.add(new ChangeSongChordsViewModeLink("chords_view_hidden_link", SongChordsViewMode.Hidden));
        updateSongChordsModeControls(toolbar, song);

        toolbar.add(new TransposeAjaxLink("tone_down_link", songId, -1));
        toolbar.add(new TransposeAjaxLink("tone_up_link", songId, +1));
    }

    @NotNull
    private String prepareAuthorInfo(@NotNull UserSong song) {
        StringBuilder res = new StringBuilder();
        //try shorten text if lyrics & music & signer are the same
        if (!isEmpty(song.textAuthor) && Objects.equals(song.textAuthor, song.musicAuthor) && Objects.equals(song.textAuthor, song.singer)) {
            res.append(song.textAuthor);
        } else if (!isEmpty(song.textAuthor) && Objects.equals(song.textAuthor, song.musicAuthor)) {
            res.append("сл. и муз. ").append(song.textAuthor);
        } else {
            if (!isEmpty(song.textAuthor)) {
                res.append("сл. ").append(song.textAuthor);
            }
            if (!isEmpty(song.musicAuthor)) {
                addSeparator(res, ", ");
                res.append("муз. ").append(song.musicAuthor);
            }
            if (!isEmpty(song.singer)) {
                addSeparator(res, ", ");
                res.append(song.singer);
            }
        }
        if (!isEmpty(song.band)) {
            addSeparator(res, ", ");
            res.append("«").append(song.band).append("»");
        }
        return res.toString();
    }

    private void updateSongTextModeControls(@NotNull Component toolbar, @NotNull UserSong song) {
        toolbar.get("text_view_visible_link").setVisible(song.extra.textViewMode != SongTextViewMode.Visible);
        toolbar.get("text_view_hidden_link").setVisible(song.extra.textViewMode != SongTextViewMode.Hidden);
    }

    private void updateSongChordsModeControls(@NotNull Component toolbar, @NotNull UserSong song) {
        toolbar.get("chords_view_inlined_link").setVisible(song.extra.chordsViewMode != SongChordsViewMode.Inlined);
        toolbar.get("chords_view_hidden_link").setVisible(song.extra.chordsViewMode != SongChordsViewMode.Hidden);
    }

    private void switchToEditMode(AjaxRequestTarget target) {
        songBlock.setVisible(false);
        mainPanel.remove(editPanel);
        editPanel = new SongEditPanel("edit_panel", songId, (AjaxCallback & IClusterable) this::switchToViewMode);
        mainPanel.add(editPanel);
        target.add(mainPanel);
    }

    private void switchToViewMode(AjaxRequestTarget target) {
        songBlock.setVisible(true);
        editPanel.setVisible(false);
        updateSongView();
        target.add(mainPanel);
        target.appendJavaScript("$site.Utils.scrollToBlock('#" + mainPanel.getMarkupId() + "')");
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

    private class ChangeSongChordsViewModeLink extends AjaxLink<Void> {

        @NotNull
        private final SongChordsViewMode mode;

        public ChangeSongChordsViewModeLink(@NotNull String id, @NotNull SongChordsViewMode mode) {
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

            Component toolbar = songBlock.get("toolbar");
            updateSongChordsModeControls(toolbar, song);
            target.add(toolbar);

            target.add(songView);
        }
    }

    private class ChangeSongTextViewModeLink extends AjaxLink<Void> {

        @NotNull
        private final SongTextViewMode mode;

        public ChangeSongTextViewModeLink(@NotNull String id, @NotNull SongTextViewMode mode) {
            super(id);
            this.mode = mode;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            UserSong song = Context.getUserSongsDbi().getSong(songId);
            if (song == null) {
                return;
            }
            song.extra.textViewMode = mode;
            Context.getUserSongsDbi().updateSong(song);

            Component toolbar = songBlock.get("toolbar");
            updateSongTextModeControls(toolbar, song);
            target.add(toolbar);

            target.add(songView);
        }
    }

}
