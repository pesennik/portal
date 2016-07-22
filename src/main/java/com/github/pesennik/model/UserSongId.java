package com.github.pesennik.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;

public class UserSongId extends AbstractId {
    public UserSongId(int value) {
        super(value);
    }

    @Mapper
    public static final DbMapper<UserSongId> MAPPER = r -> new UserSongId(r.getInt(1));
}
