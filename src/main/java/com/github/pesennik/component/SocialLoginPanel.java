package com.github.pesennik.component;

import com.github.pesennik.model.SocialNetworkType;
import com.github.pesennik.behavior.ClassModifier;
import com.github.pesennik.util.OauthLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 */
public class SocialLoginPanel extends Panel {
    public SocialLoginPanel(String id) {
        super(id);

        ListView<SocialNetworkType> socials = new ListView<SocialNetworkType>("socials", SocialNetworkType.ACTIVE_NETWORKS) {
            protected void populateItem(ListItem<SocialNetworkType> item) {
                SocialNetworkType sn = item.getModelObject();
                OauthLink link = new OauthLink("link", sn);
                link.add(new Label("name", sn.displayName));
                link.add(new WebMarkupContainer("icon").add(new ClassModifier(sn.iconClass)));
                item.add(link);
            }
        };
        add(socials);
    }
}
