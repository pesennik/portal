package com.github.pesennik.util;

import org.apache.wicket.util.cookies.CookieDefaults;
import org.apache.wicket.util.cookies.CookieUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Cookies {

    private final static CookieDefaults NEVER_EXPIRE_COOKIE_DEFAULTS = new CookieDefaults();
    public final static CookieDefaults MONTH_1_COOKIE_DEFAULTS = new CookieDefaults();

    static {
        NEVER_EXPIRE_COOKIE_DEFAULTS.setMaxAge(Integer.MAX_VALUE);
        MONTH_1_COOKIE_DEFAULTS.setMaxAge(31 * DateTimeConstants.SECONDS_PER_DAY);
    }

    @Nullable
    public static String getCookieValue(@NotNull String key) {
        CookieUtils cookies = new CookieUtils(NEVER_EXPIRE_COOKIE_DEFAULTS);
        return cookies.load(key);
    }

    public static void setCookieValue(@NotNull String key, @Nullable String value, @NotNull CookieDefaults cookieDefaults) {
        CookieUtils cookies = new CookieUtils(cookieDefaults);
        cookies.save(key, value);
    }

}
