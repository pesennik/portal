package com.github.pesennik.util;

import com.github.pesennik.Constants;
import com.github.pesennik.Mounts;
import com.github.pesennik.model.User;
import com.github.pesennik.page.user.UserProfileSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class RegistrationUtils {


    public static void sendWelcomeEmail(@NotNull User user, @NotNull String password) throws IOException {
        MailClient.sendMail(user.email, Constants.BRAND_NAME + " - регистрационные данные",
                "Поздравляем Вас с регистрацией на " + Constants.BRAND_NAME + "!\n" +
                        "Ваши данные: \n" +
                        "Логин: " + user.email + "\n" +
                        "Пароль: " + password + "\n" +
                        "Вы можете редактировать Ваши данные в персональных настройках " + Mounts.urlFor(UserProfileSettingsPage.class) + "\n\n" +
                        "Хороших песен!");
    }

    @Nullable
    public static String validatePassword(@Nullable String password1, @Nullable String password2) {
        if (TextUtils.isEmpty(password1) && TextUtils.isEmpty(password2)) {
            return "Пароли пусты!";
        }
        if (password1 == null || !password1.equals(password2)) {
            return "Пароли не совпадают";
        }
        if (password1.length() < Limits.PASSWORD_MIN_LENGTH || password1.length() > Limits.PASSWORD_MAX_LENGTH) {
            return password1.length() < Limits.PASSWORD_MIN_LENGTH ? "Пароль слишком короткий: менее " + Limits.PASSWORD_MIN_LENGTH + " символов." : "Пароль слишком длиный: более " + Limits.PASSWORD_MAX_LENGTH + " символов";
        }
        if (!isPrintableAsciiCharacters(password1)) {
            return "Пароль содержит недопустимые символы";
        }
        return null;
    }

    private static boolean isPrintableAsciiCharacters(@NotNull String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c <= 32 || c >= 128) {
                return false;
            }
        }
        return true;
    }

}
