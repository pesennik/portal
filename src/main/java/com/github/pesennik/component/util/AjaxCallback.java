package com.github.pesennik.component.util;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.io.IClusterable;
import org.jetbrains.annotations.NotNull;

public interface AjaxCallback extends IClusterable {

    void callback(@NotNull AjaxRequestTarget target);

}
