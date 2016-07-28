package com.github.pesennik.behavior;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.Model;

public final class StyleAppender extends AttributeAppender {
    public StyleAppender(String val) {
        super("style", Model.of(val), " ");
    }
}
