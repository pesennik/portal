package org.zametki.model;

/**
 * Database entity with ID. Not serializable and can't not be stored in Wicket sessions.
 */
public class Identifiable<T extends AbstractId> {
    public T id;

    public Identifiable() {
    }

    public String toString() {
        return getClass().getSimpleName() + "[id:" + id + "]";
    }
}
