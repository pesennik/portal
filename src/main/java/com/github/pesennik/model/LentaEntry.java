package com.github.pesennik.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class LentaEntry extends Identifiable<LentaEntryId> {

    @NotNull
    public UserSongId userSongId = UserSongId.UNDEFINED;

    @NotNull
    public Instant sharingDate = Instant.MIN;

    @Mapper
    public static final DbMapper<LentaEntry> MAPPER = r -> {
        LentaEntry res = new LentaEntry();
        res.id = new LentaEntryId(r.getInt("id"));
        res.userSongId = new UserSongId(r.getInt("user_song_id"));
        res.sharingDate = r.getTime("sharing_date").toInstant();
        return res;
    };
}
