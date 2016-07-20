package org.zametki.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public enum SocialNetworkType {
    GOOGLE("Google+", "google", "g", "fa fa-google-plus"),
    VK("VKontakte", "vk", "v", "fa fa-vk"),
    FACEBOOK("Facebook", "facebook", "f", "fa fa-facebook"),
    MAIL_RU("Mail.Ru", "mailru", "m", "vm-icon-mailru"),
    YANDEX("Yandex", "yandex", "y", "vm-icon-yandex"),
    ODNOKLASSNIKI("Odnoklassniki", "odnoklassniki", "o", "vm-icon-ok"),;

    @NotNull
    public final String displayName;

    @NotNull
    public final String oauthMount;

    @NotNull
    public final String key;

    @NotNull
    public final String iconClass;

    private static Map<String, SocialNetworkType> BY_KEY;
    private static Map<String, SocialNetworkType> BY_MOUNT;

    public static List<SocialNetworkType> ACTIVE_NETWORKS = Arrays.asList(GOOGLE, VK, FACEBOOK, MAIL_RU, YANDEX, ODNOKLASSNIKI);

    SocialNetworkType(@NotNull String displayName, @NotNull String name, @NotNull String key, @NotNull String iconClass) {
        this.displayName = displayName;
        this.oauthMount = name;
        this.key = key;
        this.iconClass = iconClass;
        register(this);
    }

    private static synchronized void register(@NotNull SocialNetworkType t) {
        if (BY_KEY == null) {
            BY_KEY = new HashMap<>();
        }
        BY_KEY.put(t.key, t);
        if (BY_MOUNT == null) {
            BY_MOUNT = new HashMap<>();
        }
        BY_MOUNT.put(t.oauthMount, t);
    }

    @Nullable
    public static SocialNetworkType fromOauthMount(@Nullable String mount) {
        return mount == null ? null : BY_MOUNT.get(mount.toLowerCase());
    }

    public static SocialNetworkType fromKey(@Nullable String key) {
        return key == null ? null : BY_KEY.get(key);
    }

    public String toString() {
        return "SN[" + displayName + "]";
    }
}
