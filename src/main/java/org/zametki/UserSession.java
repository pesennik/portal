package org.zametki;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.jetbrains.annotations.Nullable;
import org.zametki.model.User;
import org.zametki.model.UserId;

import java.math.BigInteger;
import java.security.SecureRandom;

public class UserSession extends WebSession {

    private UserId userId;
    private boolean initialized;
    private String ip;

    private final String oauthSecureState = new BigInteger(130, new SecureRandom()).toString(32); // anti-forgery state token

    private String lastViewedPage; // used to redirect after oauth authorization

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

    public void setUser(UserId userId) {
        this.userId = userId;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void cleanOnLogout() {
        userId = null;
        setInitialized(false);
        getAttributeNames().forEach(this::removeAttribute);
        clear();
    }

    @Nullable
    public UserId getUserId() {
        return userId;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserLogin() {
        User user = getUser();
        return user == null ? null : user.login;
    }

    public void setUser(User user) {
        setUser(user == null ? null : user.id);
    }

    public String getOauthSecureState() {
        return oauthSecureState;
    }

    public String getLastViewedPage() {
        return lastViewedPage;
    }

    public void setLastViewedPage(String lastViewedPage) {
        this.lastViewedPage = lastViewedPage;
    }
}
