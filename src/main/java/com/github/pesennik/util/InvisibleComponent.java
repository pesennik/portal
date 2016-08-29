package com.github.pesennik.util;

import org.apache.wicket.markup.html.WebMarkupContainer;

public class InvisibleComponent extends WebMarkupContainer {
    public InvisibleComponent(String id) {
        super(id);
        setVisible(false);
    }
}
