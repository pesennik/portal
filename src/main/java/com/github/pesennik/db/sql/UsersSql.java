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
    @Sql("SELECT * FROM users WHERE email = :email")
    User selectUserByEmail(@Bind("email") String email);

    @NotNull
    @Sql("INSERT INTO users (password_hash, email, uid, registration_date, termination_date, last_login_date, settings, personal_info) " +
            "VALUES (:passwordHash, :email, :uid, :registrationDate, :terminationDate, :lastLoginDate, :settings, :personalInfo)")
    UserId insertUser(@BindBean User user);

    @Sql("UPDATE users SET password_hash = :hash WHERE id = :id")
    void updatePasswordHash(@Bind("id") UserId id, @Bind("hash") String passwordHash);

    @Sql("UPDATE users SET settings = :settings WHERE id = :id")
    void updateSettings(@BindBean User user);
}
