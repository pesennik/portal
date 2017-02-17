package com.github.pesennik;

import com.github.mjdbc.Db;
import com.github.pesennik.db.dbi.UserSongsDbi;
import com.github.pesennik.db.dbi.UsersDbi;
import com.github.pesennik.db.dbi.impl.UserSongsDbiImpl;
import com.github.pesennik.db.dbi.impl.UsersDbiImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileInputStream;
import java.util.Properties;

public class Context {
    private static final Logger log = LoggerFactory.getLogger(Context.class);

    private static HikariDataSource ds;
    private static Db db;
    private static UsersDbi usersDbi;
    private static UserSongsDbi userSongsDbi;

    private static Properties prodConfig = new Properties();

    public static void init() {
        try {
            if (isProduction()) {
                prodConfig.load(new FileInputStream("/opt/pesennik/service.properties"));
            }
            ds = new HikariDataSource(prepareDbConfig("/hikari.properties"));
            db = Db.newInstance(ds);
            usersDbi = db.attachDbi(new UsersDbiImpl(db), UsersDbi.class);
            userSongsDbi = db.attachDbi(new UserSongsDbiImpl(db), UserSongsDbi.class);
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

    public static boolean isProduction() {
        return System.getProperty("pesennik.online.production") != null;
    }

    public static String getBaseUrl() {
        return isProduction() ? "https://pesennik.online" : "http://localhost:8080";
    }

    public static Properties getProdConfig() {
        return prodConfig;
    }

    @NotNull
    private static HikariConfig prepareDbConfig(@NotNull String resource) {
        HikariConfig dbConfig = new HikariConfig(resource);
        dbConfig.addDataSourceProperty("cachePrepStmts", "true");
        dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dbConfig.addDataSourceProperty("useServerPrepStmts", "true");
        return dbConfig;
    }

}
