package com.github.pesennik.db.dbi;

import com.github.pesennik.model.LentaEntry;
import org.jetbrains.annotations.NotNull;

public interface SharingDbi {

    void create(@NotNull LentaEntry entry);

}
