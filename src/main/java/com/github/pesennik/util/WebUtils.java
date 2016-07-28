package com.github.pesennik.util;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.jetbrains.annotations.NotNull;

public class WebUtils {

    public static void focus(@NotNull AjaxRequestTarget target, @NotNull String id) {
        target.appendJavaScript("$('#" + id + "').focus();");
    }

    public static void addFocusOnEnter(@NotNull Component keypressComponent, @NotNull String focusedId) {
        keypressComponent.add(new AttributeAppender("onkeypress", "$site.Utils.focusOnEnter(event, '#" + focusedId + "');"));
    }

    public static void addClickOnEnter(@NotNull Component keypressComponent, @NotNull String clickedId) {
        keypressComponent.add(new AttributeAppender("onkeypress", "$site.Utils.clickOnEnter(event, '#" + clickedId + "');"));
    }

}
