package org.doraemon.treasure.gear;

import org.apache.curator.x.discovery.ServiceInstance;
import org.doraemon.treasure.gear.api.Gear;
import org.doraemon.treasure.gear.api.ServiceDiscoveryClient;
import org.doraemon.treasure.gear.exception.GearRuntimeException;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GearClient Bootstrap
 */
public class GearClient implements Gear {

    private final GearConfig gearConfig;
    private final AtomicBoolean clientState = new AtomicBoolean(false);
    private final ServiceDiscoveryClient serviceDiscoveryClient;

    public GearClient(GearConfig gearConfig) {
        this(gearConfig, new ZkServiceDiscoveryClient(gearConfig));
    }

    public GearClient(GearConfig gearConfig, ServiceDiscoveryClient serviceDiscoveryClient) {
        this.gearConfig = gearConfig;
        this.serviceDiscoveryClient = serviceDiscoveryClient;
    }

    @Override
    public void start() {
        if (clientState.compareAndSet(false, true)) {
            serviceDiscoveryClient.start();
        } else {
            throw new GearRuntimeException("gear has been startUp already .");
        }
    }

    @Override
    public void stop() {
        if (clientState.compareAndSet(true, false)) {
            serviceDiscoveryClient.stop();
        } else {
            throw new GearRuntimeException("gear client state is down.");
        }
    }

    @Override
    public boolean isRunning() {
        return clientState.get();
    }

    @Override
    public ServiceInstance serviceInstance(String appName) {
        return serviceDiscoveryClient.serviceInstance(appName);
    }

    @Override
    public Collection<ServiceInstance> serviceInstances(String appName) {
        return serviceDiscoveryClient.serviceInstances(appName);
    }
}
