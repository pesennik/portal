package com.github.pesennik.util;

import org.apache.wicket.markup.repeater.data.IDataProvider;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractListProvider<T> implements IDataProvider<T> {
    protected List<T> list;


    public abstract List<T> getList();

    public Iterator<? extends T> iterator(long first, long count) {
        return getListInternal().subList((int) first, (int) (first + count)).iterator();
    }

    public long size() {
        return getListInternal().size();
    }

    public void detach() {
        list = null;
    }

    public List<T> getListInternal() {
        if (list == null) {
            list = getList();
        }
        return list;
    }
}
