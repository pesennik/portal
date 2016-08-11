package com.github.pesennik.component;

import com.github.pesennik.Context;
import com.github.pesennik.model.UserSong;
import com.github.pesennik.model.UserSongId;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;

public class UserSongTextView extends Panel {
    @NotNull
    private final UserSongId songId;
    ContainerWithId songView = new ContainerWithId("song_text");

    public UserSongTextView(@NotNull String id, @NotNull UserSongId songId) {
        super(id);
        this.songId = songId;
        add(songView);
    }

    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        UserSong song = Context.getUserSongsDbi().getSong(songId);

        JSONObject options = new JSONObject();
        options.put("text", song == null ? "не найдена" : song.text);
        options.put("targetSelector", "#" + songView.getMarkupId());
        options.put("chordsMode", "Inlined");

        response.render(OnDomReadyHeaderItem.forScript("$site.SongView.renderSong(" + options + ");"));
    }
}
