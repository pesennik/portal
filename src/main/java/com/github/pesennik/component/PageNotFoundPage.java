package com.github.pesennik.component;

import com.github.pesennik.annotation.MountPath;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.WebResponse;

@MountPath("/404")
public class PageNotFoundPage extends WebPage {
    @Override
    protected void configureResponse(WebResponse response) {
        super.configureResponse(response);
        response.setStatus(404);
    }
}
