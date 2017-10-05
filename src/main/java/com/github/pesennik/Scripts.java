package com.github.pesennik;

import org.apache.wicket.markup.head.HeaderItem;

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forUrl;

public class Scripts {

    public static final HeaderItem SITE_JS = forUrl("/js/site.js?" + System.currentTimeMillis(), "site.js");

    public static final HeaderItem BOOTSTRAP_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/js/bootstrap.min.js", "bootstrap.js");

    public static final HeaderItem PARSLEY_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/parsley.js/2.8.0/parsley.min.js", "parsley.js");

    public static final HeaderItem AUTOLINKER_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/autolinker/1.4.4/Autolinker.js", "autolinker.js");

    public static final HeaderItem AUTOSIZE_JS = forUrl("https://cdnjs.cloudflare.com/ajax/libs/autosize.js/3.0.20/autosize.min.js", "autosize.js");

}
