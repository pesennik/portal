package com.github.pesennik.component.tuner;

import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.BasePage;
import com.github.pesennik.component.HomePage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

@MountPath("/tuner")
public class TunerPage extends BasePage {

    public TunerPage() {
        setTitleAndDesc("Тюнер для гитары", "Тюнер для шестиструнной классической гитары.");
        setPageKeywords("тюнер", "гитара", "электрогитара", "онлайн тюнер", "шестиструнная гитара");

        add(new TunerPanel("tuner_panel"));
        add(new BookmarkablePageLink("home_link", HomePage.class));
    }

}
