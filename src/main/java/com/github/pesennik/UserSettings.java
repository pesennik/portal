package com.github.pesennik;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.util.io.IClusterable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pesennik.model.User;
import com.github.pesennik.util.HttpUtils;

/**
 * Set of user settings serializable to JSON
 */
@SuppressWarnings("SpellCheckingInspection")
public class UserSettings implements IClusterable {
    private static final Logger log = LoggerFactory.getLogger(UserSettings.class);

    public static final String USER_SETTINGS = "zus";
    public static final String USER_AUTH_TOKEN = "zut";


    @NotNull
    public String toJSONString() {
        JSONObject obj = new JSONObject();
//        JsonUtils.putOpt(obj, language != Language.DEFAULT, "l", language.getDbValue());
        return obj.toString();
    }

    /**
     * Restores user settings from persistent storage: cookie for unregistered users or database.
     */
    @NotNull
    public static UserSettings get() {
        User user = UserSession.get().getUser();
        String val = user == null ? HttpUtils.getCookieValue(USER_SETTINGS) : user.settings;
        return jsonToSettings(val);
    }

    /**
     * Stores user settings in persistent storage: cookies for unregistered users of storage for registered users.
     */
    public static void set(UserSettings val) {
        UserSession session = UserSession.get();
        User user = session.getUser();
        if (user != null) {
            boolean changed = isChanged(jsonToSettings(user.settings), val);
            if (changed) {
                user.settings = val.toJSONString();
                try {
                    Context.getUsersDbi().updateUserSettings(user);
                } catch (Exception e) {
                    log.error("Failed to flush user settings!", e);
                }
            }
        } else {
            HttpUtils.setCookieValue(USER_SETTINGS, val.toJSONString());
        }
    }

    @NotNull
    private static UserSettings jsonToSettings(String json) {
        UserSettings res = new UserSettings();
        if (json == null || json.isEmpty()) {
            return res;
        }
        try {
            JSONObject obj = new JSONObject(json);
//            res.language = Language.fromDbValue(obj.optString("l"));
        } catch (Exception e) {
            log.warn("Error parsing settings json:'" + json + "' resetting...");
            return new UserSettings();
        }
        return res;
    }


    private static boolean isChanged(UserSettings before, UserSettings after) {
        return before == null || !before.equals(after);
    }

    /**
     * Sets user auth data to cookies. It will be reused to automatically sign in user on next visit.
     */
    public static void setUserAutoLoginInfo(@Nullable String authData) {
        HttpUtils.setCookieValue(USER_AUTH_TOKEN, authData, HttpUtils.MONTH_1_COOKIE_DEFAULTS);
    }

    /**
     * Gets user authentication data from cookies. Used to automatically sign in returning users.
     */
    @Nullable
    public static String getUserAutoLoginInfo() {
        return HttpUtils.getCookieValue(USER_AUTH_TOKEN);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserSettings that = (UserSettings) o;
        return new EqualsBuilder()
//                .append(language, that.language)
                .build();
    }
}
