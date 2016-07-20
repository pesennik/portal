package org.zametki.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.wicket.markup.html.form.FormComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {


    @NotNull
    public static String nonNull(@Nullable String val) {
        return nonNull(val, "");
    }

    @NotNull
    public static String nonNull(@Nullable String val, String def) {
        if (val == null) {
            return def;
        }
        return val;
    }

    @NotNull
    public static String nonNull(FormComponent<String> obj) {
        return nonNull(obj.getModelObject());
    }

    public static double parseDoubleAmount(@Nullable String val, double defaultValue) {
        String newBalanceText = nonNull(val).trim().replace("$", "").replace("%", "").replace(" ", "");
        if (newBalanceText.contains(",")) {
            if (newBalanceText.contains(".") || newBalanceText.indexOf(',') < newBalanceText.length() - 3) {
                newBalanceText = newBalanceText.replace(",", "");
            } else {
                newBalanceText = newBalanceText.replace(",", ".");
            }
        }
        return NumberUtils.toDouble(newBalanceText, defaultValue);
    }

    @NotNull
    public static String toSafeFileName(@NotNull String key) {
        Pattern p = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = p.matcher(key);
        return m.replaceAll("_");
    }

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

    public static String abbreviate(String text, int maxLen) {
        if (text == null || text.length() <= maxLen) {
            return text;
        }
        if (maxLen <= 1) {
            return text.substring(0, maxLen);
        }
        return text.substring(0, maxLen - 1) + "…";
    }

    public static boolean equals(String s1, String s2) {
        return (s1 == null && s2 == null) || (s1 != null && s1.equals(s2));
    }

    public static boolean isPrintableAsciiCharacters(String login) {
        for (int i = 0; i < login.length(); i++) {
            char c = login.charAt(i);
            if (c <= 32 || c >= 128) {
                return true;
            }
        }
        return false;
    }

    @Contract("!null -> !null")
    public static String trim(@Nullable String v) {
        if (isEmpty(v)) {
            return v;
        }
        return v.trim();
    }

    public static String toString(Object o, String defaultValue) {
        return o == null ? defaultValue : o.toString();
    }

    public static String trimToLen(@Nullable String text, int len) {
        if (text != null && text.length() > len) {
            return text.substring(0, len);
        }
        return text;
    }

    public static String notEmpty(String val, String def) {
        return isEmpty(val) ? def : val;
    }

    @NotNull
    public static String suffix(int size) {
        int rem = size % 10;
        return rem == 0 || rem >= 5 || (size >= 5 && size <= 20) ? "ов" : rem == 1 ? "" : "а";
    }

    /**
     * Limits text to given line numbers. Compacts duplicated empty lines.
     */
    @NotNull
    public static String limitLines(@NotNull String text, int nLines) {
        if (nLines <= 0) {
            return text;
        }
        String fixedText = text.replace('\r', '\n');
        int nActualLines = StringUtils.countMatches(fixedText, '\n');
        if (nActualLines <= nLines) {
            return text;
        }
        StringBuilder res = new StringBuilder();
        StringTokenizer st = new StringTokenizer(fixedText, "\n");
        for (int i = 0; i < nLines && st.hasMoreTokens(); ) {
            String line = st.nextToken();
            if (line.isEmpty()) {
                continue;
            }
            if (i > 0) {
                res.append("\n");
            }
            res.append(line);
            i++;
        }
        return res.toString();
    }

    @NotNull
    public static String joinQuoted(@NotNull Collection<String> values, char quoteChar) {
        StringBuilder res = new StringBuilder();
        for (String v : values) {
            if (res.length() > 0) {
                res.append(",");
            }
            res.append(quoteChar).append(v).append(quoteChar);
        }
        return res.toString();
    }

    /**
     * Returns true if the String length is in specified range.
     */
    @Contract("null,_,_ -> false")
    public static boolean isLengthInRange(@Nullable String val, int minLength, int maxLength) {
        return val != null && val.length() >= minLength && val.length() <= maxLength;
    }

}
