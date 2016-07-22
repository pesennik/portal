package com.github.pesennik.db.sql;

import com.github.mjdbc.Bind;
import com.github.mjdbc.BindBean;
import com.github.mjdbc.Sql;
import com.github.pesennik.model.UserId;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Set of 'user_song' table queries
 */
public interface UserSongsSql {

    @Nullable
    @Sql("SELECT * FROM user_songs WHERE user_id = :userId ORDER BY creation_date")
    List<UserSong> selectUserSongs(@Bind("userId") UserId userId);

    @NotNull
    @Sql("INSERT INTO user_songs (user_id, title, author, text, creation_date) VALUES (:userId, :title, :author, :text, :creationDate)")
    UserSongId insert(@BindBean UserSong song);

    @Sql("UPDATE user_songs SET title = :title, author = :author, text = :text WHERE id = :id")
    void update(@BindBean UserSong userSong);

    @Sql("DELETE FROM user_songs WHERE id = :id")
    void delete(@Bind("id") UserSongId id);
}
