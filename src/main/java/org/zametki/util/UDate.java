package org.zametki.util;

import com.github.mjdbc.type.DbTimestamp;
import org.apache.wicket.util.io.IClusterable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Wrapper over Instant class with a set of handy methods.
 * Must always be considered as Date & Time in UTC Timezone.
 */
public final class UDate implements DbTimestamp, IClusterable {

    @NotNull
    public final Instant instant;

    public UDate(@NotNull Instant instant) {
        this.instant = instant;
    }

    /**
     * Optimized version to return current time with precision of 1 second.
     */

    public static UDate now() {
        return new UDate(Instant.now());
    }

    public boolean isAfter(UDate date) {
        return instant.isAfter(date.instant);
    }

    public boolean isBefore(UDate date) {
        return instant.isBefore(date.instant);
    }

    public UDate minus(int n, ChronoUnit unit) {
        return new UDate(instant.minus(n, unit));
    }

    public UDate minusMillis(long millis) {
        return new UDate(instant.minusMillis(millis));
    }

    @Override
    public String toString() {
        return instant.toString();
    }

    public UDate minusHours(int hours) {
        return new UDate(instant.minus(hours, ChronoUnit.HOURS));
    }

    @Override
    public Timestamp getDbValue() {
        return new Timestamp(instant.toEpochMilli());
    }

    @Contract("null -> null")
    public static UDate fromDate(@Nullable Date date) {
        return date == null ? null : new UDate(date.toInstant());
    }
}
