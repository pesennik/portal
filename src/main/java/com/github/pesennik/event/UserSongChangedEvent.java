package com.github.pesennik.event;

import com.github.pesennik.model.UserSongId;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.jetbrains.annotations.NotNull;

public class UserSongChangedEvent extends AjaxEvent {

    public enum ChangeType {
        Created,
        Updated,
        Transposed,
        Deleted
    }

    @NotNull
    public final UserSongId songId;

    @NotNull
    public final ChangeType changeType;


    public UserSongChangedEvent(@NotNull AjaxRequestTarget target, @NotNull UserSongId songId, @NotNull ChangeType changeType) {
        super(target);
        this.songId = songId;
        this.changeType = changeType;
    }
}
