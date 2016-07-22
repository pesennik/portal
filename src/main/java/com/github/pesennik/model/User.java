package com.github.pesennik.model;

import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;
import com.github.pesennik.util.UDate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class User extends Identifiable<UserId> {

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
    public UDate registrationDate = UDate.MIN_DATE;

    @Nullable
    public UDate terminationDate;

    /**
     * Tracking.
     */
    @NotNull
    public UDate lastLoginDate = UDate.MIN_DATE;

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
        res.registrationDate = UDate.fromDate(requireNonNull(r.getTimestamp("registration_date")));
        res.terminationDate = UDate.fromDate(r.getTimestamp("termination_date"));
        res.lastLoginDate = UDate.fromDate(r.getTimestamp("last_login_date"));
        res.settings = new UserSettings(r.getString("settings"));
        return res;
    };

    @Override
    public String toString() {
        return "U[" + (id == null ? "?" : "" + id.getDbValue()) + "|" + email + "]";
    }
}
