package com.github.pesennik.util;

import com.github.pesennik.Constants;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UDateFormat {

    @NotNull
    private final FastDateFormat df;
    private final boolean fixMonthNames;

    private UDateFormat(@NotNull String format, @NotNull TimeZone timeZone, @NotNull Locale locale) {
        df = FastDateFormat.getInstance(format, timeZone, locale);
        fixMonthNames = format.contains("MMM");
    }

    @NotNull
    public static UDateFormat getMskInstance(@NotNull String format) {
        return getInstance(format, Constants.MOSCOW_TZ);
    }

    @NotNull
    public static UDateFormat getInstance(@NotNull String format, @NotNull TimeZone timeZone) {
        return getInstance(format, timeZone, Constants.RUSSIAN_LOCALE);
    }

    @NotNull
    public static UDateFormat getInstance(@NotNull String format, @NotNull TimeZone timeZone, @NotNull Locale locale) {
        return new UDateFormat(format, timeZone, locale);
    }

    @NotNull
    public String format(@NotNull Instant instant) {
        String res = df.format(new Date(instant.toEpochMilli()));
        return fixMonthNames ? res.replace("май", "мая") : res;
    }

}
