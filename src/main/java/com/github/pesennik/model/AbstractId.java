package com.github.pesennik.model;

import com.github.mjdbc.type.impl.DbIntValue;
import org.apache.wicket.util.io.IClusterable;

/**
 * Base class for all Ids. Serializable DB value.
 */
public class AbstractId extends DbIntValue implements IClusterable {
    public AbstractId(int value) {
        super(value);
    }
}
