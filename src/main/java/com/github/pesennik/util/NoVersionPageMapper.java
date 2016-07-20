package com.github.pesennik.util;

import org.apache.wicket.core.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;

public class NoVersionPageMapper extends MountedMapper {

    public NoVersionPageMapper(String mountPath, Class<? extends IRequestablePage> pageClass) {
        super(mountPath, pageClass, new PageParametersEncoder());
    }

    protected void encodePageComponentInfo(Url url, PageComponentInfo info) {
        // do nothing so that component info does not get rendered in url
    }

    public Url mapHandler(IRequestHandler requestHandler) {
        if (requestHandler instanceof ListenerInterfaceRequestHandler || requestHandler instanceof BookmarkableListenerInterfaceRequestHandler) {
            return null;
        } else {
            return super.mapHandler(requestHandler);
        }
    }
}