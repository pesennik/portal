package com.github.pesennik.event.dispatcher;

import org.apache.wicket.Component;
import org.apache.wicket.IEventDispatcher;
import org.apache.wicket.event.IEvent;

import java.lang.reflect.Method;

public class PayloadEventDispatcher implements IEventDispatcher {

    @Override
    public void dispatchEvent(Object sink, IEvent event, Component component) {
        // Check that the payload isn't null and that the sink, the receiver, has the annotation
        if (event.getPayload() != null) {
            // Go through all methods
            for (Method method : sink.getClass().getDeclaredMethods()) {
                // Check so the annotation is on the method
                if (method.isAnnotationPresent(OnPayload.class)) {
                    try {
                        Class[] parTypes = method.getParameterTypes();

                        // Check that the method accepts one parameter and that it is the same type as the event
                        //noinspection unchecked
                        if (parTypes.length == 1 && parTypes[0].isAssignableFrom(event.getPayload().getClass())) {
                            method.invoke(sink, event.getPayload());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Exception when delivering event object " + event.getPayload().getClass() + " to component " + sink.getClass() + " and method " + method.getName(), e);
                    }
                    // We only deliver an event once to a single component, if there are multiple methods configured for it we ignore all but one
                    return;
                }
            }
        }
    }
}