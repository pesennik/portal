package com.github.pesennik.component;

import org.apache.wicket.Component;
import org.apache.wicket.util.io.IClusterable;

public interface ComponentFactory extends IClusterable {
    Component create(String markupId);
}
