package com.github.pesennik.component.parsley;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.jetbrains.annotations.NotNull;

public class ParsleyUtils {

    public static final String SERVER_SIDE_PARSLEY_ERROR = "'server-side-parsley-error'";

    public static void addParsleyError(@NotNull AjaxRequestTarget target, @NotNull String errorContainerId, @NotNull String message) {
        String safeMessage = message.replace("'", "\"");
        target.appendJavaScript("$('#" + errorContainerId + "').parsley().removeError(" + SERVER_SIDE_PARSLEY_ERROR + ");");
        target.appendJavaScript("$('#" + errorContainerId + "').parsley().addError(" + SERVER_SIDE_PARSLEY_ERROR + ", {message:'" + safeMessage + "'});");
    }
}
