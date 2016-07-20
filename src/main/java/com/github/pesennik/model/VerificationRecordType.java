package com.github.pesennik.model;

import com.github.mjdbc.type.DbInt;
import org.jetbrains.annotations.NotNull;

public enum VerificationRecordType implements DbInt {
    EmailValidation(1),
    PasswordReset(2);

    private final int dbId;

    VerificationRecordType(int dbId) {
        this.dbId = dbId;
    }

    @NotNull
    public static VerificationRecordType fromId(int id) {
        if (id == EmailValidation.dbId) {
            return EmailValidation;
        } else if (id == PasswordReset.dbId) {
            return PasswordReset;
        }
        throw new IllegalArgumentException("Not valid id: " + id);
    }


    @Override
    public int getDbValue() {
        return dbId;
    }
}
