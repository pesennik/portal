package com.github.pesennik.component.bootstrap;

import com.github.pesennik.component.util.ComponentFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BootstrapModal extends Panel {

    public enum BodyMode {
        Lazy,
        Static
    }

    public enum FooterMode {
        Show,
        Hide
    }

    public final WebMarkupContainer modal = new WebMarkupContainer("modal");
    public final WebMarkupContainer body = new WebMarkupContainer("body");
    public final WebMarkupContainer dialog = new WebMarkupContainer("dialog");
    public final WebMarkupContainer header = new WebMarkupContainer("header");
    public final WebMarkupContainer footer = new WebMarkupContainer("footer");

    @NotNull
    private final ComponentFactory bodyFactory;

    @NotNull
    public final BodyMode bodyMode;

    public BootstrapModal(@NotNull String id, @Nullable String title, @NotNull ComponentFactory bodyFactory, @NotNull BodyMode bodyMode, @NotNull FooterMode footerMode) {
        super(id);
        this.bodyFactory = bodyFactory;
        this.bodyMode = bodyMode;

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
        body.add(bodyMode == BodyMode.Lazy ? new EmptyPanel("body_content") : bodyFactory.create("body_content"));
        dialog.add(body);

        footer.setVisible(footerMode == FooterMode.Show);
        dialog.add(footer);

        WebMarkupContainer closeButton = new WebMarkupContainer("close_button");
        closeButton.setVisible(!footer.isVisible());
        body.add(closeButton);
    }

    public void show(AjaxRequestTarget target) {
        if (bodyMode != BodyMode.Lazy) {
            return;
        }
        body.replace(bodyFactory.create("body_content"));
        target.add(body);
    }

    public void hide(AjaxRequestTarget target) {
        target.appendJavaScript("$('#" + modal.getMarkupId() + "').modal('hide');");
    }

    @SuppressWarnings("unused")
    public void setOnCloseJavascript(AjaxRequestTarget target, String js) {
        target.appendJavaScript("$('#" + modal.getMarkupId() + "').on('hidden.bs.modal', function(){" + js + "})");
    }

    public String getDataTargetId() {
        return modal.getMarkupId();
    }
}