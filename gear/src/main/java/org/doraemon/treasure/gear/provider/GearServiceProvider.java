package org.doraemon.treasure.gear.provider;

import org.apache.curator.x.discovery.DownInstancePolicy;
import org.apache.curator.x.discovery.InstanceFilter;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.ServiceDiscoveryImpl;
import org.apache.curator.x.discovery.details.ServiceProviderImpl;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.filter;

public class GearServiceProvider<T> extends ServiceProviderImpl<T>
        implements ServiceProvider<T> {

    private final ProviderStrategy<T> providerStrategy;

    public GearServiceProvider(
            ServiceDiscoveryImpl<T> discovery,
            String serviceName,
            ProviderStrategy<T> providerStrategy,
            ThreadFactory threadFactory,
            List<InstanceFilter<T>> preFilter,
            DownInstancePolicy downInstancePolicy
    ) {
        super(discovery, serviceName, providerStrategy, threadFactory, preFilter, downInstancePolicy);
        this.providerStrategy = providerStrategy;
    }

    public ServiceInstance<T> getInstance(List<InstanceFilter<T>> afterFilter) throws Exception {

        Iterable<ServiceInstance<T>> filtered = filter(getAllInstances(), and(afterFilter));
        return providerStrategy.getInstance(() -> copyOf(filtered));
    }
}
