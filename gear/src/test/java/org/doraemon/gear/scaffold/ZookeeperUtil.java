package org.doraemon.gear.scaffold;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;

public class ZookeeperUtil {

    public boolean nodeExist(String connectString, String path) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                                                                   .connectString(connectString)
                                                                   .retryPolicy(new RetryNTimes(3, 1000))
                                                                   .build();
        curatorFramework.start();
        Stat stat = curatorFramework.checkExists().forPath(path);

    }
}
