package com.github.pesennik.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

import static com.github.pesennik.db.util.DbUtils.optionalInstant;

/**
 * User's song
 */
public class UserSong extends Identifiable<UserSongId> {

    public static final int MAX_SONG_TITLE_LENGTH = 64;
    public static final int MAX_SONG_AUTHOR_LENGTH = 64;
    public static final int MAX_SONG_TEXT_LENGTH = 4000;
    public static final int MIN_SONG_TEXT_LENGTH = 10;
    public static final int MAX_URLS_TEXT_LENGTH = 1000;

    @NotNull
    public UserId userId = UserId.UNDEFINED;

    @NotNull
    public String title = "";

    @NotNull
    public String textAuthor = "";

    @NotNull
    public String musicAuthor = "";

    @NotNull
    public String singer = "";

    @NotNull
    public String band = "";

    @NotNull
    public String text = "";

    @NotNull
    public Instant creationDate = Instant.MIN;

    @Nullable
    public Instant deletionDate = null;

    @NotNull
    public UserSongExtra extra = new UserSongExtra();

    @Mapper
    public static final DbMapper<UserSong> MAPPER = r -> {
        UserSong res = new UserSong();
        res.id = new UserSongId(r.getInt("id"));
        res.userId = new UserId(r.getInt("user_id"));
        res.title = r.getString("title");
        res.textAuthor = r.getString("text_author");
        res.musicAuthor = r.getString("music_author");
        res.singer = r.getString("singer");
        res.band = r.getString("band");
        res.text = r.getString("text");
        res.creationDate = r.getTimestamp("creation_date").toInstant();
        res.deletionDate = optionalInstant(r.getTimestamp("deletion_date"));
        res.extra = UserSongExtra.parse(r.getString("extra"));
        return res;
    };

    @Override
    public String toString() {
        return "US[" + (id == null ? "?" : "" + id.getDbValue()) + "|" + title + "]";
    }
}
