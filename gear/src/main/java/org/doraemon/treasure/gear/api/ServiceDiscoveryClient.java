package org.doraemon.treasure.gear.api;

import org.apache.curator.x.discovery.ServiceInstance;
import org.doraemon.treasure.gear.strategy.ServiceStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface ServiceDiscoveryClient<T> {

    void start();

    void stop();

    ServiceInstance<T> serviceInstance(String serviceName, Predicate<T> payloadFilter, ServiceStrategy selectStrategy);

    Collection<ServiceInstance<T>> serviceInstances(String serviceName);

    ServiceInstance<T> registerService(
            T serviceProfile,
            String serviceName,
            int port
    );

    ServiceInstance<T> registerService(
            T serviceProfile,
            String serviceName,
            String address,
            int port
    );

    Map<String, Collection<ServiceInstance<T>>> serviceInstances();
}
