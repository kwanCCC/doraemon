package org.doraemon.treasure.gear;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.doraemon.treasure.gear.strategy.ServiceStrategy;
import org.doraemon.treasure.gear.api.Gear;
import org.doraemon.treasure.gear.api.ServiceDiscoveryClient;
import org.doraemon.treasure.gear.beans.ServiceProfile;
import org.doraemon.treasure.gear.serializer.PayLoadJsonSerializer;
import org.doraemon.treasure.gear.util.LocalIpAddressResolver;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
public class GearServiceImpl implements Gear {

    @Getter
    private final GearConfig gearConfig;
    private final InstanceSerializer<ServiceProfile> serializer = new PayLoadJsonSerializer();
    private final ServiceDiscoveryClient<ServiceProfile> serviceDiscoveryClient;
    @Getter
    private final CuratorFramework                       curatorClient;
    @Getter
    private ServiceInstance<ServiceProfile> currentInstance = null;

    public GearServiceImpl(GearConfig gearConfig) {
        this.gearConfig = gearConfig;
        this.curatorClient = newCuratorClient(gearConfig.getZkConfig());
        this.serviceDiscoveryClient = new ZookeeperServiceDiscoveryClient<>(
                curatorClient,
                serializer,
                gearConfig.getBase(),
                ServiceProfile.class
        );
    }

    @Override
    public void start() {
        log.info("gear client starting...");
        serviceDiscoveryClient.start();
        retrieveConfiguration();
    }

    private void retrieveConfiguration() {
        // register current service to zk
        currentInstance = serviceDiscoveryClient.registerService(
                ServiceProfile.builder()
                              .profile(gearConfig.getProfile())
                              .build(),
                gearConfig.getAppName(),
                serviceAddress(),
                gearConfig.getPort()
        );
    }

    private String serviceAddress() {
        String serviceAddress = gearConfig.getAddress();
        if (serviceAddress == null) {
            serviceAddress = LocalIpAddressResolver.localIpAddress();
        }
        return serviceAddress;
    }

    @Override
    public void stop() {
        serviceDiscoveryClient.stop();
        CloseableUtils.closeQuietly(curatorClient);
    }

    @Override
    public Optional<ServiceInstance<ServiceProfile>> serviceInstance(
            String serviceName,
            ServiceStrategy strategy,
            Predicate<ServiceProfile> payloadFilter
    ) {

        ServiceInstance<ServiceProfile> serviceInstance = serviceDiscoveryClient.serviceInstance(
                serviceName,
                payloadFilter,
                strategy
        );
        return Optional.ofNullable(serviceInstance);
    }

    @Override
    public Collection<ServiceInstance<ServiceProfile>> serviceInstances(String serviceName) {
        return serviceDiscoveryClient.serviceInstances(serviceName);
    }

    @Override
    public ServiceInstance<ServiceProfile> currentInstance() {
        return currentInstance;
    }

    private CuratorFramework newCuratorClient(ZKConfig zkConfig) {
        return CuratorFrameworkFactory.newClient(
                zkConfig.getConnect(),
                zkConfig.getSessionTimeout(),
                zkConfig.getConnectTimeout(),
                new ExponentialBackoffRetry(10, zkConfig.getRetry(), 10000)
        );
    }
}
