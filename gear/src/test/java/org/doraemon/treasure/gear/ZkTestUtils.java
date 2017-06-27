package org.doraemon.treasure.gear;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;

public class ZkTestUtils {
    public static boolean pathExists(TestingServer server, String path) {
        try (CuratorFramework client = getClient(server)) {
            client.start();
            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            throw new RuntimeException("error when checking path existence: " + e, e);
        }
    }

    public static byte[] getData(TestingServer server, String path) {
        try (CuratorFramework client = getClient(server)) {
            client.start();
            return client.getData().forPath(path);
        } catch (Exception e) {
            throw new RuntimeException("error when get node data: " + e, e);
        }
    }

    public static boolean createNode(TestingServer server, String path, byte[] data) {
        try (CuratorFramework client = getClient(server)) {
            client.start();
            return client.create().creatingParentContainersIfNeeded().forPath(path, data) != null;
        } catch (Exception e) {
            throw new RuntimeException("error when set node data: " + e, e);
        }
    }

    public static boolean createNode(TestingServer server, String path) {
        try (CuratorFramework client = getClient(server)) {
            client.start();
            return client.create().creatingParentContainersIfNeeded().forPath(path) != null;
        } catch (Exception e) {
            throw new RuntimeException("error when set node data: " + e, e);
        }
    }

    public static boolean setData(TestingServer server, String path, byte[] data) {
        try (CuratorFramework client = getClient(server)) {
            client.start();
            return client.setData().withVersion(-1).forPath(path, data) != null;
        } catch (Exception e) {
            throw new RuntimeException("error when set node data: " + e, e);
        }
    }

    public static CuratorFramework getClient(TestingServer server) {
        return CuratorFrameworkFactory.builder().retryPolicy(new RetryNTimes(3, 1000)).connectString(server.getConnectString()).build();
    }
}
