package com.github.pesennik.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.jetbrains.annotations.NotNull;

public class LinkifyBehavior extends Behavior {

    public LinkifyBehavior(@NotNull Label label) {
        label.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript(getLinkifyJs(component)));
    }

    @NotNull
    public static String getLinkifyJs(@NotNull Component c) {
        return "$('#" + c.getMarkupId() + "').each(function() {this.innerHTML = $site.Utils.linkify(this.innerHTML);});";
    }
}
