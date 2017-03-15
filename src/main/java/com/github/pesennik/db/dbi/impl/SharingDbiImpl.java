package com.github.pesennik.db.dbi.impl;

import com.github.mjdbc.Db;
import com.github.pesennik.db.dbi.AbstractDbi;
import com.github.pesennik.db.dbi.SharingDbi;
import com.github.pesennik.db.sql.LentaSql;
import com.github.pesennik.model.LentaEntry;
import com.github.pesennik.model.UserSongId;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SharingDbiImpl extends AbstractDbi implements SharingDbi {

    @NotNull
    private final LentaSql lentaSql;

    public SharingDbiImpl(@NotNull Db db) {
        super(db);
        lentaSql = db.attachSql(LentaSql.class);
    }

    @Override
    public void create(@NotNull LentaEntry entry) {
        entry.id = lentaSql.insert(entry);
    }

    @NotNull
    @Override
    public List<UserSongId> getSharedSongs() {
        return lentaSql.selectSharedSongs();
    }

}
