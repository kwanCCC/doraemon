package org.doraemon.treasure.gear.api;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;

public interface ServiceDiscoveryClient {

    void start();

    void stop();

    ServiceInstance serviceInstance(String appName);

    Collection<ServiceInstance> serviceInstances(String appName);
}
