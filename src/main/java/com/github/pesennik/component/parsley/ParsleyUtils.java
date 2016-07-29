package com.github.pesennik.component.parsley;

import com.github.pesennik.util.TextUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParsleyUtils {

    private static final Logger log = LoggerFactory.getLogger(ParsleyUtils.class);

    public static final String SERVER_SIDE_PARSLEY_ERROR = "'server-side-parsley-error'";

    public static void addParsleyError(@NotNull AjaxRequestTarget target, @NotNull Component errorContainer, @NotNull String message) {
        String markupId = errorContainer.getMarkupId();
        if (TextUtils.isEmpty(markupId)) {
            log.error("Error container has no markup id: " + errorContainer);
            return;
        }
        addParsleyError(target, markupId, message);
    }

    public static void addParsleyError(@NotNull AjaxRequestTarget target, @NotNull String errorContainerId, @NotNull String message) {
        String safeMessage = message.replace("'", "\"");
        target.appendJavaScript("$('#" + errorContainerId + "').parsley().removeError(" + SERVER_SIDE_PARSLEY_ERROR + ");");
        target.appendJavaScript("$('#" + errorContainerId + "').parsley().addError(" + SERVER_SIDE_PARSLEY_ERROR + ", {message:'" + safeMessage + "'});");
    }
}
