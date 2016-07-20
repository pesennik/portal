package org.zametki.component.parsley;

import org.zametki.Constants;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.jetbrains.annotations.NotNull;

public class ValidatingJsAjaxSubmitLink extends AjaxSubmitLink {
    public ValidatingJsAjaxSubmitLink(String id, @NotNull Form form) {
        super(id);
        form.setOutputMarkupId(true);
        form.add(new AttributeModifier("data-parsley-validate", ""));
    }

    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);

        AjaxCallListener ajaxCallListener = new AjaxCallListener();
        ajaxCallListener.onPrecondition("return $('#" + getForm().getMarkupId() + "').parsley().validate();");
        attributes.getAjaxCallListeners().add(ajaxCallListener);
    }

    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(Constants.PARSLEY_JS);
    }

}
