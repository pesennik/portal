package com.github.pesennik.db.dbi;

import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import org.jetbrains.annotations.NotNull;

public interface UserSongsDbi {

    @NotNull
    UserSongId insert(@NotNull UserSong song);
}