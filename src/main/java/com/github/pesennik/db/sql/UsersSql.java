package com.github.pesennik.db.sql;

import com.github.mjdbc.Bind;
import com.github.mjdbc.BindBean;
import com.github.mjdbc.Sql;
import com.github.pesennik.model.User;
import com.github.pesennik.model.UserId;
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

    @NotNull
    @Sql("INSERT INTO users (login, password_hash, email, uid, registration_date, termination_date, last_login_date, settings) " +
            "VALUES (:login, :passwordHash, :email, :uid, :registrationDate, :terminationDate, :lastLoginDate, :settings)")
    UserId insertUser(@BindBean User user);

    @Sql("UPDATE users SET password_hash = :hash WHERE id = :id")
    void updatePasswordHash(@Bind("id") UserId id, @Bind("hash") String passwordHash);

    @Sql("UPDATE users SET settings = :settings WHERE id = :id")
    void updateSettings(@BindBean User user);

    @Sql("UPDATE users SET last_login_date = :lastLoginDate WHERE id = :id")
    void updateLastLoginDate(@BindBean User user);
}
