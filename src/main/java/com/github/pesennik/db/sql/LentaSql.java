package com.github.pesennik.db.sql;

import com.github.mjdbc.BindBean;
import com.github.mjdbc.Sql;
import com.github.pesennik.model.LentaEntry;
import com.github.pesennik.model.LentaEntryId;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Set of 'users' table queries
 */
public interface LentaSql {

    @NotNull
    @Sql("INSERT INTO lenta(user_song_id, sharing_date) VALUES (:userSongId, :sharingDate)")
    LentaEntryId insert(@NotNull @BindBean LentaEntry entry);

    @NotNull
    @Sql("SELECT * FROM lenta ORDER BY id")
    List<LentaEntryId> selectAll();

}
