package com.github.pesennik.db.dbi;

import com.github.pesennik.model.UserId;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface UserSongsDbi {

    @NotNull
    UserSongId createSong(@NotNull UserSong song);

    @NotNull
    List<UserSongId> getUserSongs(@NotNull UserId userId);

    @Nullable
    UserSong getSong(@NotNull UserSongId songId);

    void updateSong(@NotNull UserSong song);
}