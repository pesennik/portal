package org.zametki;

import org.apache.wicket.request.component.IRequestablePage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zametki.annotation.MountPath;
import org.zametki.page.HomePageWithMount;
import org.zametki.page.InternalErrorPage;
import org.zametki.page.PageNotFoundPage;
import org.zametki.page.signin.ByLinkAccountActivationPage;
import org.zametki.page.signin.EmailConfirmationPage;
import org.zametki.page.signin.ForgotPasswordPage;
import org.zametki.page.signin.LoginPage;
import org.zametki.page.signin.LogoutPage;
import org.zametki.page.signin.ManualAccountActivationPage;
import org.zametki.page.signin.Oauth2CallbackPage;
import org.zametki.page.signin.RegistrationCompletePage;
import org.zametki.page.signin.RegistrationPage;
import org.zametki.page.signin.RegistrationViaSocialNetworkPage;
import org.zametki.page.signin.ResetPasswordPage;
import org.zametki.page.user.UserProfileSettingsPage;

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
