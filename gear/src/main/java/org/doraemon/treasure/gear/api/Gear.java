package org.doraemon.treasure.gear.api;

import org.apache.curator.x.discovery.ServiceInstance;
import org.doraemon.treasure.gear.strategy.ServiceStrategy;
import org.doraemon.treasure.gear.beans.ServiceProfile;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public interface Gear {

    /**
     * startup gear client.<br>
     * <p>
     * <pre>
     * 1. will first try to connect to zk
     * 2. create gear service provider with stickyStrategy.
     * 3. try to get configuration and initial profile by issuing direct HTTP calls to gear server
     * 4. register current service
     * 5. watch current service node in case of profile update.
     * 6. watch configuration node changes
     * </pre>
     */
    void start();

    /**
     * shutdown gear client.<br>
     * Session to zookeeper will be closed in this method.
     */
    void stop();

    /**
     * Retrieve a service instance from service registry.
     *
     * @param serviceName   the name of the service
     * @param payloadFilter filter by payload
     * @param strategy      random or round_robin @see SelectStrategy
     *
     * @throws org.doraemon.treasure.gear.exception.GearRuntimeException if failed to discover such a service
     */
    Optional<ServiceInstance<ServiceProfile>> serviceInstance(
            String serviceName,
            ServiceStrategy strategy,
            Predicate<ServiceProfile> payloadFilter
    );

    /**
     * Retrieve all service instance from service registry.
     *
     * @param serviceName the name of the service
     *
     * @return a list of service instance
     *
     * @throws org.doraemon.treasure.gear.exception.GearRuntimeException if failed to discover such a service
     */
    Collection<ServiceInstance<ServiceProfile>> serviceInstances(String serviceName);

    /**
     * Retrieve the service instance of the calling application registered in service registry.
     *
     * @return the service instance of the current application
     */
    ServiceInstance<ServiceProfile> currentInstance();
}
