package com.github.pesennik.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
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
    public final WebMarkupContainer footer = new WebMarkupContainer("footer");

    @NotNull
    private final ComponentFactory bodyFactory;

    public BootstrapModal(@NotNull String id, @Nullable String title, @NotNull ComponentFactory bodyFactory, boolean staticMode, boolean hideFooter) {
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
        body.add(staticMode ? bodyFactory.create("body_content") : new EmptyPanel("body_content"));
        dialog.add(body);

        footer.setVisible(!hideFooter);
        dialog.add(footer);

        WebMarkupContainer closeButton = new WebMarkupContainer("close_button");
        closeButton.setVisible(hideFooter);
        body.add(closeButton);
    }

    public void show(AjaxRequestTarget target) {
        body.removeAll();
        body.add(bodyFactory.create("body_content"));
        target.add(body);
    }

    @SuppressWarnings("unused")
    public void setOnCloseJavascript(AjaxRequestTarget target, String js) {
        target.appendJavaScript("$('#" + modal.getMarkupId() + "').on('hidden.bs.modal', function(){" + js + "})");
    }

    public String getDataTargetId() {
        return modal.getMarkupId();
    }
}