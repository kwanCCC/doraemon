package org.doraemon.treasure.gear.scaffold;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Objects;

public class ZookeeperUtil {

    public static boolean nodeExist(String connectString, String path) throws Exception {
        try (CuratorFramework curatorFramework = setUpFrameWork(connectString)) {
            Stat stat = curatorFramework.checkExists().forPath(path);
            return Objects.nonNull(stat);
        }
    }

    public static void createNode(String connectString, String path, byte[] payload) throws Exception {
        try (CuratorFramework curatorFramework = setUpFrameWork(connectString)) {
            curatorFramework.create()
                            .creatingParentContainersIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .forPath(path, payload);
        }
    }

    public static byte[] getNode(String connectString, String path) throws Exception {
        try (CuratorFramework curatorFramework = setUpFrameWork(connectString)) {
            byte[] bytes = curatorFramework.getData().forPath(path);
            return bytes;
        }
    }

    public static void setData(String coonectString, String path, byte[] payload) throws Exception {
        try (CuratorFramework curatorFramework = setUpFrameWork(coonectString)) {
            curatorFramework.setData().withVersion(999).forPath(path, payload);
        }
    }

    private static CuratorFramework setUpFrameWork(String connectString) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                                                                   .sessionTimeoutMs(10000)
                                                                   .connectString(connectString)
                                                                   .retryPolicy(new RetryNTimes(3, 1000))
                                                                   .build();
        curatorFramework.start();
        return curatorFramework;
    }
}
