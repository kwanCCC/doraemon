package org.doraemon.treasure.gear;

import com.google.common.collect.Lists;
import org.apache.curator.x.discovery.ServiceInstance;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GearClientTest {

    @Test
    public void test_gear_setUp_then_stop() throws Exception {
        GearConfig gearConfig = Mockito.mock(GearConfig.class);
        GearClient gearClient = new GearClient(gearConfig);
        gearClient.start();
        assertTrue(gearClient.isRunning());
        gearClient.stop();
        assertFalse(gearClient.isRunning());
    }


    @Test
    public void test_gear_serviceInstance_then_call_underlying_serviceInstance() throws Exception {
        GearConfig gearConfig = Mockito.mock(GearConfig.class);
        ZkServiceDiscoveryClient serviceDiscoveryClient = Mockito.mock(ZkServiceDiscoveryClient.class);
        GearClient gearClient = new GearClient(gearConfig, serviceDiscoveryClient);
        ServiceInstance<Object> serviceInstance = ServiceInstance.builder()
                                                                 .address("localhost")
                                                                 .port(22)
                                                                 .id("aasdf")
                                                                 .name("test")
                                                                 .build();
        Mockito.when(serviceDiscoveryClient.serviceInstance(Mockito.anyString())).thenReturn(serviceInstance);
        gearClient.serviceInstance("test");
        Mockito.verify(serviceDiscoveryClient).serviceInstance("test");
    }

    @Test
    public void test_gear_serviceInstance_then_call_underlying_serviceInstances() throws Exception {
        GearConfig gearConfig = Mockito.mock(GearConfig.class);
        ZkServiceDiscoveryClient serviceDiscoveryClient = Mockito.mock(ZkServiceDiscoveryClient.class);
        GearClient gearClient = new GearClient(gearConfig, serviceDiscoveryClient);
        ServiceInstance<Object> serviceInstance = ServiceInstance.builder()
                                                                 .address("localhost")
                                                                 .port(22)
                                                                 .id("aasdf")
                                                                 .name("test")
                                                                 .build();
        Mockito.when(serviceDiscoveryClient.serviceInstances(Mockito.anyString()))
               .thenReturn(Lists.newArrayList(serviceInstance));
        gearClient.serviceInstances("test");
        Mockito.verify(serviceDiscoveryClient).serviceInstances("test");
    }
}