package com.github.pesennik;

import com.github.pesennik.model.User;
import com.github.pesennik.model.UserId;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.security.SecureRandom;

public class UserSession extends WebSession {

    @Nullable
    private UserId userId;

    @Nullable
    private String userLogin;

    private boolean initialized;

    @Nullable
    public String ip;

    @NotNull
    public final String oauthSecureState = new BigInteger(130, new SecureRandom()).toString(32); // anti-forgery state token

    @Nullable
    public String lastViewedPage; // used to redirect after oauth authorization

    public UserSession(Request request) {
        super(request);
        setLocale(Constants.RUSSIAN_LOCALE);
    }

    public boolean isSignedIn() {
        return userId != null;
    }

    public static UserSession get() {
        return (UserSession) WebSession.get();
    }

    @Nullable
    public User getUser() {
        return userId == null ? null : Context.getUsersDbi().getUserById(userId);
    }

    public void setUser(@NotNull User user) {
        this.userId = user.id;
        this.userLogin = user.login;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void cleanOnLogout() {
        userId = null;
        userLogin = null;
        setInitialized(false);
        getAttributeNames().forEach(this::removeAttribute);
        clear();
    }

    @Nullable
    public UserId getUserId() {
        return userId;
    }

    @Nullable
    public String getUserLogin() {
        return userLogin;
    }

    public String toString() {
        String res = "session:" + hashCode();
        User user = getUser();
        if (user != null) {
            res += "|user:" + user.login;
        }
        res += "|ip:" + ip;
        return res;
    }

    @Nullable
    public String getUserEmail() {
        User user = getUser();
        return user == null ? null : user.email;
    }
}
