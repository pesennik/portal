package com.github.pesennik.db.dbi;

import com.github.pesennik.model.LentaEntry;
import com.github.pesennik.model.UserSongId;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SharingDbi {

    void create(@NotNull LentaEntry entry);

    /**
     * Returns list of shared songs ordered by sharing date ascending.
     */
    @NotNull
    List<UserSongId> getSharedSongs();
}
