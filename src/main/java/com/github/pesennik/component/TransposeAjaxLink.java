package com.github.pesennik.component;

import com.github.pesennik.Context;
import com.github.pesennik.event.UserSongChangedEvent;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import com.github.pesennik.util.TransposeUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.jetbrains.annotations.NotNull;

public class TransposeAjaxLink extends AjaxLink<Void> {

    @NotNull
    private final UserSongId songId;
    private final int step;

    public TransposeAjaxLink(@NotNull String id, @NotNull UserSongId songId, int step) {
        super(id);
        this.songId = songId;
        this.step = step;
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        UserSong song = Context.getUserSongsDbi().getSong(songId);
        if (song == null) {
            return;
        }
        String newText = TransposeUtils.transposeText(song.text, step);
        if (song.text.equals(newText)) {
            return;
        }
        song.text = newText;
        Context.getUserSongsDbi().updateSong(song);
        send(getPage(), Broadcast.BREADTH, new UserSongChangedEvent(target, songId, UserSongChangedEvent.ChangeType.ChordsChanged));
    }
}
