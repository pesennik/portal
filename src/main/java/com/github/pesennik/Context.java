package com.github.pesennik;

import com.github.mjdbc.Db;
import com.github.pesennik.db.dbi.UserSongsDbi;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.db.dbi.impl.UserSongsDbiImpl;
import com.github.pesennik.db.dbi.impl.UsersDbiImpl;
import com.github.pesennik.service.OnlineStatusManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class Context {
    private static final Logger log = LoggerFactory.getLogger(Context.class);

    private static HikariDataSource ds;
    private static Db db;
    private static UsersDbi usersDbi;
    private static UserSongsDbi userSongsDbi;
    private static OnlineStatusManager onlineStatusManager;

    public static void init() {
        try {
            ds = new HikariDataSource(new HikariConfig("/hikari.properties"));
            db = Db.newInstance(ds);
            usersDbi = db.attachDbi(new UsersDbiImpl(db), UsersDbi.class);
            userSongsDbi = db.attachDbi(new UserSongsDbiImpl(db), UserSongsDbi.class);
            onlineStatusManager = new OnlineStatusManager();
        } catch (Exception e) {
            log.error("", e);
            shutdown();
            throw new RuntimeException(e);
        }
    }

    public static void shutdown() {
        close(ds);
    }

    private static void close(Closeable c) {
        try {
            c.close();
        } catch (Exception ignored) {
        }
    }

    public static Db getDb() {
        return db;
    }

    public static UsersDbi getUsersDbi() {
        return usersDbi;
    }

    public static UserSongsDbi getUserSongsDbi() {
        return userSongsDbi;
    }

    public static OnlineStatusManager getOnlineStatusManager() {
        return onlineStatusManager;
    }

    public static String getBaseUrl() {
        return Constants.BASE_URL;
    }
}
