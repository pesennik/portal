package com.github.pesennik.db.sql;

import com.github.pesennik.model.User;
import com.github.pesennik.model.UserId;
import com.github.mjdbc.Bind;
import com.github.mjdbc.BindBean;
import com.github.mjdbc.Sql;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Set of 'users' table queries
 */
public interface UsersSql {

    @Nullable
    @Sql("SELECT * FROM users WHERE id = :id")
    User selectUserById(@Bind("id") UserId userId);

    @Nullable
    @Sql("SELECT * FROM users WHERE login = :login")
    User selectUserByLogin(@Bind("login") String login);

    @Nullable
    @Sql("SELECT * FROM users WHERE email = :email")
    User selectUserByEmail(@Bind("email") String email);

    @Nullable
    @Sql("SELECT * FROM users WHERE login = :value OR email = :value")
    User selectUserByLoginOrEmail(@Bind("value") String value);

    @NotNull
    @Sql("INSERT INTO users (login, password_hash, email, uid, registration_date, termination_date, last_login_date, email_checked, settings, personal_info) " +
            "VALUES (:login, :passwordHash, :email, :uid, :registrationDate, :terminationDate, :lastLoginDate, :emailChecked, :settings, :personalInfo)")
    UserId insertUser(@BindBean User user);

    @Sql("UPDATE users SET email_checked = :v WHERE id = :id")
    void updateEmailCheckedFlag(@Bind("id") UserId id, @Bind("v") boolean v);

    @Sql("UPDATE users SET password_hash = :hash WHERE id = :id")
    void updatePasswordHash(@Bind("id") UserId id, @Bind("hash") String passwordHash);

    @Sql("UPDATE users SET settings = :settings WHERE id = :id")
    void updateSettings(@Bind("id") UserId id, @Bind("settings") String settings);
}
