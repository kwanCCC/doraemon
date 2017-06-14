package org.doraemon.treasure.gear;

import org.doraemon.treasure.gear.api.Gear;
import org.doraemon.treasure.gear.exception.GearRuntimeException;

import java.util.concurrent.atomic.AtomicBoolean;

public class GearClient implements Gear {


    private final GearConfig gearConfig;
    private final AtomicBoolean clientState = new AtomicBoolean(false);
    private final ZkServiceDiscoveryClient serviceDiscoveryClient;

    public GearClient(GearConfig gearConfig) {
        serviceDiscoveryClient = new ZkServiceDiscoveryClient(gearConfig);
        this.gearConfig = gearConfig;
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
}
