package com.github.pesennik.component.tuner;

import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.component.BasePage;
import com.github.pesennik.component.tuner.TunerPanel;

@MountPath("/tuner")
public class TunerPage extends BasePage {

    public TunerPage() {
        setTitleAndDesc("Тюнер для гитары", "Тюнер для шестиструнной классической гитары.");
        setPageKeywords("тюнер", "гитара", "электрогитара", "онлайн тюнер", "шестиструнная гитара");

        add(new TunerPanel("tuner_panel"));
    }

}
