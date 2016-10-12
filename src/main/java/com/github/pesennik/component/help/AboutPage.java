package com.github.pesennik.component.help;

import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.BasePage;

@MountPath("/about")
public class AboutPage extends BasePage {
    public static final String FORMAT_ANCHOR = "format";

    public AboutPage() {
        setTitleAndDesc("О сайте", "О сайте pesennik.online и используемых форматах");
    }

}
