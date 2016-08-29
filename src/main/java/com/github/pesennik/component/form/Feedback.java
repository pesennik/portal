package com.github.pesennik.component.form;

import com.github.pesennik.behavior.ClassModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.jetbrains.annotations.Nullable;

public class Feedback extends Panel {

    private WebMarkupContainer feedbackBlock = new WebMarkupContainer("feedback_block");
    private IModel<String> messageModel;
    private String alertClass;

    private final Label label = new Label("message", new LoadableDetachableModel<String>() {
        protected String load() {
            return messageModel == null ? "" : messageModel.getObject();
        }
    });


    public Feedback(String id) {
        super(id);
        setOutputMarkupId(true);

        add(feedbackBlock);
        feedbackBlock.setVisible(false);
        feedbackBlock.add(new ClassModifier(new LoadableDetachableModel<String>() {
            protected String load() {
                return "alert " + (alertClass == null ? "" : alertClass) + " alert-dismissible fade in";
            }
        }));
        feedbackBlock.add(label);
    }

    public void clear() {
        out(null, "");
    }

    public Feedback error(IModel<String> message) {
        return out(message, "alert-danger");
    }

    public Feedback error(String message) {
        return error(Model.of(message));
    }

    public Feedback success(IModel<String> message) {
        return out(message, "alert-success");
    }

    public Feedback success(String message) {
        return out(Model.of(message), "alert-success");
    }

    public Feedback info(IModel<String> message) {
        return out(message, "alert-info");
    }

    public Feedback info(String message) {
        return info(Model.of(message));
    }


    public Feedback warn(IModel<String> message) {
        return out(message, "alert-warning");
    }

    public Feedback warn(String message) {
        return warn(Model.of(message));
    }


    public Feedback out(@Nullable IModel<String> messageModel, @Nullable String alertClass) {
        this.messageModel = messageModel;
        this.alertClass = alertClass;
        label.setEscapeModelStrings(getEscapeModelStrings());
        feedbackBlock.setVisible(messageModel != null);
        return this;
    }

    public boolean hasMessage() {
        return messageModel != null;
    }

    public void reset(AjaxRequestTarget target) {
        clear();
        target.add(this);
    }
}