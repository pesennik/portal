package com.github.pesennik;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

public final class Constants {
    public static final String BRAND_NAME = "Песенник";
    public static final Locale RUSSIAN_LOCALE = Locale.forLanguageTag("ru");
    public static final TimeZone MOSCOW_TZ = TimeZone.getTimeZone("Europe/Moscow");
    public static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");
}
