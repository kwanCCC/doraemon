package org.doraemon.treasure.gear.spring;

import org.apache.curator.x.discovery.ServiceInstance;
import org.doraemon.treasure.gear.GearConfig;
import org.doraemon.treasure.gear.GearServiceImpl;
import org.doraemon.treasure.gear.api.Gear;
import org.doraemon.treasure.gear.beans.ServiceProfile;
import org.doraemon.treasure.gear.strategy.ServiceStrategy;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class SpringGearClient implements Gear {
    private final Gear client;

    public SpringGearClient(ConfigurableApplicationContext context) {
        this(new SpringContextPropertiesInjector(context));
    }

    public SpringGearClient() {
        this(GearConfig.load());
    }

    public SpringGearClient(GearConfig gearClientConfig) {
        this(new GearServiceImpl(gearClientConfig));
    }

    SpringGearClient(Gear client) {
        this.client = client;
    }

    SpringGearClient(SpringContextPropertiesInjector propertiesInjector) {
        this();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Optional<ServiceInstance<ServiceProfile>> serviceInstance(
            String serviceName, ServiceStrategy strategy, Predicate<ServiceProfile> payloadFilter
    ) {
        return null;
    }

    @Override
    public Collection<ServiceInstance<ServiceProfile>> serviceInstances(
            String serviceName
    ) {
        return null;
    }

    @Override
    public ServiceInstance<ServiceProfile> currentInstance() {
        return null;
    }
}

