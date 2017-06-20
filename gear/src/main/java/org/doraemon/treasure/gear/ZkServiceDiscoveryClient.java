package org.doraemon.treasure.gear;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.doraemon.treasure.gear.api.ServiceDiscoveryClient;
import org.doraemon.treasure.gear.exception.GearRuntimeException;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.doraemon.treasure.gear.SelectStrategyFactory.getStrategy;

@Slf4j
public class ZkServiceDiscoveryClient<T> implements ServiceDiscoveryClient<T> {

    private final GearConfig       gearConfig;
    private final CuratorFramework curator;
    private       ServiceDiscovery discovery;
    private final Cache<String, ServiceProvider> providerCache = Caffeine.newBuilder()
                                                                         .initialCapacity(5)
                                                                         .maximumSize(2000)
                                                                         .expireAfterAccess(
                                                                                 5,
                                                                                 TimeUnit.MINUTES
                                                                         )
                                                                         .removalListener((key, value, cause) -> {
                                                                             if (value instanceof ServiceProvider) {
                                                                                 CloseableUtils.closeQuietly(((ServiceProvider) value));
                                                                             }
                                                                         }).build();

    public ZkServiceDiscoveryClient(GearConfig gearConfig) {
        this(gearConfig, CuratorFrameworkFactory.builder()
                                                .connectString(gearConfig.getZkConfig().getConnect())
                                                .sessionTimeoutMs(gearConfig.getZkConfig().getSessionTimeout())
                                                .connectionTimeoutMs(gearConfig.getZkConfig().getConnectTimeout())
                                                .retryPolicy(new RetryNTimes(
                                                        gearConfig.getZkConfig().getRetry()
                                                        ,
                                                        gearConfig.getZkConfig().getSessionTimeout() / 3
                                                ))
                                                .build());
    }

    public ZkServiceDiscoveryClient(GearConfig gearConfig, CuratorFramework curator) {
        this.gearConfig = gearConfig;
        this.curator = curator;
    }

    @Override
    public void start() {
        discovery = ServiceDiscoveryBuilder.builder(gearConfig.getClass())
                                           .basePath(gearConfig.getBase())
                                           .client(curator)
                                           .serializer(gearConfig.getSerializer())
                                           .build();
        try {
            curator.start();
            discovery.start();
        } catch (Exception e) {
            throw new GearRuntimeException(e);
        }
    }

    @Override
    public void stop() {
        CloseableUtils.closeQuietly(discovery);
        CloseableUtils.closeQuietly(curator);
    }

    @Override
    public ServiceInstance<T> serviceInstance(String appName, ServiceStrategy strategy, Predicate filter)
            throws Exception {
        Optional<ProviderStrategy<T>> providerStrategy = getStrategy(strategy);
        ServiceProvider serviceProvider = providerCache.get(
                appName,
                s -> createServiceProvider(appName, filter, providerStrategy)
        );
        return serviceProvider.getInstance();
    }

    private ServiceProvider createServiceProvider(
            String appName,
            Predicate filter,
            Optional<ProviderStrategy<T>> providerStrategy
    ) {
        ServiceProvider provider = discovery.serviceProviderBuilder()
                                            .providerStrategy(providerStrategy.get())
                                            .additionalFilter(instanceFilter -> filter.test(instanceFilter.getPayload()))
                                            .serviceName(appName)
                                            .build();
        try {
            provider.start();
        } catch (Exception e) {
            log.error("start service provider error . ", e);
        }
        return provider;
    }

    @Override
    public Collection<ServiceInstance<T>> serviceInstances(
            String appName,
            ServiceStrategy strategy,
            Predicate<T> filter
    ) throws Exception {
        Optional<ProviderStrategy<T>> providerStrategy = getStrategy(strategy);
        ServiceProvider serviceProvider = providerCache.get(
                appName,
                s -> createServiceProvider(appName, filter, providerStrategy)
        );
        return serviceProvider.getAllInstances();
    }

    @Override
    public void registerService(ServiceInstance<T> serviceInstance) throws Exception {
        discovery.registerService(serviceInstance);
    }

    @Override
    public void unregisterService(ServiceInstance<T> serviceInstance) throws Exception {
        discovery.unregisterService(serviceInstance);
    }
}