package com.github.pesennik;

import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forUrl;

public class Scripts {
    public static final JavaScriptUrlReferenceHeaderItem SITE_JS = forUrl("/js/site.min.js?" + System.currentTimeMillis(), "site.min.js");
    public static final JavaScriptUrlReferenceHeaderItem BOOTSTRAP_JS = forUrl("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js", "bootstrap.js");

    public static final JavaScriptUrlReferenceHeaderItem ES6_PROMISE_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/es6-promise/3.2.2/es6-promise.min.js", "es6-promise.js");
    public static final JavaScriptUrlReferenceHeaderItem PARSLEY_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/parsley.js/2.3.13/parsley.min.js", "parsley.js");
    public static final JavaScriptUrlReferenceHeaderItem AUTOSIZE_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/autosize.js/3.0.14/autosize.min.js", "autosize.js");

}
