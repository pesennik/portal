package org.zametki.db.dbi;

import org.zametki.util.LazyValue;
import com.github.mjdbc.Db;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class AbstractDbi {
    @NotNull
    protected final Db db;

    public AbstractDbi(@NotNull Db db) {
        this.db = db;
    }

    /**
     * Helper methods used in Dbi implementations to assert about some state.
     * Placed here because not used anywhere else in the code.
     */
    public static void assertTrue(boolean v, @NotNull LazyValue<String> checkName) {
        if (!v) {
            throw new IllegalStateException(checkName.get());
        }
    }

}
