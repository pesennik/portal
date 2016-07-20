package org.zametki.page.signin;

import org.zametki.Context;
import org.zametki.UserSession;
import org.zametki.annotation.MountPath;
import org.zametki.db.dbi.UsersDbi;
import org.zametki.model.SocialNetworkType;
import org.zametki.model.User;
import org.zametki.util.DigestUtils;
import org.zametki.util.HttpIO;
import org.zametki.util.HttpUtils;
import org.zametki.util.OauthLink;
import org.zametki.util.OauthUtils;
import org.zametki.util.TextUtils;
import org.zametki.util.UserSessionUtils;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.zametki.page.signin.LoginPage.errorParams;

/**
 * TODO: vulnerability with checked/unchecked emails
 */
@MountPath("/oauth2callback")
public class Oauth2CallbackPage extends WebPage {
    private static final Logger log = LoggerFactory.getLogger(Oauth2CallbackPage.class);

    public static final String NETWORK_NAME = "network";

    public static final int MAX_OAUTH_RESPONSE_SIZE = 200 * 1000;

    public final Map<SocialNetworkType, NetworkHandler> HANDLERS = new HashMap<SocialNetworkType, NetworkHandler>() {
        {
            put(SocialNetworkType.GOOGLE, new GoogleHandler());
            put(SocialNetworkType.VK, new VkHandler());
            put(SocialNetworkType.YANDEX, new YandexHandler());
            put(SocialNetworkType.MAIL_RU, new MailHandler());
            put(SocialNetworkType.FACEBOOK, new FacebookHandler());
            put(SocialNetworkType.ODNOKLASSNIKI, new OdnoklassnikiHandler());
        }
    };

    private OauthLink.Action action;

    public Oauth2CallbackPage(PageParameters pp) {
        super(pp);
        setStatelessHint(true);

        String error = pp.get("error").toString();
        if (!TextUtils.isEmpty(error)) {
            if (error.equals("access_denied")) {
                HttpUtils.redirectToLastViewedPage(Oauth2CallbackPage.this);
            } else {
                log.error("Response contains error: " + error);
                setResponsePage(LoginPage.class, errorParams("Во время идентификации произошла ошибка. Пожалуйста, заполните данные вручную."));
            }
            return;
        }

        SocialNetworkType network = SocialNetworkType.fromOauthMount(pp.get(NETWORK_NAME).toString());
        NetworkHandler handler = network == null ? null : HANDLERS.get(network);
        if (handler == null) {
            HttpUtils.redirectToLastViewedPage(Oauth2CallbackPage.this);
            return;
        }
        try {
            RegistrationViaSocialNetworkPage.SocialRegData socialData = handler.obtainUserData(pp);
            User user = UserSession.get().getUser();
            if (user != null) { // user is binding his social account with local account
                if (action == OauthLink.Action.LOGIN) {
                    throw new IllegalStateException("User " + user.login + " is logged in and action is LOGIN");
                }
                if (action == OauthLink.Action.BIND_ACCOUNT) {
                    bindUserAccount(user, socialData);
                } else if (action == OauthLink.Action.BIND_AVATAR) {
                    bindUserAvatar(user, socialData);
                }
                HttpUtils.redirectToLastViewedPage(Oauth2CallbackPage.this);
                return;
            }

            if (action != OauthLink.Action.LOGIN) {
                throw new IllegalStateException("Unexpected action " + action);
            }
            if (doUserLogin(socialData)) {
                HttpUtils.redirectToLastViewedPage(Oauth2CallbackPage.this);
            } else {
                setResponsePage(new RegistrationViaSocialNetworkPage(socialData));
            }
        } catch (IllegalStateException e) {
            log.warn("Strange attempt to login using " + network + " was detected. " + e.getMessage());
            setResponsePage(LoginPage.class, errorParams("Уникальный ключ не совпадает! Если Вы считаете, что попали сюда по правильной ссылке, пожалуйста, сообщите об этой ошибке на todo@todo"));
        } catch (IOException e) { // happens with VK sometimes
            if (action != OauthLink.Action.LOGIN) {
                HttpUtils.redirectToLastViewedPage(Oauth2CallbackPage.this);
            } else {
                setResponsePage(LoginPage.class, errorParams("Не удалось соединиться с сервером. Пожалуйста, заполните данные вручную."));
            }
        } catch (Exception e) {
            log.error("Exception while fetching " + network + " data. Message: " + e.getMessage(), e);
            if (action != OauthLink.Action.LOGIN) {
                HttpUtils.redirectToLastViewedPage(Oauth2CallbackPage.this);
            } else {
                setResponsePage(LoginPage.class, errorParams("Во время идентификации произошла ошибка. Пожалуйста, заполните данные вручную."));
            }
        }
    }

