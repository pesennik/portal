package org.zametki.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.image.Image;

/**
 *
 */
public class RefreshCaptchaLink extends AjaxLink {
    private final CaptchaField captchaField;
    private final Image captchaImage;

    public RefreshCaptchaLink(String id, CaptchaField captchaField, Image captchaImage) {
        super(id);
        this.captchaField = captchaField;
        this.captchaImage = captchaImage;
    }

    public void onClick(AjaxRequestTarget target) {
        captchaField.change();
        captchaField.invalidate();
        target.add(captchaImage);
        target.add(captchaField);
    }
}
