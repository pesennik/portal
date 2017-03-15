package com.github.pesennik.component.song;

import com.github.pesennik.Context;
import com.github.pesennik.UserSession;
import com.github.pesennik.behavior.ToggleDisplayBehavior;
import com.github.pesennik.component.util.AjaxCallback;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.event.UserSongChangedEvent;
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

    private final boolean readOnly;

    public SongPanel(String id, @NotNull UserSong song) {
        super(id);
        this.songId = song.id;
        readOnly = !song.userId.equals(UserSession.get().getUserId());
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

        ContainerWithId chordsView = new ContainerWithId("chords_view");
        songBlock.add(chordsView);

        songView = new SongTextView("text_view", songId, chordsView.getMarkupId());
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

    private void switchToEditMode(AjaxRequestTarget target) {
        if (readOnly) {
            return;
        }
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

}
