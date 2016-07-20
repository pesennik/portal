package com.github.pesennik.component.parsley;

import com.github.pesennik.Constants;
import com.github.pesennik.Scripts;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.util.value.AttributeMap;

public class RequiredFieldJsValidator extends Behavior {

    protected final AttributeMap attributeMap = new AttributeMap();

    public RequiredFieldJsValidator() {
        attributeMap.put("data-parsley-trigger", "change");
        setRequired(true);
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);
        if (tag.getType() == XmlTag.TagType.CLOSE) {
            return;
        }
        tag.getAttributes().putAll(attributeMap);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(Scripts.PARSLEY_JS);
    }

    public RequiredFieldJsValidator setRequired(boolean val) {
        if (val) {
            attributeMap.put("data-parsley-required", "");
        } else {
            attributeMap.remove("data-parsley-required");
        }
        return this;
    }
}
