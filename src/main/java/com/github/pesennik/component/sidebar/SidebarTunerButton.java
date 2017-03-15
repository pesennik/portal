package com.github.pesennik.component.sidebar;

import com.github.pesennik.component.bootstrap.BootstrapLazyModalLink;
import com.github.pesennik.component.bootstrap.BootstrapModal;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;

public class SidebarTunerButton extends Panel {

    public SidebarTunerButton(@NotNull String id, @NotNull BootstrapModal lazyModal) {
        super(id);
        add(new BootstrapLazyModalLink("tuner_link", lazyModal));
    }
}
