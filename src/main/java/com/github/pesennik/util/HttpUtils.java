package com.github.pesennik.util;

import com.github.pesennik.UserSession;
import com.github.pesennik.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.cookies.CookieDefaults;
import org.apache.wicket.util.cookies.CookieUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class HttpUtils {

    public final static CookieDefaults NEVER_EXPIRE_COOKIE_DEFAULTS = new CookieDefaults();
    public final static CookieDefaults MONTH_1_COOKIE_DEFAULTS = new CookieDefaults();

    static {
        NEVER_EXPIRE_COOKIE_DEFAULTS.setMaxAge(Integer.MAX_VALUE);
        MONTH_1_COOKIE_DEFAULTS.setMaxAge(31 * DateTimeConstants.SECONDS_PER_DAY);
    }

    @Nullable
    public static String getCookieValue(@NotNull String key) {
        Cookie cookie = ((WebRequest) RequestCycle.get().getRequest()).getCookie(key);
        return cookie == null ? null : cookie.getValue();
    }

    public static void setCookieValue(@NotNull String key, @Nullable String v) {
        setCookieValue(key, v, NEVER_EXPIRE_COOKIE_DEFAULTS);
    }

    public static void setCookieValue(@NotNull String key, @Nullable String v, @NotNull CookieDefaults cookieDefaults) {
        new CookieUtils(cookieDefaults).save(key, v);
    }

    public static void saveLastViewedPage(HttpServletRequest r) {
        String referer = r.getHeader("Referer");
        if (referer == null) {
            String query = r.getQueryString();
            referer = r.getRequestURI() + (TextUtils.isEmpty(query) ? "" : "?" + query);
        }
        UserSession.get().lastViewedPage = referer;
    }

    public static void redirectToLastViewedPage(Component component) {
        String referer = UserSession.get().lastViewedPage;
        if (TextUtils.isEmpty(referer)) {
            component.setResponsePage(HomePage.class);
        } else {
            component.setResponsePage(new RedirectPage(referer));
            UserSession.get().lastViewedPage = null;
        }
    }


}

