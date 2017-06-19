package org.doraemon.treasure.gear;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
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
import org.doraemon.treasure.gear.exception.GearCannotGetServiceException;
import org.doraemon.treasure.gear.exception.GearRuntimeException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ZkServiceDiscoveryClient<T> implements ServiceDiscoveryClient<T> {

    private final GearConfig       gearConfig;
    private final CuratorFramework curator;
    private       ServiceDiscovery discovery;
    private final Caffeine<String, ServiceProvider> serviceProviderMap=Caffeine.newBuilder().initialCapacity(5).maximumSize(2000).

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
            throws GearCannotGetServiceException {
        Optional<ProviderStrategy<Object>> providerStrategy = SelectStrategyFactory.getStrategy(strategy);
        ServiceProvider serviceProvider;
        try {
            serviceProvider = discovery.serviceProviderBuilder()
                                       .providerStrategy(providerStrategy.get())
                                       .additionalFilter(instanceFilter -> filter.test(instanceFilter.getPayload()))
                                       .serviceName(appName).build();
            serviceProvider.start();
            return serviceProvider.getInstance();
        } catch (Exception e) {
            throw new GearCannotGetServiceException(e);
        } finally {

        }
    }

    @Override
    public Collection<ServiceInstance<T>> serviceInstances(
            String appName,
            ServiceStrategy strategy,
            Predicate<T> filter
    ) {
        return null;
    }
}
