package org.zametki.util;

import org.zametki.Mounts;
import org.zametki.UserSession;
import org.zametki.model.SocialNetworkType;
import org.zametki.page.signin.Oauth2CallbackPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Base64;

public class OauthUtils {
    public static final String GOOGLE_CLIENT_ID = "TODO";
    public static final String GOOGLE_CLIENT_SECRET = "TODO";

    public static final String VK_CLIENT_ID = "TODO";
    public static final String VK_CLIENT_SECRET = "TODO";

    public static final String YANDEX_CLIENT_ID = "TODO";
    public static final String YANDEX_CLIENT_SECRET = "TODO";

    public static final String MAIL_CLIENT_ID = "TODO";
    public static final String MAIL_CLIENT_SECRET = "TODO";

    public static final String FACEBOOK_CLIENT_ID = "TODO";
    public static final String FACEBOOK_CLIENT_SECRET = "TODO";

    public static final String ODNOKLASSNIKI_CLIENT_ID = "TODO";
    public static final String ODNOKLASSNIKI_CLIENT_SECRET = "TODO";
    public static final String ODNOKLASSNIKI_APP_KEY = "TODO";


    @NotNull
    public static String getOauthUrl(SocialNetworkType sn, OauthLink.Action action) {
        switch (sn) {
            case GOOGLE:
                return "https://accounts.google.com/o/oauth2/auth?" +
                        "redirect_uri=" + getCallbackUrl(SocialNetworkType.GOOGLE) +
                        "&response_type=code&client_id=" + GOOGLE_CLIENT_ID +
                        "&state=" + UserSession.get().getOauthSecureState() + action +
                        "&scope=openid%20email";
            case VK:
                return "https://oauth.vk.com/authorize?" +
                        "client_id=" + VK_CLIENT_ID +
                        "&redirect_uri=" + getCallbackUrl(SocialNetworkType.VK) +
                        "&state=" + UserSession.get().getOauthSecureState() + action +
                        "&response_type=code&scope=email";
            case FACEBOOK:
                return "https://www.facebook.com/dialog/oauth?" +
                        "client_id=" + FACEBOOK_CLIENT_ID +
                        "&redirect_uri=" + getCallbackUrl(SocialNetworkType.FACEBOOK) +
                        "&response_type=code" +
                        "&scope=email" +
                        "&state=" + UserSession.get().getOauthSecureState() + action;
            case MAIL_RU:
                return "https://connect.mail.ru/oauth/authorize?" +
                        "client_id=" + MAIL_CLIENT_ID +
                        "&response_type=code" +
                        "&redirect_uri=" + getCallbackUrl(SocialNetworkType.MAIL_RU) +
                        "&state=" + UserSession.get().getOauthSecureState() + action;
            case YANDEX:
                return "https://oauth.yandex.ru/authorize?response_type=code" +
                        "&client_id=" + YANDEX_CLIENT_ID +
                        "&state=" + UserSession.get().getOauthSecureState() + action;
            case ODNOKLASSNIKI:
                return "http://www.odnoklassniki.ru/oauth/authorize?" +
                        "client_id=" + ODNOKLASSNIKI_CLIENT_ID +
                        "&response_type=code&" +
                        "redirect_uri=" + getCallbackUrl(SocialNetworkType.ODNOKLASSNIKI) +
                        "&state=" + UserSession.get().getOauthSecureState() + action;
        }
        throw new IllegalArgumentException("SN: " + sn);
    }

    @Nullable
    public static OauthLink.Action getActionFromState(String state) {
        for (OauthLink.Action a : OauthLink.Action.values()) {
            if (state.endsWith(a.toString())) {
                return a;
            }
        }
        return null;
    }

    public static String rejectActionFromState(String state) {
        for (OauthLink.Action a : OauthLink.Action.values()) {
            if (state.endsWith(a.toString())) {
                return state.substring(0, state.length() - a.toString().length());
            }
        }
        return state;
    }

    public static String getClaims(String token) {
        int firstPeriod = token.indexOf('.');
        int lastPeriod = token.lastIndexOf('.');
        if (firstPeriod <= 0 || lastPeriod <= firstPeriod) {
            throw new IllegalArgumentException("JWT must have 3 tokens");
        }
        CharBuffer buffer = CharBuffer.wrap(token, 0, firstPeriod);
        buffer.limit(lastPeriod).position(firstPeriod + 1);
        byte[] res = Base64.getUrlDecoder().decode(utf8Encode(buffer));
        return new String(res);
    }

    /**
     * UTF-8 encoding/decoding. Using a charset rather than `String.getBytes` is less forgiving
     * and will raise an exception for invalid data.
     */
    private static byte[] utf8Encode(CharSequence string) {
        try {
            ByteBuffer bytes = Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(string));
            byte[] bytesCopy = new byte[bytes.limit()];
            System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());
            return bytesCopy;
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCallbackUrl(@NotNull SocialNetworkType sn) {
        return Mounts.urlFor(Oauth2CallbackPage.class) + "/" + sn.oauthMount;
    }
}
