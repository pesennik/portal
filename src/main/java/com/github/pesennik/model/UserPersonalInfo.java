package com.github.pesennik.model;

import com.github.mjdbc.type.DbString;
import com.github.pesennik.util.JsonUtils;
import com.github.pesennik.util.TextUtils;
import org.apache.wicket.ajax.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserPersonalInfo implements DbString {
    private static final String FIRST_NAME_KEY = "n";
    private static final String LAST_NAME_KEY = "s";
    private static final String PARENTAL_NAME_KEY = "d";
    private static final String SOCIAL_IDS_KEY = "si";

    @NotNull
    public String firstName = "";

    @NotNull
    public String lastName = "";

    @NotNull
    public String parentalName = "";

    @NotNull
    public final Map<SocialNetworkType, String> socialIds = new HashMap<>();

    public UserPersonalInfo(@Nullable String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return;
        }
        try {
            JSONObject json = new JSONObject(jsonString);
            firstName = json.optString(FIRST_NAME_KEY, "");
            lastName = json.optString(LAST_NAME_KEY, "");
            parentalName = json.optString(PARENTAL_NAME_KEY, "");
            JSONObject idsJson = json.optJSONObject(SOCIAL_IDS_KEY);
            if (idsJson != null) {
                for (Iterator it = idsJson.keys(); it.hasNext(); ) {
                    String key = (String) it.next();
                    String id = idsJson.optString(key, null);
                    if (!TextUtils.isEmpty(id)) {
                        SocialNetworkType sn = SocialNetworkType.fromKey(key);
                        if (sn != null) {
                            socialIds.put(sn, id);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public String getDbValue() {
        JSONObject obj = new JSONObject();
        try {
            JsonUtils.putOpt(obj, !TextUtils.isEmpty(firstName), FIRST_NAME_KEY, firstName);
            JsonUtils.putOpt(obj, !TextUtils.isEmpty(firstName), LAST_NAME_KEY, lastName);
            if (!socialIds.isEmpty()) {
                JSONObject socialIdsJson = new JSONObject();
                for (SocialNetworkType sn : socialIds.keySet()) {
                    String val = socialIds.get(sn);
                    socialIdsJson.put(sn.key, val);
                }
                obj.put(SOCIAL_IDS_KEY, socialIdsJson);
            }
        } catch (Exception ignored) {
        }
        return obj.toString();
    }
}
