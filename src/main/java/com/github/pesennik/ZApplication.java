package com.github.pesennik;

import com.github.pesennik.util.NoVersionPageMapper;
import com.github.pesennik.annotation.MountPath;
import com.github.pesennik.page.HomePage;
import com.github.pesennik.util.NoVersionHomePageMapper;
import com.github.pesennik.util.TextUtils;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.pages.InternalErrorPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.settings.ApplicationSettings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 */
public class ZApplication extends WebApplication {
    private static final Logger log = LoggerFactory.getLogger(ZApplication.class);
    private static ZApplication instance;
    public final Set<Class> mountedPageClasses = new HashSet<>();

    protected void init() {
        instance = this;

        log.error("----------======== Startup ========----------");

        Context.init();

        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setCompressWhitespace(true);

        getRootRequestMapperAsCompound().add(new NoVersionHomePageMapper());
        ApplicationSettings appSettings = getApplicationSettings();
        appSettings.setPageExpiredErrorPage(HomePage.class);
        appSettings.setInternalErrorPage(InternalErrorPage.class);

        setAjaxRequestTargetProvider(page -> {
            Context.getOnlineStatusManager().touch();
            return new AjaxRequestHandler(page);
        });

        Mounts.mountAll(this);
        log.info("Web application is initialized");
    }


    public <T extends Page> MountedMapper mountPage(String path, final Class<T> pageClass) {
        NoVersionPageMapper mapper = new NoVersionPageMapper(path, pageClass);
        mount(mapper);
        mountedPageClasses.add(pageClass);
        return mapper;
    }

    public void mountPage(Class<? extends Page> cls) {
        MountPath a = cls.getAnnotation(MountPath.class);
        Objects.requireNonNull(a);
        mount(cls, a.alt()); // first map alt paths. The value on the next line will override them.
        mount(cls, a.value());
    }

    private void mount(Class cls, String... mountPaths) {
        for (String mountPath : mountPaths) {
            if (TextUtils.isEmpty(mountPath)) {
                throw new IllegalArgumentException("Illegal mount for page " + cls + " mount: " + mountPath);
            }
            mountPage(mountPath, cls);
        }
    }

    public Session newSession(Request request, Response response) {
        HttpServletRequest r = (HttpServletRequest) request.getContainerRequest();
        String ip = r.getHeader("REMOTE_ADDR");
        if (TextUtils.isEmpty(ip)) {
            ip = r.getHeader("X-Forwarded-For");
            if (TextUtils.isEmpty(ip)) {
                ip = r.getRemoteAddr();
            }
        }
        UserSession session = new UserSession(request);
        session.setIp(ip);
        return session;
    }

    protected void onDestroy() {
        log.debug("onDestroy!");
        Context.shutdown();
    }

    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @NotNull
    public static ZApplication get() {
        if (instance != null && ThreadContext.getApplication() != instance) {
            ThreadContext.setApplication(instance);
        }
        return (ZApplication) WebApplication.get();
    }

    public boolean isMounted(Class<? extends IRequestablePage> pageClass) {
        return mountedPageClasses.contains(pageClass) || pageClass == getHomePage();
    }
}
