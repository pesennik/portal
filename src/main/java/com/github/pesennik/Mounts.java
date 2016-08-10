package com.github.pesennik;

import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.page.CreateUserSongPage;
import com.github.pesennik.page.InternalErrorPage;
import com.github.pesennik.page.PageNotFoundPage;
import com.github.pesennik.page.TunerPage;
import com.github.pesennik.page.UserSongsListPage;
import com.github.pesennik.page.signin.ForgotPasswordPage;
import com.github.pesennik.page.signin.ResetPasswordPage;
import com.github.pesennik.page.user.UserProfileSettingsPage;
import org.apache.wicket.request.component.IRequestablePage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Mounts {
    static void mountAll(ZApplication app) {
        mountPages(app,
                // Base pages
                InternalErrorPage.class,
                PageNotFoundPage.class,

                // Reset passwords
                ForgotPasswordPage.class,
                ResetPasswordPage.class,

                // User pages
                CreateUserSongPage.class,
                UserSongsListPage.class,
                UserProfileSettingsPage.class,

                // Public resources
                TunerPage.class
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
