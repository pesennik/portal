package com.github.pesennik.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

import static com.github.pesennik.db.util.DbUtils.optionalInstant;

public class User extends Identifiable<UserId> {

    public static final int LOGIN_MIN_LENGTH = 3;
    public static final int LOGIN_MAX_LENGTH = 30;

    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 30;

    public static final int EMAIL_MIN_LENGTH = 8;
    public static final int EMAIL_MAX_LENGTH = 50;

    public static final int FIRST_LAST_NAME_MIN_LENGTH = 2;
    public static final int FIRST_LAST_NAME_MAX_LENGTH = 30;

    /**
     * Authentication.
     */
    @NotNull
    public String login = "";

    @NotNull
    public String passwordHash = "";

    @NotNull
    public String email = "";

    @NotNull
    public String uid = ""; //36-symbols UUID based hash, to be used for personal resources mapping (like avatar)

    /**
     * Status.
     */
    @NotNull
    public Instant registrationDate = Instant.MIN;

    @Nullable
    public Instant terminationDate;

    /**
     * Tracking.
     */
    @NotNull
    public Instant lastLoginDate = Instant.MIN;

    @NotNull
    public UserSettings settings = new UserSettings("");

    @Mapper
    public static final DbMapper<User> MAPPER = r -> {
        User res = new User();
        res.id = new UserId(r.getInt("id"));
        res.login = r.getString("login");
        res.uid = r.getString("uid");
        res.email = r.getString("email");
        res.passwordHash = r.getString("password_hash");
        res.registrationDate = r.getTimestamp("registration_date").toInstant();
        res.terminationDate = optionalInstant(r.getTimestamp("termination_date"));
        res.lastLoginDate = optionalInstant(r.getTimestamp("last_login_date"));
        res.settings = new UserSettings(r.getString("settings"));
        return res;
    };

    @Override
    public String toString() {
        return "U[" + (id == null ? "?" : "" + id.getDbValue()) + "|" + email + "]";
    }
}
