package com.github.pesennik.component.bootstrap;

import com.github.pesennik.behavior.StyleAppender;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.jetbrains.annotations.NotNull;

public class BootstrapLazyModalLink extends AjaxLink<Void> {

    @NotNull
    private final BootstrapModal modal;

    public BootstrapLazyModalLink(@NotNull String id, @NotNull BootstrapModal modal) {
        super(id);
        this.modal = modal;

        add(new StyleAppender("cursor: pointer;"));
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        modal.show(target);
        target.appendJavaScript("$('#" + modal.getDataTargetId() + "').modal();");
    }
}
