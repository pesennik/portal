package com.github.pesennik.model;

import com.github.mjdbc.type.DbInt;
import org.apache.wicket.util.io.IClusterable;

/**
 * Base class for all Ids. Serializable DB value.
 */
public class AbstractId implements DbInt, IClusterable {
    protected final int value;

    public AbstractId(int value) {
        this.value = value;
    }

    @Override
    public int getDbValue() {
        return value;
    }


    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractId that = (AbstractId) o;
        return value == that.value;
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + value + "]";
    }
}
