package com.github.pesennik.component.util;

import org.apache.wicket.Page;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnchoredBookmarkablePageLink extends BookmarkablePageLink<String> {

    @Nullable
    public String anchor;

    public AnchoredBookmarkablePageLink(@NotNull String id, @NotNull Class<? extends Page> pageClass, @Nullable String anchor) {
        this(id, pageClass, null, anchor);
    }

    public AnchoredBookmarkablePageLink(@NotNull String id, @NotNull Class<? extends Page> pageClass, @Nullable PageParameters params, @Nullable String anchor) {
        super(id, pageClass, params);
        this.anchor = anchor;
    }

    @SuppressWarnings("unused")
    public AnchoredBookmarkablePageLink setAnchor(@Nullable String a) {
        anchor = a;
        return this;
    }

    @Override
    protected CharSequence appendAnchor(ComponentTag tag, CharSequence url) {
        if (anchor == null) {
            return super.appendAnchor(tag, url);
        }
        return url + "#" + anchor;
    }
}
