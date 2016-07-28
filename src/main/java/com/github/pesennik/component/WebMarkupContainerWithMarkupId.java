package com.github.pesennik.component;

import org.apache.wicket.markup.html.WebMarkupContainer;

public class WebMarkupContainerWithMarkupId extends WebMarkupContainer {
    public WebMarkupContainerWithMarkupId(String id) {
        super(id);
        setOutputMarkupId(true);
    }
}
