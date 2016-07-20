package org.zametki.model;

import org.zametki.util.UDate;
import com.github.mjdbc.DbMapper;
import com.github.mjdbc.Mapper;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 *
 */
public class User extends Identifiable<UserId> {

    /**
     * Authentication.
     */
    public String login;
    public String passwordHash;
    public String email;
    public String uid; //32-symbols hash, to be used for personal resources mapping (like avatar)

    /**
     * Status.
     */
    public UDate registrationDate;
    public UDate terminationDate;

    /**
     * Tracking.
     */
    public UDate lastLoginDate;
    public boolean emailChecked;

    /**
     * Personal settings & info in JSON; //TODO: remove
     */
    public String settings;
    public String personalInfo;

    private UserPersonalInfo unpackedPersonalInfo;


    @NotNull
    public UserPersonalInfo unpackedPersonalInfo() {
        if (unpackedPersonalInfo == null) {
            unpackedPersonalInfo = new UserPersonalInfo(personalInfo);
        }
        return unpackedPersonalInfo;
    }

    @Mapper
    public static final DbMapper<User> MAPPER = r -> {
        User res = new User();
        res.id = new UserId(r.getInt("id"));
        res.login = r.getString("login");
        res.email = r.getString("email");
        res.uid = r.getString("uid");
        res.passwordHash = r.getString("password_hash");
        res.registrationDate = UDate.fromDate(requireNonNull(r.getTimestamp("registration_date")));
        res.terminationDate = UDate.fromDate(r.getTimestamp("termination_date"));
        res.lastLoginDate = UDate.fromDate(r.getTimestamp("last_login_date"));
        res.emailChecked = r.getBoolean("email_checked");
        res.settings = r.getString("settings");
        res.personalInfo = r.getString("personal_info");
        return res;
    };

}
