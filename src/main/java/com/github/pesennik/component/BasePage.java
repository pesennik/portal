package com.github.pesennik.component;

import com.github.pesennik.Mounts;
import com.github.pesennik.Scripts;
import com.github.pesennik.util.HttpUtils;
import com.github.pesennik.util.UserSessionUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base page for all pages on site
 */
public abstract class BasePage extends WebPage implements IRequestablePage {

    private static final Logger log = LoggerFactory.getLogger(BasePage.class);

    public static final String DEFAULT_KEYWORDS = "Песенник, персональный песенник";
    public static final String DEFAULT_DESCRIPTION = "Персональный песенник";

    protected Label title = new Label("title", "Песенник");

    private final WebMarkupContainer keysField;
    private final WebMarkupContainer descField;

    protected final WebMarkupContainer scrollTop = new WebMarkupContainer("top_link");

    protected WebMarkupContainer header = new WebMarkupContainer("header");
    protected WebMarkupContainer footer = new WebMarkupContainer("footer");

    public BasePage() {
        checkCorrectMount();
        UserSessionUtils.initializeSession();
        pageInitCallback();
        HttpUtils.saveLastViewedPage();


        add(header);
        add(footer);

        add(scrollTop);
        scrollTop.setVisible(false);


        keysField = new WebMarkupContainer("meta_keywords");
        setPageKeywords(DEFAULT_KEYWORDS);
        add(keysField);

        descField = new WebMarkupContainer("meta_description");
        setPageDescription(DEFAULT_DESCRIPTION);
        add(descField);

        add(title);
    }

    private void checkCorrectMount() {
        if (!Mounts.isMounted(getClass())) {
            log.error("Page is not mounted: " + getClass());
        }
    }

    protected void pageInitCallback() {
    }


    public void setTitle(String v) {
        title.setDefaultModelObject(v);
    }

    @SuppressWarnings("unused")
    public void setTitleAndDesc(String v) {
        setTitle(v);
        setPageDescription(v);
    }

    @SuppressWarnings("unused")
    public void setTitleAndDesc(String title, String desc) {
        setTitle(title);
        setPageDescription(desc);
    }

    @SuppressWarnings("unused")
    public void setPageKeywords(Object... keywords) {
        StringBuilder sb = new StringBuilder();
        for (Object s : keywords) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(s);
        }
        setPageKeywords(sb.toString());
    }

    public void setPageKeywords(String keywords) {
        keysField.add(new AttributeModifier("content", keywords));
    }

    public void setPageDescription(String desc) {
        descField.add(new AttributeModifier("content", desc));
    }

    protected void setHeaders(WebResponse response) {
        response.disableCaching();
    }

    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference())));
        response.render(new PriorityHeaderItem(Scripts.BOOTSTRAP_JS));
        response.render(new PriorityHeaderItem(Scripts.PARSLEY_JS));
        response.render(new PriorityHeaderItem(Scripts.AUTOLINKER_JS));
        response.render(new PriorityHeaderItem(Scripts.SITE_JS));
    }
}
