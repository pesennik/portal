package com.github.pesennik;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forUrl;

public class Scripts {

    public static final HeaderItem SITE_JS = forUrl("/js/site.min.js?" + System.currentTimeMillis(), "site.min.js");

    public static final HeaderItem BOOTSTRAP_JS = forUrl("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js", "bootstrap.js");

    public static final HeaderItem PARSLEY_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/parsley.js/2.5.1/parsley.min.js", "parsley.js");

    public static final JavaScriptUrlReferenceHeaderItem AUTOLINKER_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/autolinker/1.4.0/Autolinker.js", "autolinker.js");

    public static final JavaScriptUrlReferenceHeaderItem AUTOSIZE_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/autosize.js/3.0.17/autosize.min.js", "autosize.js");

}
