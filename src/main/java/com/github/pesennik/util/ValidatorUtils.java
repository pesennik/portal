package com.github.pesennik.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Set of validation methods for login, email, first and last name etc..
 */
public class ValidatorUtils {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z]{2,}){1}$)", Pattern.CASE_INSENSITIVE);

    private static final Set<Character> VALID_LOGIN_CHARS = new HashSet<Character>() {{
        addAll(Arrays.asList('-', '_', '.', ' '));
    }};

    /**
     * Checks if the provided string is valid user password.
     *
     * @param password - string to check, may be null.
     * @return true if {@code password} is a valid user password. Otherwise returns false.
     */
    public static boolean isValidPassword(@Nullable String password) {
        return TextUtils.isLengthInRange(password, Limits.PASSWORD_MIN_LENGTH, Limits.PASSWORD_MAX_LENGTH);
    }

    /**
     * Checks if the provided string is valid email address.
     *
     * @param email - string to check, may be null.
     * @return true if {@code email} is a valid email address. Otherwise returns false.
     */
    public static boolean isValidEmail(@Nullable String email) {
        return TextUtils.isLengthInRange(email, Limits.EMAIL_MIN_LENGTH, Limits.EMAIL_MAX_LENGTH) && EMAIL_PATTERN.matcher(email).matches();
    }


    /**
     * Checks if the provided string is valid first or last name.
     *
     * @param name - string to check, may be null.
     * @return true if {@code name} is a valid first or last name. Otherwise returns false.
     */
    public static boolean isValidFirstOrLastName(@Nullable String name) {
        if (!TextUtils.isLengthInRange(name, Limits.FIRST_LAST_NAME_MIN_LENGTH, Limits.FIRST_LAST_NAME_MAX_LENGTH)) {
            return false;
        }
        char[] chars = name.toCharArray();
        if (!Character.isLetter(chars[0]) || !Character.isLetter(chars[chars.length - 1])) {
            return false;
        }
        for (char c : name.toCharArray()) {
            if (!Character.isLetter(c) && !(c == '-' || c == ' ' || c == '.')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the specified string is a valid HEX color in 3 or 6 characters form: #FFF or #FFFFFF.
     *
     * @param value - color string, may be null.
     * @return true if {code value}  parameter is a valid HEX color representation. Otherwise returns false.
     */
    public static boolean isValidHexColor(@Nullable String value) {
        return !TextUtils.isEmpty(value) && HEX_COLOR_PATTERN.matcher(value).matches();
    }


    /**
     * Returns true if the String length is in specified range.
     */
    @Contract("null,_,_ -> false")
    public static boolean isLengthInRange(@Nullable String val, int minLength, int maxLength) {
        return val != null && val.length() >= minLength && val.length() <= maxLength;
    }

    /**
     * Checks if the provided string is valid user login.
     *
     * @param login - string to check, may be null.
     * @return true if {@code login} is a valid user login. Otherwise returns false.
     */
    public static boolean isValidLogin(@Nullable String login) {
        if (!isLengthInRange(login, Limits.LOGIN_MIN_LENGTH, Limits.LOGIN_MAX_LENGTH)) {
            return false;
        }
        char[] chars = login.toCharArray();
        if (!Character.isLetter(chars[0]) || !Character.isLetterOrDigit(chars[chars.length - 1])) {
            return false;
        }
        for (char c : chars) {
            if (!(Character.isLetterOrDigit(c) || VALID_LOGIN_CHARS.contains(c))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidSongTitle(@Nullable String title) {
        return isLengthInRange(title, 2, Limits.MAX_SONG_TITLE_LENGTH);
    }

    public static boolean isValidSongAuthor(@Nullable String author) {
        return isLengthInRange(author, 0, Limits.MAX_SONG_AUTHOR_LENGTH);
    }

    public static boolean isValidSongText(@Nullable String text) {
        return isLengthInRange(text, 0, Limits.MAX_SONG_TEXT_LENGTH);
    }

}
