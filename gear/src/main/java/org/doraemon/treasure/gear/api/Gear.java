package org.doraemon.treasure.gear.api;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;

public interface Gear {

    void start();

    void stop();

    boolean isRunning();

    ServiceInstance serviceInstance(String appName);

    Collection<ServiceInstance> serviceInstances(String appName);
}
