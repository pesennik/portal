package com.github.pesennik.component;

import com.github.pesennik.Scripts;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputArea extends TextArea<String> {

    private boolean autofocus;

    public InputArea(@NotNull String id, @Nullable String val) {
        super(id, Model.of(val == null ? "" : val));
    }

    public void setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
    }

    @NotNull
    public String getInputString() {
        return getDefaultModelObjectAsString();
    }

    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(Scripts.AUTOSIZE_JS);
        response.render(OnDomReadyHeaderItem.forScript("var $a = $('#" + getMarkupId() + "');" + (autofocus ? "$a.focus();" : "") + "autosize($a);"));
    }
}
