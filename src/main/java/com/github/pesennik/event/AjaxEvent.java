package com.github.pesennik.event;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.jetbrains.annotations.NotNull;

public class AjaxEvent {
    @NotNull
    public final AjaxRequestTarget target;

    public AjaxEvent(@NotNull AjaxRequestTarget target) {
        this.target = target;
    }
}
