package com.github.pesennik.util;

import com.github.pesennik.Constants;
import com.github.pesennik.Context;
import com.github.pesennik.model.User;
import com.github.pesennik.model.VerificationRecord;
import com.github.pesennik.page.user.UserProfileSettingsPage;
import com.github.pesennik.Mounts;
import com.github.pesennik.model.VerificationRecordType;
import com.github.pesennik.page.signin.ByLinkAccountActivationPage;
import com.github.pesennik.page.signin.ManualAccountActivationPage;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class RegistrationUtils {


    @NotNull
    public static VerificationRecord createEmailVerification(@NotNull User user) {
        Context.getUsersDbi().deleteAllUserVerificationsByType(user.id, VerificationRecordType.EmailValidation);

        VerificationRecord c = new VerificationRecord();
        c.userId = user.id;
        c.type = VerificationRecordType.EmailValidation;
        c.value = user.email;
        c.creationDate = UDate.now();
        Context.getUsersDbi().createVerificationRecord(c);
        return c;
    }

    public static void sendVerificationEmail(@NotNull User user, @NotNull VerificationRecord request) throws IOException {
        MailClient.sendMail(user.email, "Завершите регистрацию на " + Constants.BRAND_NAME,
                String.format("Перейдите по следующей ссылке для завершения регистрации: %s \n" +
                                "либо введите код %s вручную на странице %s",
                        Mounts.urlFor(ByLinkAccountActivationPage.class) + "/" + request.hash,
                        request.hash, Mounts.urlFor(ManualAccountActivationPage.class) + "/" + UrlEncoder.PATH_INSTANCE.encode(user.login, "UTF-8"))
        );
    }

    public static void sendWelcomeEmail(@NotNull User user, @NotNull String password) throws IOException {
        MailClient.sendMail(user.email, Constants.BRAND_NAME + " - регистрационные данные",
                "Поздравляем Вас с регистрацией на " + Constants.BRAND_NAME + "!\n" +
                        "Ваши данные: \n" +
                        "Логин: " + user.login + "\n" +
                        "Пароль: " + password + "\n" +
                        "Вы можете редактировать Ваши данные в персональных настройках " + Mounts.urlFor(UserProfileSettingsPage.class) + "\n\n" +
                        "Успешных инвестиций!");
    }

    public static void sendVerificationEmailWithUserData(@NotNull User user, @NotNull String password, @NotNull VerificationRecord request) throws Exception {
        MailClient.sendMail(user.email, "Завершите регистрацию на " + Constants.BRAND_NAME,
                String.format("До регистрации на " + Constants.BRAND_NAME + " остался один шаг!\n" +
                                "Перейдите по следующей ссылке для завершения регистрации: %s \n" +
                                "либо введите код %s вручную на странице %s\n" +
                                "Ваши данные:\n" +
                                "Логин: %s\n" +
                                "Пароль: %s\n",
                        Mounts.urlFor(ByLinkAccountActivationPage.class) + "/" + request.hash,
                        request.value, Mounts.urlFor(ManualAccountActivationPage.class) + "/" + UrlEncoder.PATH_INSTANCE.encode(user.login, "UTF-8"),
                        user.login, password)
        );
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
        if (TextUtils.isPrintableAsciiCharacters(password1)) {
            return "Пароль содержит недопустимые символы";
        }
        return null;
    }
}
