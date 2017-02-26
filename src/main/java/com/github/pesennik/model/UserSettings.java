package com.github.pesennik.model;

import com.github.mjdbc.type.DbString;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * Set of user settings serializable to JSON
 */
public class UserSettings implements DbString {

    public UserSettings(@NotNull String json) {
//        JSONObject obj = new JSONObject(json);
//            res.language = Language.fromDbValue(obj.optString("l"));
    }

    @Override
    public String getDbValue() {
        JSONObject obj = new JSONObject();
//        JsonUtils.putOpt(obj, language != Language.DEFAULT, "l", language.getDbValue());
        return obj.toString();
    }
}
