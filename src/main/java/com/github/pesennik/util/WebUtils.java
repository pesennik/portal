package com.github.pesennik.util;

import com.github.pesennik.Context;
import com.github.pesennik.ZApplication;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.BookmarkablePageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {

    private static final Logger log = LoggerFactory.getLogger(WebUtils.class);

    public static void focus(@NotNull AjaxRequestTarget target, @NotNull Component component) {
        String markupId = component.getMarkupId();
        if (TextUtils.isEmpty(markupId)) {
            log.error("Component has no markup id: " + component);
            return;
        }
        focus(target, markupId);
    }

    public static void focus(@NotNull AjaxRequestTarget target, @NotNull String id) {
        target.appendJavaScript("$('#" + id + "').focus();");
    }

    public static void addFocusOnEnter(@NotNull Component keypressComponent, @NotNull Component component) {
        String markupId = component.getMarkupId();
        if (TextUtils.isEmpty(markupId)) {
            log.error("Component has no markup id: " + component);
            return;
        }
        addFocusOnEnter(keypressComponent, markupId);
    }

    public static void addFocusOnEnter(@NotNull Component keypressComponent, @NotNull String focusedId) {
        keypressComponent.add(new AttributeAppender("onkeypress", "$site.Utils.focusOnEnter(event, '#" + focusedId + "');"));
    }

    public static void addClickOnEnter(@NotNull Component keypressComponent, @NotNull Component component) {
        String markupId = component.getMarkupId();
        if (TextUtils.isEmpty(markupId)) {
            log.error("Component has no markup id: " + component);
            return;
        }
        addClickOnEnter(keypressComponent, markupId);

    }

    public static void addClickOnEnter(@NotNull Component keypressComponent, @NotNull String clickedId) {
        keypressComponent.add(new AttributeAppender("onkeypress", "$site.Utils.clickOnEnter(event, '#" + clickedId + "');"));
    }

    @NotNull
    public static byte[] getWebInfResource(@NotNull String path) throws Exception {
        return IOUtils.toByteArray(WebApplication.get().getServletContext().getResource(path));
    }

    public static void scrollTo(@NotNull Component c, @NotNull AjaxRequestTarget target) {
        target.appendJavaScript("$site.Utils.scrollToBlock('#" + c.getMarkupId() + "');");
    }

    @NotNull
    public static String getFullPageUrl(@NotNull Class<? extends Page> cls, @Nullable PageParameters pp) {
        return Context.getBaseUrl() + getPageMount(cls, pp);
    }

    @NotNull
    public static String getPageMount(@NotNull Class<? extends Page> cls, @Nullable PageParameters pp) {
        IRequestHandler handler = new BookmarkablePageRequestHandler(new PageProvider(cls, pp));
        String url = ZApplication.get().getRootRequestMapper().mapHandler(handler).toString();
        url = url.startsWith(".") ? url.substring(1) : url;
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (!url.isEmpty() && !url.startsWith("/")) {
            url = "/" + url;
        }
        return url;
    }
}
