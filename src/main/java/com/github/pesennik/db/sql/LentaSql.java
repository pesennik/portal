package com.github.pesennik.db.sql;

import com.github.mjdbc.BindBean;
import com.github.mjdbc.Sql;
import com.github.pesennik.model.LentaEntry;
import com.github.pesennik.model.LentaEntryId;
import com.github.pesennik.model.UserSongId;
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
    @Sql("SELECT user_song_id FROM lenta ORDER BY sharing_date")
    List<UserSongId> selectSharedSongs();

}