    private boolean doUserLogin(@NotNull RegistrationViaSocialNetworkPage.SocialRegData sd) {
        UsersDbi dao = Context.getUsersDbi();

        User user = dao.getUserBySocialId(sd.network, sd.socialId);
        if (user == null && !TextUtils.isEmpty(sd.email)) {
            user = dao.getUserByEmail(sd.email);
            if (user != null) {
                if (!user.emailChecked) {
                    user.emailChecked = true; // TODO:
                    dao.updateEmailCheckedFlag(user, null);
                }
                user.unpackedPersonalInfo().socialIds.put(sd.network, sd.socialId);
                if (TextUtils.isEmpty(user.unpackedPersonalInfo().firstName)) {
                    user.unpackedPersonalInfo().firstName = sd.firstName;
                }
                if (TextUtils.isEmpty(user.unpackedPersonalInfo().lastName)) {
                    user.unpackedPersonalInfo().lastName = sd.lastName;
                }
                dao.updatePersonalInfo(user);
            }
        }

        if (user != null) {
//            try {
//                if (!AvatarUtils.hasPersonalAvatar(user)) {
//                    AvatarUtils.fetchAndSaveAvatar(sd.avatarUrl, user);
//                }
//            } catch (Exception e) {
//                log.error("Error during fetching/saving user avatar: ", e);
//            }

            // TODO: vulnerability - green gate for all
            UserSessionUtils.login(user);
            return true;
        }
        return false;
    }

    private void bindUserAccount(@NotNull User sessionUser, @NotNull RegistrationViaSocialNetworkPage.SocialRegData sd) {
        User userWithSocialId = Context.getUsersDbi().getUserBySocialId(sd.network, sd.socialId);
        if (userWithSocialId != null && userWithSocialId.id.equals(sessionUser.id)) {
            log.warn("User with same social data was found. Session user: " + sessionUser.login + ", founded user: " + userWithSocialId.login);
            // TODO: what we should do?
        }
        sessionUser.unpackedPersonalInfo().socialIds.put(sd.network, sd.socialId);
        if (TextUtils.isEmpty(sessionUser.unpackedPersonalInfo().firstName)) {
            sessionUser.unpackedPersonalInfo().firstName = sd.firstName;
        }
        if (TextUtils.isEmpty(sessionUser.unpackedPersonalInfo().lastName)) {
            sessionUser.unpackedPersonalInfo().lastName = sd.lastName;
        }
        Context.getUsersDbi().updatePersonalInfo(sessionUser);

//        try {
//            if (!AvatarUtils.hasPersonalAvatar(sessionUser)) {
//                AvatarUtils.fetchAndSaveAvatar(sd.avatarUrl, sessionUser);
//            }
//        } catch (Exception e) {
//            log.error("Error during fetching/saving user avatar: ", e);
//        }
    }

    private void bindUserAvatar(@NotNull User sessionUser, @NotNull RegistrationViaSocialNetworkPage.SocialRegData sd) throws IOException {
//        AvatarUtils.fetchAndSaveAvatar(sd.avatarUrl, sessionUser);
    }

    private abstract class NetworkHandler {
        @NotNull
        public RegistrationViaSocialNetworkPage.SocialRegData obtainUserData(PageParameters pp) throws IOException, JSONException {
            String state = pp.get("state").toString();
            action = OauthUtils.getActionFromState(state);
            if (action == null) {
                throw new IllegalStateException("Cannot detect action by state! State: '" + state + "'");
            }
            state = OauthUtils.rejectActionFromState(state);
            String originalState = UserSession.get().getOauthSecureState();
            // this checking is needed to prevent CSRF (Cross-Site Request Forgery)
            if (!originalState.equals(state)) {
                throw new IllegalStateException("received: '" + state + "', original: '" + originalState + "'");
            }

            String code = pp.get("code").toString();
            if (TextUtils.isEmpty(code)) {
                throw new IllegalArgumentException("'code' parameter was not provided!");
            }
            return fetchUserData(code);
        }

