package com.github.pesennik.page;

import com.github.pesennik.annotation.MountPath;

@MountPath("/tuner")
public class TunerPage extends BasePage {

    public TunerPage() {
        setTitleAndDesc("Тюнер для гитары", "Тюнер для шестиструнной классической гитары.");
        setPageKeywords("тюнер", "гитара", "электрогитара", "онлайн тюнер", "шестиструнная гитара");
    }

}
