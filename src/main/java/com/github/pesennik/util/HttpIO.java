package com.github.pesennik.util;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpIO {
    private static final Logger log = LoggerFactory.getLogger(HttpIO.class);
    public static final int DEFAULT_MAX_RESPONSE_SIZE = 10 * 1024 * 1024;

    @NotNull
    public static String get(String url) {
        return get(url, Collections.emptyMap(), DEFAULT_MAX_RESPONSE_SIZE);
    }

    @NotNull
    public static String get(String url, Map<String, String> params, int maxSize) {
        log.debug("Fetching raw page: " + url);
        try {
            URL u = new URL(url);
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            uc.setConnectTimeout(20000);
            uc.setReadTimeout(20000);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:33.0) Gecko/20100101 Firefox/33.0");
            for (String key : params.keySet()) {
                uc.addRequestProperty(key, params.get(key));
            }
            uc.connect();
            StringBuilder tmp = new StringBuilder();
            boolean gzip = "gzip".equals(uc.getHeaderField("Content-Encoding"));
            InputStream is = gzip ? new GZIPInputStream(uc.getInputStream()) : uc.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            char[] buf = new char[64 * 1024];
            int readLen;
            while ((readLen = in.read(buf)) > 0) {
                tmp.append(buf, 0, readLen);
                if (tmp.length() >= maxSize) {
                    break;
                }
            }
            return tmp.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static JSONObject getJSON(String url, int maxResponseSizeInBytes) {
        return new JSONObject(get(url, Collections.emptyMap(), maxResponseSizeInBytes));
    }

    @NotNull
    public static JSONArray getJSONArray(String url, int maxResponseSizeInBytes) {
        return new JSONArray(get(url, Collections.emptyMap(), maxResponseSizeInBytes));
    }

    public static String post(@NotNull String url, @NotNull String urlencodedData) {
        log.debug("executePostRequest: " + url);
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);

            DataOutputStream os = new DataOutputStream(con.getOutputStream());
            os.writeBytes(urlencodedData);
            os.flush();
            os.close();

            String line;
            StringBuilder tmp = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((line = in.readLine()) != null) {
                tmp.append(line);
                tmp.append("\n");
            }
            return tmp.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
