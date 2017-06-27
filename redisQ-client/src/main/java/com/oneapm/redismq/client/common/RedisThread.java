package com.oneapm.redismq.client.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public RedisThread(final String name, Runnable runnable, boolean daemon) {
        super(runnable, name);
        setDaemon(daemon);
        setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught exception in " + name + ": ", e);
            }
        });
    }
}
