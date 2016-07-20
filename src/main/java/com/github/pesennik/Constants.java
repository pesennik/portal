package com.github.pesennik;

import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;

import java.util.Locale;

import static org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem.forUrl;

public final class Constants {
    public static final String BASE_URL = "http://localhost:8080";
    public static final String BRAND_NAME = "Песенник";

    public static final JavaScriptUrlReferenceHeaderItem BOOTSTRAP_JS = forUrl("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js", "bootstrap.js");
    public static final JavaScriptUrlReferenceHeaderItem PARSLEY_JS = forUrl("/js/parsley.min.js", "parsley.min.js");

    public static final Locale RUSSIAN_LOCALE = Locale.forLanguageTag("ru");
}
