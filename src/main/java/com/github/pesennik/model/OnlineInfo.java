package com.github.pesennik.model;

import org.apache.wicket.util.io.IClusterable;
import org.jetbrains.annotations.NotNull;

/**
 * Online object info. Not stored in database, calculated in runtime.
 */
public class OnlineInfo implements IClusterable {
    public UserId userId;
    public long accessTimeMillis;

    public OnlineInfo(@NotNull UserId userId, long accessTime) {
        this.userId = userId;
        this.accessTimeMillis = accessTime;
    }

}
