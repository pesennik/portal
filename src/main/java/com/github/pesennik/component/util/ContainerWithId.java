package com.github.pesennik.component.util;

import org.apache.wicket.markup.html.WebMarkupContainer;

public class ContainerWithId extends WebMarkupContainer {
    public ContainerWithId(String id) {
        super(id);
        setOutputMarkupId(true);
    }
}
