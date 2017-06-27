package org.doraemon.treasure.gear;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.x.discovery.DownInstancePolicy;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceDiscoveryImpl;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.doraemon.treasure.gear.api.ServiceDiscoveryClient;
import org.doraemon.treasure.gear.exception.GearRuntimeException;
import org.doraemon.treasure.gear.provider.GearServiceProvider;
import org.doraemon.treasure.gear.strategy.ServiceStrategy;
import org.doraemon.treasure.gear.util.LocalIpAddressResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.doraemon.treasure.gear.strategy.SelectStrategyFactory.getStrategy;
import static org.doraemon.treasure.gear.strategy.ServiceStrategy.*;

@Slf4j
public class ZookeeperServiceDiscoveryClient<T> implements ServiceDiscoveryClient<T> {

    private final CuratorFramework curatorClient;
    private final InstanceSerializer<T> serializer;
    private final String basePath;
    private final Class<T> payloadClass;

    private ServiceDiscovery<T> serviceDiscovery;

    private final Cache<String, GearServiceProvider<T>> cache = cacheBuild();

    private Cache<String, GearServiceProvider<T>> cacheBuild() {
        return Caffeine.newBuilder()
                       .initialCapacity(10)
                       .maximumSize(3000)
                       .recordStats()
                       .expireAfterAccess(
                               5,
                               TimeUnit.MINUTES
                       )
                       .removalListener((RemovalListener<String, GearServiceProvider<T>>) (key, value, cause) -> {
                           CloseableUtils.closeQuietly(value);
                       })
                       .build();
    }

    public ZookeeperServiceDiscoveryClient(
            CuratorFramework curatorClient,
            InstanceSerializer<T> serializer,
            String basePath,
            Class<T> payloadClass
    ) {
        this.curatorClient = curatorClient;
        this.serializer = serializer;
        this.basePath = basePath;
        this.payloadClass = payloadClass;
    }


    @Override
    public void start() {
        serviceDiscovery = ServiceDiscoveryBuilder
                .builder(payloadClass)
                .serializer(serializer)
                .basePath(basePath)
                .client(curatorClient)
                .build();

        try {
            curatorClient.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new GearRuntimeException("cannot create service discovery", e);
        }
    }

    @Override
    public void stop() {
        cache.cleanUp();
        CloseableUtils.closeQuietly(serviceDiscovery);
    }

    @Override
    public ServiceInstance<T> serviceInstance(
            String serviceName,
            Predicate<T> payloadFilter,
            ServiceStrategy selectStrategy
    ) {
        try {
            Optional<ProviderStrategy<T>> strategy = getStrategy(selectStrategy);
            GearServiceProvider<T> provider = cache.get(
                    serviceName + "-" + selectStrategy.name(),
                    s -> startServiceProvider(serviceName, strategy)
            );

            org.apache.curator.x.discovery.ServiceInstance<T> instance;
            if (Objects.nonNull(payloadFilter)) {
                instance = provider.getInstance(
                        singletonList(ist -> payloadFilter.test(checkNotNull(ist).getPayload()))
                );
            } else {
                instance = provider.getInstance();
            }

            return instance;
        } catch (Exception e) {
            log.error("Failed to discover service " + serviceName, e);
            throw new GearRuntimeException("Failed to discover service " + serviceName, e);
        }
    }

    @Override
    public Collection<ServiceInstance<T>> serviceInstances(String serviceName) {
        try {
            Optional<ProviderStrategy<T>> strategy = getStrategy(ROUND_ROBIN);
            Collection<org.apache.curator.x.discovery.ServiceInstance<T>> instances = cache.get(
                    serviceName + "-" + ROUND_ROBIN.name(),
                    s -> startServiceProvider(serviceName, strategy)
            ).getAllInstances();

            checkState(!instances.isEmpty());

            return instances;
        } catch (Exception e) {
            log.error("Failed to discover service " + serviceName, e);
            throw new GearRuntimeException("Failed to discover service " + serviceName, e);
        }
    }

    @Override
    public ServiceInstance<T> registerService(T serviceProfile, String serviceName, int port) {
        return registerService(serviceProfile, serviceName,
                               LocalIpAddressResolver.localIpAddress(), port
        );
    }

    @Override
    public ServiceInstance<T> registerService(T serviceProfile, String serviceName, String address, int port) {
        try {
            org.apache.curator.x.discovery.ServiceInstance<T> instance = org.apache.curator.x.discovery.ServiceInstance.<T>builder()
                    .serviceType(ServiceType.DYNAMIC)
                    .name(serviceName)
                    .address(address)
                    .port(port)
                    .payload(serviceProfile).build();
            serviceDiscovery.registerService(instance);
            return instance;
        } catch (Exception e) {
            throw new GearRuntimeException("Failed to register current service " + serviceName, e);
        }
    }

    @Override
    public Map<String, Collection<ServiceInstance<T>>> serviceInstances() {
        try {
            return serviceDiscovery.queryForNames().stream().collect(
                    toMap(
                            name -> name,
                            name -> allInstancesOf(name)
                    )
            );
        } catch (Exception e) {
            throw new GearRuntimeException("Failed to discover any service under " + basePath, e);
        }
    }

    private Collection<org.apache.curator.x.discovery.ServiceInstance<T>> allInstancesOf(String serviceName) {
        try {
            return serviceDiscovery.queryForInstances(serviceName);
        } catch (Exception e) {
            throw new GearRuntimeException("Failed to query instance of service " + serviceName, e);
        }
    }

    private GearServiceProvider<T> startServiceProvider(String serviceName, Optional<ProviderStrategy<T>> strategy) {
        try {
            GearServiceProvider<T> provider = new GearServiceProvider<>(
                    (ServiceDiscoveryImpl<T>) serviceDiscovery,
                    serviceName,
                    strategy.orElse(new RoundRobinStrategy<>()),
                    ThreadUtils.newThreadFactory("GearServiceProvider"),
                    Lists.newArrayList(),
                    new DownInstancePolicy()
            );
            provider.start();
            return provider;
        } catch (Exception e) {
            throw new GearRuntimeException(e);
        }
    }

}
