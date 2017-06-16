package org.doraemon.treasure.gear;

import lombok.AllArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.doraemon.treasure.gear.api.ServiceDiscoveryClient;

import java.util.Collection;

public class ZkServiceDiscoveryClient implements ServiceDiscoveryClient {

    private final GearConfig       gearConfig;
    private final CuratorFramework curator;

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
        ServiceDiscoveryBuilder.builder(asdf).basePath(gearConfig.getBase()).client(curator).thisInstance()
    }

    @Override
    public void stop() {

    }

    @Override
    public ServiceInstance serviceInstance(String appName) {
        return null;
    }

    @Override
    public Collection<ServiceInstance> serviceInstances(String appName) {
        return null;
    }
}
