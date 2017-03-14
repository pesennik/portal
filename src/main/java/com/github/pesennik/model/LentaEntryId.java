package com.github.pesennik.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;

public class LentaEntryId extends AbstractId {

    public LentaEntryId(int value) {
        super(value);
    }

    @Mapper
    public static final DbMapper<LentaEntryId> MAPPER = r -> new LentaEntryId(r.getInt(1));
}
