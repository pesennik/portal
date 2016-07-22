package com.github.pesennik.service;

import com.github.pesennik.UserSession;
import com.github.pesennik.model.UserId;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;

public class OnlineStatusManager extends AbstractService {

    private static final Logger log = LoggerFactory.getLogger(OnlineStatusManager.class);

    public void touch() {
        UserId userId = UserSession.get().getUserId();
        if (userId == null) {
            return;
        }
        HttpServletRequest request = ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest());
        HttpSession httpSession = request.getSession();
        touch(httpSession);
    }

    private static void touch(HttpSession session) {
        try {
            Field f = session.getClass().getDeclaredField("session");
            f.setAccessible(true);
            HttpSession realSession = (HttpSession) f.get(session);
            realSession.getClass().getMethod("access").invoke(realSession);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
