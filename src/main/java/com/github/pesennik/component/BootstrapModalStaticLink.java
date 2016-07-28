package com.github.pesennik.component;

import com.github.pesennik.behavior.StyleAppender;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.jetbrains.annotations.NotNull;

public class BootstrapModalStaticLink extends WebMarkupContainer {

    public BootstrapModalStaticLink(@NotNull String id, @NotNull BootstrapModal modal) {
        super(id);
        add(new StyleAppender("cursor: pointer;"));
        add(new AttributeModifier("onclick", "$('#" + modal.getDataTargetId() + "').modal();"));
    }
}
