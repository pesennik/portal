package com.github.pesennik.component;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputArea extends TextArea<String> {
    public InputArea(@NotNull String id) {
        this(id, "");
    }

    public InputArea(@NotNull String id, @Nullable String val) {
        super(id, Model.of(val == null ? "" : val));
    }

    @NotNull
    public String getInputString() {
        return getDefaultModelObjectAsString();
    }
}
