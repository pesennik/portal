package com.github.pesennik.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static com.github.pesennik.db.util.DbUtils.optionalInstant;

/**
 *
 */
public class VerificationRecord extends Identifiable<VerificationRecordId> {

    public String hash;
    public UserId userId;
    public VerificationRecordType type;
    public String value;
    public Instant creationDate;
    public Instant verificationDate;

    public VerificationRecord() {
    }

    public VerificationRecord(@NotNull User user, @NotNull VerificationRecordType type, @NotNull String value) {
        this.userId = user.id;
        this.type = type;
        this.value = value;
        this.creationDate = Instant.now();
    }

    @Mapper
    public static final DbMapper<VerificationRecord> MAPPER = r -> {
        VerificationRecord res = new VerificationRecord();
        res.id = new VerificationRecordId(r.getInt("id"));
        res.hash = r.getString("hash");
        res.userId = new UserId(r.getInt("user_id"));
        res.type = VerificationRecordType.fromId(r.getInt("type"));
        res.value = r.getString("value");
        res.creationDate = r.getTimestamp("creation_date").toInstant();
        res.verificationDate = optionalInstant(r.getTimestamp("verification_date"));
        return res;
    };


}
