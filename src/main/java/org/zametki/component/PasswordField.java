package org.zametki.component;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.IModel;

/**
 * Password field without default wicket validation.
 */
public class PasswordField extends PasswordTextField {
    public PasswordField(String id) {
        super(id);
        setRequired(false);
    }

    public PasswordField(String id, IModel<String> model) {
        super(id, model);
        setRequired(false);
    }
}
