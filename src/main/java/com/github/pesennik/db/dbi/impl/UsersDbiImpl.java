package com.github.pesennik.db.dbi.impl;

import com.github.mjdbc.Db;
import com.github.pesennik.db.dbi.AbstractDbi;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.db.sql.UsersSql;
import com.github.pesennik.db.sql.VerificationRecordSql;
import com.github.pesennik.model.SocialNetworkType;
import com.github.pesennik.model.User;
import com.github.pesennik.model.UserId;
import com.github.pesennik.model.VerificationRecord;
import com.github.pesennik.model.VerificationRecordId;
import com.github.pesennik.model.VerificationRecordType;
import com.github.pesennik.util.TextUtils;
import com.github.pesennik.util.UDate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Database access interface for W7 DB
 */
public final class UsersDbiImpl extends AbstractDbi implements UsersDbi {

    @NotNull
    private final UsersSql usersSql;

    @NotNull
    private final VerificationRecordSql vrSql;

    public UsersDbiImpl(@NotNull Db db) {
        super(db);
        usersSql = db.attachSql(UsersSql.class);
        vrSql = db.attachSql(VerificationRecordSql.class);
    }

    @Override
    @Nullable
    public User getUserById(@Nullable UserId userId) {
        return userId == null ? null : usersSql.selectUserById(userId);
    }


    /**
     * Creates user, assigns uid and id. Repacks personal info and settings.
     */
    @Override
    public void createUser(@NotNull User user) {
        user.uid = UUID.randomUUID().toString();
        user.id = usersSql.insertUser(user);
    }

    @Override
    public void updateUserSettings(@NotNull User user) {
        usersSql.updateSettings(user);
    }

    @Override
    public void updateLastLoginDate(@NotNull User user) {
        //todo:
    }

    @Override
    public User getUserBySocialId(@NotNull SocialNetworkType networkType, @NotNull String socialId) {
        //todo:
        return null;
    }

    @Override
    @Nullable
    public User getUserByEmail(@Nullable String email) {
        return TextUtils.isEmpty(email) ? null : usersSql.selectUserByEmail(email);
    }

    @Override
    public void updatePersonalInfo(@NotNull User user) {
//todo:
    }

    @Override
    public void updatePassword(@NotNull User user, @Nullable VerificationRecord r) {
        assertTrue(r == null || r.type == VerificationRecordType.PasswordReset && user.id.equals(r.userId), () -> "Некорректная запись: " + r + " user: " + user);
        usersSql.updatePasswordHash(user.id, user.passwordHash);
        if (r != null) {
            vrSql.updateVerificationDate(r.id, UDate.now());
        }
    }

    /**
     * Creates verification record. Assigns hash to it.
     */
    @Override
    public void createVerificationRecord(@NotNull VerificationRecord r) {
        r.hash = UUID.randomUUID().toString();
        r.id = vrSql.insertVerificationRecord(r);
    }

    @Override
    public void deleteAllUserVerificationsByType(@NotNull UserId id, @NotNull VerificationRecordType type) {
        vrSql.deleteAllUserVerificationsByType(id, type);
    }

    @Override
    public VerificationRecord getVerificationRecordById(@NotNull VerificationRecordId id) {
        return vrSql.selectVerificationRecordById(id);
    }

    public VerificationRecord getVerificationRecordByHashAndType(@NotNull String hash, @NotNull VerificationRecordType type) {
        return vrSql.selectVerificationRecordByHashAndType(hash, type);
    }
}
