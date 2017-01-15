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

    @NotNull
    @Sql("INSERT INTO user_songs (user_id, title, text_author, music_author, singer, band, text, creation_date, extra) " +
            "VALUES (:userId, :title, :textAuthor, :musicAuthor, :singer, :band, :text, :creationDate, :extra)")
    UserSongId insert(@BindBean UserSong song);

    @NotNull
    @Sql("SELECT id FROM user_songs WHERE user_id = :userId ORDER BY creation_date")
    List<UserSongId> selectUserSongs(@Bind("userId") UserId userId);

    @Nullable
    @Sql("SELECT * FROM user_songs WHERE id = :id")
    UserSong getSong(@Bind("id") UserSongId songId);

    @Sql("UPDATE user_songs SET title = :title, text_author = :textAuthor, music_author = :musicAuthor, singer = :singer, band = :band, " +
            "text = :text, extra = :extra WHERE id = :id")
    void update(@BindBean UserSong userSong);

    @Sql("DELETE FROM user_songs WHERE id = :id")
    void delete(@Bind("id") UserSongId id);
}
