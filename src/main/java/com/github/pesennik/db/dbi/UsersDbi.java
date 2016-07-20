package com.github.pesennik.db.dbi;

import com.github.pesennik.model.SocialNetworkType;
import com.github.pesennik.model.User;
import com.github.pesennik.model.VerificationRecord;
import com.github.pesennik.model.VerificationRecordId;
import com.github.pesennik.model.VerificationRecordType;
import com.github.pesennik.model.UserId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface UsersDbi {

    @Nullable
    User getUserById(@Nullable UserId userId);

    @Nullable
    User getUserByLogin(@Nullable String login);


    @Nullable
    User getUserByLoginOrEmail(@Nullable String loginOrEmail);

    void createUser(@NotNull User user);

    void updateUserSettings(@NotNull User user);

    void updateLastLoginDate(@NotNull User user);

    @Nullable
    User getUserBySocialId(@NotNull SocialNetworkType networkType, @NotNull String socialId);

    @Nullable
    User getUserByEmail(@Nullable String email);

    void updateEmailCheckedFlag(@NotNull User user, @Nullable VerificationRecord r);

    void updatePersonalInfo(@NotNull User user);

    void updatePassword(@NotNull User user, @Nullable VerificationRecord r);

    void createVerificationRecord(@NotNull VerificationRecord r);

    void deleteAllUserVerificationsByType(@NotNull UserId id, @NotNull VerificationRecordType type);

    @Nullable
    VerificationRecord getVerificationRecordById(@NotNull VerificationRecordId id);

    @Nullable
    VerificationRecord getVerificationRecordByHashAndType(@NotNull String hash, @NotNull VerificationRecordType type);
}