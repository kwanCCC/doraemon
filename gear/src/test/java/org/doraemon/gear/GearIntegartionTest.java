package org.doraemon.gear;

import org.apache.curator.test.TestingServer;
import org.junit.BeforeClass;
import org.junit.Test;

public class GearIntegartionTest {

    public static TestingServer zookeeper;

    @BeforeClass
    public void setUp() throws Exception {
        zookeeper = new TestingServer();
        zookeeper.start();
    }

    @Test
    public void server_register_self_then_discovery_others() {
        Gear gear = new Gear();
        gear.start();
        
    }
}
