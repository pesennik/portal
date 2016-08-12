package com.github.pesennik.model;

import com.github.mjdbc.type.DbString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ChordsViewMode implements DbString {
    Inlined("Inlined"),
    Hidden("Hidden");

    @NotNull
    private final String dbValue;

    private static final Map<String, ChordsViewMode> BY_DB_VALUE = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(v -> BY_DB_VALUE.put(v.dbValue, v));
    }

    ChordsViewMode(@NotNull String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    @NotNull
    public String getDbValue() {
        return dbValue;
    }

    @NotNull
    public String getJsValue() {
        return getDbValue();
    }

    @NotNull
    public static ChordsViewMode fromDbValue(@Nullable String val, @NotNull ChordsViewMode defaultValue) {
        return BY_DB_VALUE.getOrDefault(val, defaultValue);
    }
}
