package com.github.pesennik.component.song;

import com.github.pesennik.behavior.LinkifyBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;

public class SongLinksPanel extends Panel {

    public SongLinksPanel(@NotNull String id, @NotNull String links) {
        super(id);

        Label linksLabel = new Label("links", links.replace("\n", "<br>"));
        linksLabel.setEscapeModelStrings(false);
        linksLabel.add(new LinkifyBehavior(linksLabel));
        add(linksLabel);
    }
}
