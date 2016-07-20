package com.github.pesennik;

import com.github.pesennik.page.HomePageWithMount;
import com.github.pesennik.page.InternalErrorPage;
import com.github.pesennik.page.PageNotFoundPage;
import com.github.pesennik.page.signin.EmailConfirmationPage;
import com.github.pesennik.page.signin.RegistrationPage;
import com.github.pesennik.page.signin.ResetPasswordPage;
import com.github.pesennik.page.user.UserProfileSettingsPage;
import org.apache.wicket.request.component.IRequestablePage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.page.signin.ByLinkAccountActivationPage;
import com.github.pesennik.page.signin.ForgotPasswordPage;
import com.github.pesennik.page.signin.LoginPage;
import com.github.pesennik.page.signin.LogoutPage;
import com.github.pesennik.page.signin.ManualAccountActivationPage;
import com.github.pesennik.page.signin.Oauth2CallbackPage;
import com.github.pesennik.page.signin.RegistrationCompletePage;
import com.github.pesennik.page.signin.RegistrationViaSocialNetworkPage;

public class Mounts {
    static void mountAll(ZApplication app) {
        mountPages(app,
                // Base pages
                HomePageWithMount.class,
                InternalErrorPage.class,
                PageNotFoundPage.class,

                // Registration & sign in
                ByLinkAccountActivationPage.class,
                ByLinkAccountActivationPage.class,
                ForgotPasswordPage.class,
                EmailConfirmationPage.class,
                LoginPage.class,
                LogoutPage.class,
                ManualAccountActivationPage.class,
                Oauth2CallbackPage.class,
                RegistrationCompletePage.class,
                RegistrationPage.class,
                RegistrationViaSocialNetworkPage.class,
                ResetPasswordPage.class,

                // User settings
                UserProfileSettingsPage.class
        );
    }

    @Nullable
    public static String mountPath(@NotNull Class<? extends IRequestablePage> pageClass) {
        MountPath a = pageClass.getAnnotation(MountPath.class);
        return a == null ? null : mountPath(a.value());
    }

    @Nullable
    public static String mountPath(@NotNull String mountPathWithParams) {
        int first$Idx = mountPathWithParams.indexOf('$');
        if (first$Idx == -1) {
            return mountPathWithParams;
        }
        String res = mountPathWithParams.substring(0, first$Idx);
        int lastSlashIdx = res.lastIndexOf('/');
        return lastSlashIdx == -1 ? mountPathWithParams : res.substring(0, lastSlashIdx);
    }

    private static void mountPages(@NotNull ZApplication app, Class... classes) {
        for (Class cls : classes) {
            //noinspection unchecked
            app.mountPage(cls);
        }
    }

    @NotNull
    public static String urlFor(Class<? extends IRequestablePage> pageClass) {
        return Context.getBaseUrl() + mountPath(pageClass);
    }

    public static boolean isMounted(@NotNull Class<? extends IRequestablePage> pageClass) {
        return ZApplication.get().isMounted(pageClass);
    }
}
