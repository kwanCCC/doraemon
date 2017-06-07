package org.doraemon.gear;

import org.apache.curator.test.TestingServer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by guanboyu on 2017/6/4.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(GearIntegrationTest.class)
public class GearIntegrationTest {

    public static TestingServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        server = new TestingServer();
    }


    @Test
    public void gear_can_registered_self() throws Exception {
    }
}
