package com.github.pesennik.component.form;

import com.github.pesennik.util.TextUtils;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputField extends TextField<String> {
    public InputField(@NotNull String id) {
        this(id, "");
    }

    public InputField(@NotNull String id, @Nullable String val) {
        super(id, Model.of(val == null ? "" : val));
        setOutputMarkupId(true);
    }

    @NotNull
    public String getInputString() {
        return TextUtils.notNull(getModelObject(), "");
    }
}
