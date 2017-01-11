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

public class SongTextView extends Panel {
    @NotNull
    private final UserSongId songId;
    ContainerWithId songView = new ContainerWithId("song_text");

    public SongTextView(@NotNull String id, @NotNull UserSongId songId) {
        super(id);
        this.songId = songId;
        setOutputMarkupId(true);

        add(songView);
    }

    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        UserSong song = Context.getUserSongsDbi().getSong(songId);
        if (song == null) {
            JSONObject options = new JSONObject();
            options.put("text", "не найдена");
            options.put("targetSelector", "#" + songView.getMarkupId());
            response.render(OnDomReadyHeaderItem.forScript("$site.SongView.renderSong(" + options + ");"));
            return;
        }

        JSONObject options = new JSONObject();
        options.put("text", song.text);
        options.put("targetSelector", "#" + songView.getMarkupId());
        options.put("chordsMode", song.extra.chordsViewMode.getJsValue());
        options.put("textViewMode", song.extra.textViewMode.getJsValue());

        response.render(OnDomReadyHeaderItem.forScript("$site.SongView.renderSong(" + options + ");"));
    }
}