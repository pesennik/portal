package com.github.pesennik.util;

import com.github.pesennik.Context;
import com.github.pesennik.UserSettings;
import com.github.pesennik.model.User;
import com.github.pesennik.page.HomePage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.util.string.StringValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pesennik.UserSession;
import com.github.pesennik.model.UserId;

import static org.apache.wicket.core.request.handler.RenderPageRequestHandler.RedirectPolicy.NEVER_REDIRECT;

/**
 * Set of user session management utils.
 */
public class UserSessionUtils {
    private static final Logger log = LoggerFactory.getLogger(UserSessionUtils.class);

    private static final String ID_PASSWORD_SEPARATOR_CHAR = ":";

    /**
     * Initializes session on first use: auto-login user and sets last used locale.
     */
    public static void initializeSession() {
        UserSession session = UserSession.get();
        if (session.isInitialized() || session.isSignedIn()) {
            return;
        }
        // try auto login user.
        try {
            String autoLoginData = UserSettings.getUserAutoLoginInfo();
            if (autoLoginData == null || autoLoginData.length() < 3) {
                return;
            }
            int sep = autoLoginData.indexOf(ID_PASSWORD_SEPARATOR_CHAR);
            if (sep < 1 || sep == autoLoginData.length() - 1) {
                return;
            }
            int id = StringValue.valueOf(autoLoginData.substring(0, sep)).toInt(-1);
            User user = id == -1 ? null : Context.getUsersDbi().getUserById(new UserId(id));
            if (user == null) {
                return;
            }
            String passwordHash = autoLoginData.substring(sep + 1);
            if (user.passwordHash.equals(passwordHash)) {
                login(user);
            }
        } finally {
            session.setInitialized(true);
        }
    }

    public static void login(@NotNull User user) {
        UserSession.get().setUser(user);
        UserSettings.setUserAutoLoginInfo(user.id.getDbValue() + ID_PASSWORD_SEPARATOR_CHAR + user.passwordHash);
        user.lastLoginDate = UDate.now();
        Context.getUsersDbi().updateLastLoginDate(user);
    }


    public static void logout() {
        UserSession session = UserSession.get();
        log.debug("Logging out: " + session.getUserLogin());
        UserId userId = session.getUserId();
        session.cleanOnLogout();
        UserSettings.setUserAutoLoginInfo(null);
        if (userId != null) {
            Context.getOnlineStatusManager().removeFromOnline(userId);
        }
    }

    public static boolean checkPassword(String password, String dbHash) {
        String hash = password2Hash(password);
        return hash.equals(dbHash);
    }

    public static String password2Hash(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    public static void redirectHomeIfSignedIn() {
        if (UserSession.get().isSignedIn()) {
            throw new RestartResponseException(new PageProvider(HomePage.class), NEVER_REDIRECT);
        }
    }
}
