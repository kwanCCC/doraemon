package com.oneapm.redismq.client.common.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.oneapm.redismq.client.common.ConsumerGroup;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.Utils;
import com.oneapm.redismq.client.common.buffer.CopyOnWriteMap;
import com.oneapm.redismq.client.exception.RedisException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * producer or consumer listener Manage topic and consumerGroup Infomation
 *
 */
@Slf4j
public final class ConsumerGrouplistener {

    private final ConcurrentMap<TopicInfo, ConsumerGroup> batches;
    private final ConcurrentMap<TopicInfo, Set<byte[]>>   realTopicNames;
    private final CountDownLatch latch = new CountDownLatch(1);
    private final long        heartbeat;
    private final JedisClient client;
    private final Timer       upToDate;


    /**
     * redis client listener
     * Divided into Producer mode and Consumer mode
     * it store topic and consumerGroups locally
     *
     * @param client jedis client
     * @param heartbeat listener period
     */
    public ConsumerGrouplistener(
            JedisClient client,
            long heartbeat
    ) {
        this.client = client;
        this.heartbeat = heartbeat;
        this.batches = new CopyOnWriteMap<>();
        this.realTopicNames = new CopyOnWriteMap<>();
        this.upToDate = new Timer(true);
    }

    public ConsumerGrouplistener(
            JedisClient client,
            long heartbeat,
            Map<TopicInfo, ConsumerGroup> topicAndConsumerInfo
    ) {
        this.client = client;
        this.heartbeat = heartbeat;
        this.batches = new CopyOnWriteMap<>();
        batches.putAll(topicAndConsumerInfo);
        this.realTopicNames = new CopyOnWriteMap<>();
        this.upToDate = new Timer(true);
    }

    /**
     * it will return a null when there is not any consumerGroup startup
     *
     * @param tp topicInfo
     *
     * @return ConsumerGroup
     */
    public Set<byte[]> getTopicCgpInfomation(TopicInfo tp) {
        return realTopicNames.get(tp);
    }

    public ConsumerGroup getConsumerGroup(TopicInfo tp) {
        return batches.get(tp);
    }

    public Set<String> getTopics() {
        return batches.keySet().stream().map(k -> k.topic()).collect(Collectors.toSet());
    }

    @AllArgsConstructor
    class _SyncReadConsumerGroup extends TimerTask {
        private final JedisClient                   jedis;
        private final Map<TopicInfo, ConsumerGroup> tpCgp;

        @Override
        public void run() {
            try {
                /**
                 *只有第一次启动才会创建未存在的Topic和相匹配的ConsumerGroup
                 */
                if (isturningOn()) {
                    log.info("Producer Listen action start ...");
                    storeTopicConusmeGroup(client, tpCgp);
                }
                Set<TopicInfo> topicInfos = tpCgp.keySet();
                Preconditions.checkArgument(waitOnMetaData(
                        jedis,
                        topicInfos.toArray(new TopicInfo[topicInfos.size()])
                ));
                topicInfos.forEach(item -> {
                    ConsumerGroup consumerGroup = jedis.sycnConsumerGroup(item.topic());
                    log.debug(
                            "Synchronize read result .topic {} and consumerGroup : {}",
                            item.topic(),
                            consumerGroup.toString()
                    );
                    if (batches.containsKey(item)) {
                        ConsumerGroup consumerGroupOfTopic = batches.get(item);
                        consumerGroup.merge(consumerGroupOfTopic);
                    }
                    batches.put(item, consumerGroup);
                    log.debug("store real topic {} locally", item.topic());
                    realTopicNames.put(item, rebuild(item, consumerGroup));
                });
                if (isturningOn()) {
                    setUp();
                    log.info("producer listener start up");
                }
            } catch (Throwable e) {
                throw new RedisException(e);
            }
        }
    }

    public Set<byte[]> rebuild(TopicInfo tp, ConsumerGroup cgps) {
        Set<String> consumerGroup = cgps.getConsumerGroup();
        Set<byte[]> realTopicNames = Sets.newHashSetWithExpectedSize(consumerGroup.size());
        for (String cpg : consumerGroup) {
            byte[] bytes = Utils.mergeByteArray(tp.topic(), cpg);
            log.debug("store real topic name {}", new String(bytes));
            realTopicNames.add(bytes);
        }
        return realTopicNames;
    }

    /**
     * consumer write action
     */
    @AllArgsConstructor
    class _SyncWriteConsumerGroup extends TimerTask {
        private final JedisClient                   jedis;
        private final Map<TopicInfo, ConsumerGroup> tpCgp;

        @Override
        public void run() {
            try {
                /**
                 *初始化的时写入Topic ConsumerGroup 信息
                 */
                if (isturningOn()) {
                    storeTopicConusmeGroup(client, tpCgp);
                }
                Set<TopicInfo> topicInfos = tpCgp.keySet();
                Preconditions.checkArgument(waitOnMetaData(
                        jedis,
                        topicInfos.toArray(new TopicInfo[topicInfos.size()])
                ));
                tpCgp.forEach((k, v) -> {
                    Set<String> consumer = v.getConsumerGroup();
                    /**
                     *以本地的信息更新Redis
                     */
                    jedis.addConsumerGroupOfTopic(k.topic(), consumer.toArray(new String[consumer.size()]));
                    realTopicNames.put(k, rebuild(k, v));
                    log.debug(
                            "Consumer register CONSUMERGROUP in TOPIC {} and consumerGroup {}",
                            k.topic(),
                            v.toString()
                    );
                });
                if (isturningOn()) {
                    setUp();
                    log.info("consumer listener start up");
                }
            } catch (Throwable e) {
                throw new RedisException(e);
            }
        }
    }

    private boolean waitOnMetaData(JedisClient client, TopicInfo... topicInfos) {
        if (client.ping()) {
            for (TopicInfo tp : topicInfos) {
                if (!client.exitsTopic(tp.topic())) {
                    log.error(
                            "redis server hasn't any set (topicName {consumerGroup}) named {}",
                            tp.topic()
                    );
                    return false;
                } else {
                    log.debug("redis server has topic {}", tp.topic());
                }
            }
        } else {
            log.error("Lost contact with the redis server");
        }
        return true;
    }

    public void waitOnMetaData() {
        try {
            latch.await(heartbeat, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error("consumer group listener set up fail", e);
            Thread.currentThread().interrupt();
        }
    }

    public void producerStart(Map<TopicInfo, ConsumerGroup> tpCgp) {
        this.upToDate.scheduleAtFixedRate(
                new _SyncReadConsumerGroup(this.client, tpCgp),
                0,
                this.heartbeat
        );
    }

    public void consumerStart(Map<TopicInfo, ConsumerGroup> tpCgp) {
        this.upToDate.scheduleAtFixedRate(
                new _SyncWriteConsumerGroup(this.client, tpCgp),
                0,
                this.heartbeat
        );
    }

    public void close() {
        this.upToDate.cancel();
        this.batches.clear();
    }

    public boolean isturningOn() {
        return latch.getCount() == 1;
    }

    public void setUp() {
        latch.countDown();
    }

    public boolean exist(TopicInfo tp) {
        return batches.containsKey(tp);
    }

    public void storeTopicConusmeGroup(JedisClient client, Map<TopicInfo, ConsumerGroup> tpCgp) {
        Preconditions.checkNotNull(tpCgp);
        tpCgp.forEach((k, v) -> {
            Set<String> consumerGroup = v.getConsumerGroup();
            client.addConsumerGroupOfTopic(
                    k.topic(),
                    consumerGroup.toArray(new String[consumerGroup.size()])
            );
        });
    }
}
