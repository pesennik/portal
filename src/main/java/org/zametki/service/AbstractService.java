package org.zametki.service;

public class AbstractService {
    public AbstractService() {
        try {
            //TODO: Context.getScheduler().schedule(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
