package com.github.pesennik.event.dispatcher;

import com.github.pesennik.ZApplication;
import org.apache.wicket.Component;
import org.apache.wicket.IEventDispatcher;
import org.apache.wicket.event.IEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class PayloadEventDispatcher implements IEventDispatcher {

    private static final String APP_PACKAGE = ZApplication.class.getPackage().getName();

    @Override
    public void dispatchEvent(@NotNull Object sink, @NotNull IEvent event, @Nullable Component component) {
        Object payload = event.getPayload();
        if (payload == null || !sink.getClass().getName().startsWith(APP_PACKAGE)) {
            return;
        }
        for (Method method : sink.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnPayload.class)) {
                try {
                    Class[] parTypes = method.getParameterTypes();
                    //noinspection unchecked
                    if (parTypes.length == 1 && parTypes[0].isAssignableFrom(payload.getClass())) {
                        method.invoke(sink, payload);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Exception when delivering event object " + payload.getClass() + " to component " + sink.getClass() + " and method " + method.getName(), e);
                }
                // We only deliver an event once to a single component, if there are multiple methods configured for it we ignore all but one
                return;
            }
        }
    }
}