package org.zametki.db.dbi.impl;

import org.zametki.db.dbi.AbstractDbi;
import org.zametki.db.dbi.UsersDbi;
import org.zametki.db.sql.UsersSql;
import org.zametki.db.sql.VerificationRecordSql;
import org.zametki.model.SocialNetworkType;
import org.zametki.model.User;
import org.zametki.model.UserId;
import org.zametki.model.VerificationRecord;
import org.zametki.model.VerificationRecordId;
import org.zametki.model.VerificationRecordType;
import org.zametki.util.HashUtils;
import org.zametki.util.TextUtils;
import org.zametki.util.UDate;
import com.github.mjdbc.Db;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Override
    @Nullable
    public User getUserByLogin(@Nullable String login) {
        return TextUtils.isEmpty(login) ? null : usersSql.selectUserByLogin(login);
    }

    @Override
    @Nullable
    public User getUserByLoginOrEmail(@Nullable String loginOrEmail) {
        return TextUtils.isEmpty(loginOrEmail) ? null : usersSql.selectUserByLoginOrEmail(loginOrEmail);
    }

    /**
     * Creates user, assigns uid and id. Repacks personal info and settings.
     */
    @Override
    public void createUser(@NotNull User user) {
        user.uid = HashUtils.generateRandomUid();
        user.personalInfo = user.unpackedPersonalInfo().pack();
        user.id = usersSql.insertUser(user);
    }

    @Override
    public void updateUserSettings(@NotNull User user) {
        usersSql.updateSettings(user.id, user.settings);
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
    public void updateEmailCheckedFlag(@NotNull User user, @Nullable VerificationRecord r) {
        assertTrue(r == null || r.type == VerificationRecordType.EmailValidation && user.id.equals(r.userId), () -> "Некорректная запись: " + r + " user: " + user);
        usersSql.updateEmailCheckedFlag(user.id, true);
        if (r != null) {
            vrSql.updateVerificationDate(r.id, UDate.now());
        }
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
        r.hash = HashUtils.generateRandomUid();
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
