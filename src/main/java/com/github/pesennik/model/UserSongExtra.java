package com.github.pesennik.model;

import com.github.mjdbc.type.DbString;
import com.github.pesennik.util.JsonUtils;
import com.github.pesennik.util.TextUtils;
import org.apache.wicket.ajax.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserSongExtra implements DbString {

    @NotNull
    public SongChordsViewMode chordsViewMode = SongChordsViewMode.Inlined;

    @NotNull
    public SongTextViewMode textViewMode = SongTextViewMode.Visible;

    @NotNull
    public String links = "";

    @NotNull
    public static UserSongExtra parse(@Nullable String val) {
        UserSongExtra res = new UserSongExtra();
        if (TextUtils.isEmpty(val)) {
            return res;
        }
        JSONObject json = new JSONObject(val);

        res.chordsViewMode = SongChordsViewMode.fromDbValue(json.optString("chords"), SongChordsViewMode.Inlined);
        res.textViewMode = SongTextViewMode.fromDbValue(json.optString("text"), SongTextViewMode.Visible);
        res.links = json.optString("links", "");
        return res;
    }

    @Override
    @NotNull
    public String getDbValue() {
        JSONObject json = new JSONObject();
        JsonUtils.putOpt(json, chordsViewMode != SongChordsViewMode.Inlined, "chords", chordsViewMode.getDbValue());
        JsonUtils.putOpt(json, textViewMode != SongTextViewMode.Visible, "text", textViewMode.getDbValue());
        JsonUtils.putOpt(json, !links.isEmpty(), "links", links);
        return json.toString();
    }
}
