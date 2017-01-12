package com.github.pesennik.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextUtils {


    @Contract("null -> true")
    public static boolean isEmpty(@Nullable String v) {
        return v == null || v.isEmpty();
    }

    public static String limit(@Nullable String t, int maxLen) {
        if (t == null || t.length() <= maxLen) {
            return t;
        }
        return t.substring(0, maxLen);
    }

    @SuppressWarnings("unused")
    public static String abbreviate(String text, int maxLen) {
        if (text == null || text.length() <= maxLen) {
            return text;
        }
        if (maxLen <= 1) {
            return text.substring(0, maxLen);
        }
        return text.substring(0, maxLen - 1) + "…";
    }


    public static String toString(Object o, String defaultValue) {
        return o == null ? defaultValue : o.toString();
    }

    @NotNull
    public static String suffix(int size) {
        int rem = size % 10;
        return rem == 0 || rem >= 5 || (size >= 5 && size <= 20) ? "ов" : rem == 1 ? "" : "а";
    }

    /**
     * Returns true if the String length is in specified range.
     */
    @Contract("null,_,_ -> false")
    public static boolean isLengthInRange(@Nullable String val, int minLength, int maxLength) {
        return val != null && val.length() >= minLength && val.length() <= maxLength;
    }

    @Contract("_,null->null")
    public static String notNull(@Nullable String text, @Nullable String defaultValue) {
        return text == null ? defaultValue : text;
    }

    public static void addSeparator(@NotNull StringBuilder res, @NotNull String separator) {
        if (res.length() != 0) {
            res.append(separator);
        }
    }
}
