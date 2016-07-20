package org.zametki.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;

/**
 *
 */
public final class UserId extends AbstractId {
    public UserId(int id) {
        super(id);
    }

    @Mapper
    public static final DbMapper<UserId> MAPPER = r -> new UserId(r.getInt(1));
}
