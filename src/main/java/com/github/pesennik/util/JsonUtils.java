package com.github.pesennik.util;

import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtils {
    public static void putOpt(JSONObject obj, boolean condition, String property, int value) throws JSONException {
        if (condition) {
            obj.putOnce(property, value);
        }
    }

    public static void putOpt(JSONObject obj, boolean condition, String property, double value) throws JSONException {
        if (condition) {
            obj.putOnce(property, value);
        }
    }

    public static void putOpt(JSONObject obj, boolean condition, String property, String value) throws JSONException {
        if (condition) {
            obj.putOnce(property, value);
        }
    }

    public static void putOpt(JSONObject obj, boolean condition, String property, JSONArray value) throws JSONException {
        if (condition) {
            obj.putOnce(property, value);
        }
    }

    public static void putOpt(JSONObject obj, boolean condition, String property, JSONObject value) throws JSONException {
        if (condition) {
            obj.putOnce(property, value);
        }
    }

    public static JSONArray concatArrays(JSONArray... arrs) throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }

    @NotNull
    public static Map<String, String> unpackStringMap(@Nullable String json) {
        Map<String, String> res = new HashMap<>();
        if (json != null) {
            JSONObject jsonObj = new JSONObject(json);
            for (Iterator it = jsonObj.keys(); it.hasNext(); it.next()) {
                String key = (String) it.next();
                String value = jsonObj.optString(key, null);
                res.put(key, value);
            }
        }
        return res;
    }

    public static String packStringMap(@NotNull Map<String, String> map) {
        JSONObject res = new JSONObject();
        for (Map.Entry<String, String> e : map.entrySet()) {
            res.put(e.getKey(), e.getValue());
        }
        return res.toString();
    }
}
