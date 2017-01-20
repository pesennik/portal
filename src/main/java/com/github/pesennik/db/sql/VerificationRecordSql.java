package com.github.pesennik.db.sql;

import com.github.pesennik.model.VerificationRecord;
import com.github.pesennik.model.VerificationRecordId;
import com.github.pesennik.util.UDate;
import com.github.pesennik.model.UserId;
import com.github.pesennik.model.VerificationRecordType;
import com.github.mjdbc.Bind;
import com.github.mjdbc.BindBean;
import com.github.mjdbc.Sql;
import org.jetbrains.annotations.NotNull;

public interface VerificationRecordSql {

    @Sql("UPDATE verification_record SET verification_date = :date WHERE id = :id")
    void updateVerificationDate(@Bind("id") VerificationRecordId id, @Bind("date") UDate date);

    @NotNull
    @Sql("INSERT INTO verification_record (hash, user_id, type, value, creation_date, verification_date) " +
            "VALUES (:hash, :userId, :type, :value, :creationDate, :verificationDate)")
    VerificationRecordId insertVerificationRecord(@BindBean VerificationRecord r);

    @Sql("DELETE FROM verification_record WHERE user_id = :user_id AND type = :type")
    void deleteAllUserVerificationsByType(@Bind("user_id") UserId id, @Bind("type") VerificationRecordType type);

    @Sql("SELECT * FROM verification_record WHERE id = :id")
    VerificationRecord selectVerificationRecordById(@Bind("id") VerificationRecordId id);

    @Sql("SELECT * FROM verification_record WHERE hash = :hash AND type = :type")
    VerificationRecord selectVerificationRecordByHashAndType(@Bind("hash") String hash, @Bind("type") VerificationRecordType type);
}
