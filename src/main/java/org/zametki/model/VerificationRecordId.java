package org.zametki.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;

/**
 *
 */
public final class VerificationRecordId extends AbstractId {

    public VerificationRecordId(int id) {
        super(id);
    }

    @Mapper
    public static final DbMapper<VerificationRecordId> MAPPER = r -> new VerificationRecordId(r.getInt(1));
}
