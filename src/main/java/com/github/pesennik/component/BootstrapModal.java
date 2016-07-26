package com.github.pesennik.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BootstrapModal extends Panel {

    public final WebMarkupContainer modal = new WebMarkupContainer("modal");
    public final WebMarkupContainer body = new WebMarkupContainer("body");
    public final WebMarkupContainer dialog = new WebMarkupContainer("dialog");
    public final WebMarkupContainer header = new WebMarkupContainer("header");

    @NotNull
    private final ComponentFactory bodyFactory;

    public BootstrapModal(@NotNull String id, @Nullable String title, @NotNull ComponentFactory bodyFactory) {
        super(id);
        this.bodyFactory = bodyFactory;

        modal.setOutputMarkupId(true);
        add(modal);

        modal.add(dialog);

        dialog.add(header);
        if (title != null) {
            header.add(new Label("title", title));
        } else {
            header.setVisible(false);
        }

        body.setOutputMarkupId(true);
        body.add(new EmptyPanel("body_content"));
        dialog.add(body);


    }

    public void show(AjaxRequestTarget target) {
        body.removeAll();
        body.add(bodyFactory.create("body_content"));
        target.add(body);
    }

//    public void setOnCloseJavascript(AjaxRequestTarget target, String js) {
//        target.appendJavaScript("$('#" + modal.getMarkupId() + "').on('hidden.bs.modal', function(){" + js + "})");
//    }

    public String getDataTargetId() {
        return modal.getMarkupId();
    }


}
