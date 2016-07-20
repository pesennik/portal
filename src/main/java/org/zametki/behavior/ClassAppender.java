package org.zametki.behavior;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.Model;

/**
 *
 */
public final class ClassAppender extends AttributeAppender {
    public ClassAppender(String val) {
        super("class", Model.of(val), " ");
    }
}