        @NotNull
        abstract RegistrationViaSocialNetworkPage.SocialRegData fetchUserData(@NotNull String code) throws IOException, JSONException;
    }

    private class GoogleHandler extends NetworkHandler {
        @NotNull
        RegistrationViaSocialNetworkPage.SocialRegData fetchUserData(@NotNull String code) throws IOException, JSONException {
            String url = "https://www.googleapis.com/oauth2/v3/token";
            String postData = "code=" + code +
                    "&client_id=" + OauthUtils.GOOGLE_CLIENT_ID +
                    "&client_secret=" + OauthUtils.GOOGLE_CLIENT_SECRET +
                    "&redirect_uri=" + OauthUtils.getCallbackUrl(SocialNetworkType.GOOGLE) +
                    "&grant_type=authorization_code";

            String response = null;//TODO: HttpFetcher.fetchPageRawWithPost(url, postData);
            JSONObject obj = new JSONObject(response);
            assertEmpty(obj.optString("error", ""));

            String idToken = obj.getString("id_token");

            String claims = OauthUtils.getClaims(idToken);
            JSONObject decodedToken = new JSONObject(claims);
            assertEmpty(decodedToken.optString("error", ""));

            String apiUrl = "https://www.googleapis.com/plus/v1/people/me?access_token=" + obj.getString("access_token");
            JSONObject personalInfo = HttpIO.getJSON(apiUrl, MAX_OAUTH_RESPONSE_SIZE);
            assertEmpty(personalInfo.optString("error", ""));
            log.debug(SocialNetworkType.GOOGLE + "->" + personalInfo);
            JSONObject name = personalInfo.getJSONObject("name");

            RegistrationViaSocialNetworkPage.SocialRegData socialData = new RegistrationViaSocialNetworkPage.SocialRegData();
            socialData.network = SocialNetworkType.GOOGLE;
            socialData.email = decodedToken.getString("email");
            socialData.socialId = personalInfo.getString("id");
            socialData.firstName = name.getString("givenName");
            socialData.lastName = name.getString("familyName");
            //TODO: socialData.avatarUrl = personalInfo.getJSONObject("image").getString("url");

            return socialData;
        }
    }

    private class VkHandler extends NetworkHandler {
        @NotNull
        RegistrationViaSocialNetworkPage.SocialRegData fetchUserData(@NotNull String code) throws IOException, JSONException {
            String url = "https://oauth.vk.com/access_token?" +
                    "client_id=" + OauthUtils.VK_CLIENT_ID +
                    "&client_secret=" + OauthUtils.VK_CLIENT_SECRET +
                    "&code=" + code +
                    "&redirect_uri=" + OauthUtils.getCallbackUrl(SocialNetworkType.VK);

            JSONObject res = HttpIO.getJSON(url, MAX_OAUTH_RESPONSE_SIZE);
            assertEmpty(res.optString("error", ""));

            String userId = "" + res.getLong("user_id");
            String apiUrl = "https://api.vk.com/method/users.get?uid=" + userId + "&fields=photo_200"; // this is an open method; it does not require an access_token.
            JSONObject personalInfo = HttpIO.getJSON(apiUrl, MAX_OAUTH_RESPONSE_SIZE).getJSONArray("response").getJSONObject(0);
            log.debug(SocialNetworkType.VK + "->" + personalInfo);

            assertEmpty(personalInfo.optString("error", ""));

            RegistrationViaSocialNetworkPage.SocialRegData socialData = new RegistrationViaSocialNetworkPage.SocialRegData();
            socialData.network = SocialNetworkType.VK;
            socialData.socialId = userId;
            socialData.email = res.getString("email");
            socialData.firstName = personalInfo.getString("first_name");
            socialData.lastName = personalInfo.getString("last_name");
            //TODO: socialData.avatarUrl = personalInfo.getString("photo_200");
            return socialData;
        }
    }

