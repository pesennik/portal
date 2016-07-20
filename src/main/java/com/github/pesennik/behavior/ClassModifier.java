package com.github.pesennik.behavior;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.IModel;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public final class ClassModifier extends AttributeModifier {
    public ClassModifier(@NotNull String val) {
        super("class", val);
    }

    public ClassModifier(@NotNull IModel<String> val) {
        super("class", val);
    }
}

