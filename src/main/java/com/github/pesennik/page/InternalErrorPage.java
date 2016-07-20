package com.github.pesennik.page;

import com.github.pesennik.annotation.MountPath;
import org.apache.wicket.markup.html.pages.AbstractErrorPage;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.HttpServletResponse;

@MountPath("/internal-error")
public class InternalErrorPage extends AbstractErrorPage {

    @Override
    protected void setHeaders(final WebResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
