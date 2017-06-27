package org.doraemon.treasure.gear;

import java.io.IOException;
import java.net.BindException;

import org.apache.curator.test.TestingServer;

public class AbstractZKTest {

    TestingServer server;

    protected void beforeTest() throws Exception {
        while (server == null) {
            try {
                server = new TestingServer();
            } catch (BindException e) {
                System.err.println("Getting bind exception - retrying to allocate server");
                server = null;
            }
        }
    }

    protected void teardown() throws Exception {
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                server = null;
            }
        }
    }

    protected TestingServer zkServer() {
        return server;
    }
}
