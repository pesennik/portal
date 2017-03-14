package com.github.pesennik.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;

public final class UserId extends AbstractId {
    public static final UserId UNDEFINED = new UserId(-1);
    public static final UserId SYSTEM_USER_ID = new UserId(0);

    public UserId(int id) {
        super(id);
    }

    @Mapper
    public static final DbMapper<UserId> MAPPER = r -> new UserId(r.getInt(1));
}
