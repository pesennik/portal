package com.github.pesennik.event;

import com.github.pesennik.model.UserSongId;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.jetbrains.annotations.NotNull;

public class UserSongModifiedEvent extends AjaxEvent {

    @NotNull
    public final UserSongId songId;

    public UserSongModifiedEvent(@NotNull AjaxRequestTarget target, @NotNull UserSongId songId) {
        super(target);
        this.songId = songId;
    }
}
