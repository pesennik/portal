package com.github.pesennik.component.song;

import com.github.pesennik.Context;
import com.github.pesennik.component.util.ContainerWithId;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SongTextView extends Panel {

    @NotNull
    private final UserSongId songId;

    @Nullable
    private String chordsViewId;

    @NotNull
    private final ContainerWithId songView = new ContainerWithId("song_text");

    public SongTextView(@NotNull String id, @NotNull UserSongId songId, @Nullable String chordsViewId) {
        super(id);
        this.songId = songId;
        this.chordsViewId = chordsViewId;
        setOutputMarkupId(true);

        add(songView);
    }

    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        UserSong song = Context.getUserSongsDbi().getSong(songId);
        if (song == null) {
            JSONObject options = new JSONObject();
            options.put("text", "не найдена");
            options.put("songViewSelector", "#" + songView.getMarkupId());
            response.render(OnDomReadyHeaderItem.forScript("$site.SongView.renderSong(" + options + ");"));
            return;
        }

        JSONObject options = new JSONObject();
        options.put("text", song.text);
        options.put("songViewSelector", "#" + songView.getMarkupId());
        if (chordsViewId != null) {
            options.put("chordsViewSelector", "#" + chordsViewId);
        }
        response.render(OnDomReadyHeaderItem.forScript("$site.SongView.renderSong(" + options + ");"));
    }
}