    private class YandexHandler extends NetworkHandler {
        @NotNull
        RegistrationViaSocialNetworkPage.SocialRegData fetchUserData(@NotNull String code) throws IOException, JSONException {
            String accessToken;
            { // fetch access_token
                String url = "https://oauth.yandex.ru/token";
                String postData = "grant_type=authorization_code" +
                        "&code=" + code +
                        "&client_id=" + OauthUtils.YANDEX_CLIENT_ID +
                        "&client_secret=" + OauthUtils.YANDEX_CLIENT_SECRET;

                String response = null;//TODO: HttpFetcher.fetchPageRawWithPost(url, postData);
                JSONObject obj = new JSONObject(response);
                assertEmpty(obj.optString("error", ""));
                accessToken = obj.getString("access_token");
            }

            { // fetch user data
                String url = "https://login.yandex.ru/info?format=json&oauth_token=" + accessToken;
                JSONObject personalInfo = HttpIO.getJSON(url, MAX_OAUTH_RESPONSE_SIZE);
                log.debug(SocialNetworkType.YANDEX + "->" + personalInfo);
                assertEmpty(personalInfo.optString("error", ""));

                RegistrationViaSocialNetworkPage.SocialRegData socialData = new RegistrationViaSocialNetworkPage.SocialRegData();
                socialData.network = SocialNetworkType.YANDEX;
                socialData.socialId = personalInfo.getString("id");
                socialData.email = personalInfo.getString("default_email");
                socialData.firstName = personalInfo.getString("first_name");
                socialData.lastName = personalInfo.getString("last_name");
                //TODO: socialData.avatarUrl = "http://avatars.mds.yandex.net/get-yapic/" + personalInfo.getString("default_avatar_id") + "/islands-200";
                return socialData;
            }
        }
    }

    private class MailHandler extends NetworkHandler {
        @NotNull
        RegistrationViaSocialNetworkPage.SocialRegData fetchUserData(@NotNull String code) throws IOException, JSONException {
            String accessToken;
            { //fetch access_token
                String url = "https://connect.mail.ru/oauth/token";
                String postData = "grant_type=authorization_code" +
                        "&code=" + code +
                        "&client_id=" + OauthUtils.MAIL_CLIENT_ID +
                        "&client_secret=" + OauthUtils.MAIL_CLIENT_SECRET +
                        "&redirect_uri=" + OauthUtils.getCallbackUrl(SocialNetworkType.MAIL_RU);

                String response = null;//TODO: HttpFetcher.fetchPageRawWithPost(url, postData);
                JSONObject obj = new JSONObject(response);
                assertEmpty(obj.optString("error", ""));
                accessToken = obj.getString("access_token");
            }

            { // fetch user data
                String appId = "app_id=" + OauthUtils.MAIL_CLIENT_ID;
                String sessionKey = "session_key=" + accessToken;
                String method = "method=users.getInfo";
                String secure = "secure=1";
                String params = appId + method + secure + sessionKey + OauthUtils.MAIL_CLIENT_SECRET; // params MUST be sorted in alphabetical order
                String sig = "sig=" + DigestUtils.md5DigestAsHex(params.getBytes()).toLowerCase(); // signature

                String url = "http://www.appsmail.ru/platform/api?" + method + "&" + appId + "&" + sessionKey + "&" + secure + "&" + sig;
                JSONObject personalInfo = HttpIO.getJSONArray(url, 30 * 1000).getJSONObject(0);
                log.debug(SocialNetworkType.MAIL_RU + "->" + personalInfo);
                assertEmpty(personalInfo.optString("error", ""));

                RegistrationViaSocialNetworkPage.SocialRegData socialData = new RegistrationViaSocialNetworkPage.SocialRegData();
                socialData.network = SocialNetworkType.MAIL_RU;
                socialData.socialId = personalInfo.getString("uid");
                socialData.email = personalInfo.getString("email");
                socialData.firstName = personalInfo.getString("first_name");
                socialData.lastName = personalInfo.getString("last_name");
                //TODO: socialData.avatarUrl = personalInfo.getString("pic_190");
                return socialData;
            }
        }
    }

