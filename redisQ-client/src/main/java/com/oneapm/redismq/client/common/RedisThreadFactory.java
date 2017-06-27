package com.oneapm.redismq.client.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class RedisThreadFactory implements ThreadFactory {

    private String     application = null;
    private AtomicLong threadMark  = new AtomicLong(0);

    /**
     * @param application Thread's application
     */
    public RedisThreadFactory(String application) {
        this.application = application;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(application + threadMark.getAndAdd(1));
        t.setDaemon(true);
        return null;
    }
}
