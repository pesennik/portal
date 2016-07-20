package com.github.pesennik.component;

import com.github.pesennik.util.SimpleCaptchaImageResource;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.value.ValueMap;

import java.util.Random;

public class CaptchaField extends RequiredTextField<String> {
    private static final Random RND = new Random(System.currentTimeMillis());

    private String imageText = randomString(6, 8);
    private SimpleCaptchaImageResource captchaImageResource;
    public static final String CAPTCHA_PROPERTY = "captcha";
    private ValueMap properties = new ValueMap();

    public CaptchaField(String id) {
        super(id, String.class);
        setOutputMarkupId(true);
        setDefaultModel(new PropertyModel<>(properties, CAPTCHA_PROPERTY));
        captchaImageResource = new SimpleCaptchaImageResource(Model.of(getOriginalText()), 48, 30, RND) {
            protected void setResponseHeaders(AbstractResource.ResourceResponse data, IResource.Attributes attributes) {
                super.setResponseHeaders(data, attributes);
                data.disableCaching();
            }
        };
    }

    protected final void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);
        tag.put("value", ""); // clear the field after each render
    }

    public String getOriginalText() {
        return imageText;
    }

    public String getUserText() {
        return properties.getString(CAPTCHA_PROPERTY);
    }

    public SimpleCaptchaImageResource getCaptchaImageResource() {
        return captchaImageResource;
    }

    public void change() {
        imageText = randomString(6, 8).replaceAll("l", "x"); // 'l' can be confused with 'i'
        captchaImageResource.getChallengeIdModel().setObject(imageText);

    }

    public static String randomString(int min, int max) {
        int num = randomInt(min, max);
        byte b[] = new byte[num];
        for (int i = 0; i < num; i++) {
            b[i] = (byte) randomInt('a', 'z');
        }
        return new String(b);
    }

    public static int randomInt(int min, int max) {
        return (int) (RND.nextDouble() * (max - min) + min);
    }

    public void invalidate() {
        captchaImageResource.invalidate();
    }
}
