package org.doraemon.treasure.gear.api;

import org.apache.curator.x.discovery.ServiceInstance;
import org.doraemon.treasure.gear.ServiceStrategy;
import org.doraemon.treasure.gear.exception.GearCannotGetServiceException;

import java.util.Collection;
import java.util.function.Predicate;

public interface ServiceDiscoveryClient<T> {

    void start();

    void stop();

    ServiceInstance<T> serviceInstance(String appName, ServiceStrategy strategy, Predicate<T> filter)
            throws Exception;

    Collection<ServiceInstance<T>> serviceInstances(String appName, ServiceStrategy strategy, Predicate<T> filter)
            throws Exception;

    void registerService(ServiceInstance<T> serviceInstance) throws Exception;

    void unregisterService(ServiceInstance<T> serviceInstance) throws Exception;
}