    private class FacebookHandler extends NetworkHandler {
        @NotNull
        RegistrationViaSocialNetworkPage.SocialRegData fetchUserData(@NotNull String code) throws IOException, JSONException {
            String accessToken;
            { // fetch access_token
                String url = "https://graph.facebook.com/oauth/access_token?" +
                        "client_id=" + OauthUtils.FACEBOOK_CLIENT_ID +
                        "&client_secret=" + OauthUtils.FACEBOOK_CLIENT_SECRET +
                        "&redirect_uri=" + OauthUtils.getCallbackUrl(SocialNetworkType.FACEBOOK) +
                        "&code=" + code;

                String result = HttpIO.get(url);
                JSONObject obj = new JSONObject("{" + result.replace("&", ",").replace("=", ":") + "}"); // result is presented like URL params "key1=val1&key2=val2&..."
                assertEmpty(obj.optString("error", ""));
                accessToken = obj.getString("access_token");
            }

            { // fetch user data
                String url = "https://graph.facebook.com/me?access_token=" + accessToken;
                JSONObject personalInfo = HttpIO.getJSON(url, MAX_OAUTH_RESPONSE_SIZE);
                log.debug(SocialNetworkType.FACEBOOK + "->" + personalInfo);
                assertEmpty(personalInfo.optString("error", ""));

                RegistrationViaSocialNetworkPage.SocialRegData socialData = new RegistrationViaSocialNetworkPage.SocialRegData();
                socialData.network = SocialNetworkType.FACEBOOK;
                socialData.socialId = personalInfo.getString("id");
                socialData.email = personalInfo.getString("email");
                socialData.firstName = personalInfo.getString("first_name");
                socialData.lastName = personalInfo.getString("last_name");

                String imageDataUrl = "http://graph.facebook.com/" + socialData.socialId + "/picture?type=large&redirect=false";
                JSONObject imageData = HttpIO.getJSON(imageDataUrl, MAX_OAUTH_RESPONSE_SIZE);
                assertEmpty(imageData.optString("error", ""));
                //todo: socialData.avatarUrl = imageData.getJSONObject("data").getString("url");

                return socialData;
            }
        }
    }

    private class OdnoklassnikiHandler extends NetworkHandler {
        @NotNull
        RegistrationViaSocialNetworkPage.SocialRegData fetchUserData(@NotNull String code) throws IOException, JSONException {
            String accessToken;
            { //fetch access_token
                String url = "https://api.odnoklassniki.ru/oauth/token.do";
                String postData = "grant_type=authorization_code" +
                        "&code=" + code +
                        "&client_id=" + OauthUtils.ODNOKLASSNIKI_CLIENT_ID +
                        "&client_secret=" + OauthUtils.ODNOKLASSNIKI_CLIENT_SECRET +
                        "&redirect_uri=" + OauthUtils.getCallbackUrl(SocialNetworkType.ODNOKLASSNIKI);

                String response = null;//TODO: HttpFetcher.fetchPageRawWithPost(url, postData);
                JSONObject obj = new JSONObject(response);
                assertEmpty(obj.optString("error", ""));
                accessToken = obj.getString("access_token");
            }

            { // fetch user data
                String token = "access_token=" + accessToken;
                String appKey = "application_key=" + OauthUtils.ODNOKLASSNIKI_APP_KEY;
                String method = "method=users.getCurrentUser";
                String params = appKey + method + DigestUtils.md5DigestAsHex((accessToken + OauthUtils.ODNOKLASSNIKI_CLIENT_SECRET).getBytes()).toLowerCase(); // params MUST be sorted in alphabetical order
                String sig = "sig=" + DigestUtils.md5DigestAsHex(params.getBytes()).toLowerCase();

                String url = "http://api.ok.ru/fb.do?" + token + "&" + appKey + "&" + method + "&" + sig;
                JSONObject personalInfo = new JSONObject(HttpIO.get(url));
                log.debug(SocialNetworkType.ODNOKLASSNIKI + "->" + personalInfo);
                assertEmpty(personalInfo.optString("error", ""));

                RegistrationViaSocialNetworkPage.SocialRegData socialData = new RegistrationViaSocialNetworkPage.SocialRegData();
                socialData.network = SocialNetworkType.ODNOKLASSNIKI;
                socialData.socialId = personalInfo.getString("uid");
                socialData.firstName = personalInfo.getString("first_name");
                socialData.lastName = personalInfo.getString("last_name");
                socialData.email = "";
                //TODO: socialData.avatarUrl = personalInfo.getString("pic_2");
                return socialData;
            }
        }
    }

    private static void assertEmpty(@NotNull String value) {
        if (!value.isEmpty()) {
            throw new IllegalArgumentException("Value is not empty: " + value);
        }
    }

}
