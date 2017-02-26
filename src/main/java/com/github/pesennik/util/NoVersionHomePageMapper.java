package com.github.pesennik.util;

import org.apache.wicket.Application;
import org.apache.wicket.core.request.handler.BookmarkableListenerRequestHandler;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.handler.ListenerRequestHandler;
import org.apache.wicket.core.request.mapper.AbstractComponentMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.jetbrains.annotations.Nullable;

public class NoVersionHomePageMapper extends AbstractComponentMapper {
    public int getCompatibilityScore(Request request) {
        return isHomeUrl(request) ? 1 : 0;
    }

    @Nullable
    public Url mapHandler(IRequestHandler requestHandler) {
        if (requestHandler instanceof ListenerRequestHandler || requestHandler instanceof BookmarkableListenerRequestHandler) {
            return null;
        }

        if (requestHandler instanceof IPageRequestHandler) {
            IPageRequestHandler pageRequestHandler = (IPageRequestHandler) requestHandler;

            if (pageRequestHandler.getPageClass().equals(Application.get().getHomePage())) {
                return new Url();
            }
        }

        return null;
    }

    @Override
    public IRequestHandler mapRequest(Request request) {
        return null;
    }

    /**
     * A home URL is considered a URL without any segments
     *
     * @return <code>true</code> if the request is to the home page ("/")
     */
    private boolean isHomeUrl(Request request) {
        return request.getUrl().getSegments().isEmpty();
    }
}