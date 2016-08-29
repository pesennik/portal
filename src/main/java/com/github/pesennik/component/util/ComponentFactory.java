package com.github.pesennik.component.util;

import org.apache.wicket.Component;
import org.apache.wicket.util.io.IClusterable;
import org.jetbrains.annotations.NotNull;

public interface ComponentFactory extends IClusterable {
    @NotNull
    Component create(@NotNull String markupId);
}
