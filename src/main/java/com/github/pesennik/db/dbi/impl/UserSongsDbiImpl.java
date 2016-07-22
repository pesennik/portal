package com.github.pesennik.db.dbi.impl;

import com.github.mjdbc.Db;
import com.github.pesennik.db.dbi.AbstractDbi;
import com.github.pesennik.db.dbi.UserSongsDbi;
import com.github.pesennik.db.sql.UserSongsSql;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import org.jetbrains.annotations.NotNull;

/**
 * Database access interface for user data
 */
public final class UserSongsDbiImpl extends AbstractDbi implements UserSongsDbi {

    @NotNull
    private final UserSongsSql songsSql;

    public UserSongsDbiImpl(@NotNull Db db) {
        super(db);
        songsSql = db.attachSql(UserSongsSql.class);
    }

    @NotNull
    @Override
    public UserSongId insert(@NotNull UserSong song) {
        song.id = songsSql.insert(song);
        return song.id;
    }
}
