package com.github.pesennik.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;
import com.github.pesennik.util.UDate;

import static java.util.Objects.requireNonNull;

/**
 * User's song
 */
public class UserSong extends Identifiable<UserSongId> {
    public UserId userId;
    public String title;
    public String author;
    public String text;
    public UDate creationDate;

    @Mapper
    public static final DbMapper<UserSong> MAPPER = r -> {
        UserSong res = new UserSong();
        res.id = new UserSongId(r.getInt("id"));
        res.userId = new UserId(r.getInt("user_id"));
        res.title = r.getString("title");
        res.author = r.getString("author");
        res.text = r.getString("text");
        res.creationDate = UDate.fromDate(requireNonNull(r.getTimestamp("creation_date")));
        return res;
    };

    @Override
    public String toString() {
        return "US[" + (id == null ? "?" : "" + id.getDbValue()) + "|" + title + "]";
    }
}
