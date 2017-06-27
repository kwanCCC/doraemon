package org.doraemon.treasure.gear;

import org.apache.curator.test.TestingServer;
import org.doraemon.treasure.gear.api.Gear;
import org.doraemon.treasure.gear.scaffold.ZookeeperUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GearIntegartionTest {

    public static TestingServer zookeeper;

    @BeforeClass
    public static void setUp() throws Exception {
        zookeeper = new TestingServer();
        zookeeper.start();
    }

    @Test
    public void server_register_self_then_discovery_others() throws Exception {
        ZKConfig zkConfig = ZKConfig.builder()
                                    .sessionTimeout(3000)
                                    .connectTimeout(1000)
                                    .connect(zookeeper.getConnectString())
                                    .build();
        GearConfig gearConfig = GearConfig.builder()
                                          .base("doraemon")
                                          .zkConfig(zkConfig)
                                          .localConfigEnable(true)
                                          .appName("testApp")
                                          .version("1.0.0")
                                          .address("4.4.4.4")
                                          .port(9999)
                                          .profile("test")
                                          .build();
        Gear gear = new GearServiceImpl(gearConfig);
        gear.start();
        Assert.assertTrue(ZookeeperUtil.nodeExist(zookeeper.getConnectString(), "/doramemon/treasure/testApp"));
    }
}
