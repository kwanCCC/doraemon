package com.oneapm.redismq.client.consumer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oneapm.redismq.client.common.ConsumerGroup;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.Util.SystemTime;
import com.oneapm.redismq.client.common.Util.Time;
import com.oneapm.redismq.client.common.Utils;
import com.oneapm.redismq.client.common.buffer.CopyOnWriteMap;
import com.oneapm.redismq.client.common.client.ConsumerGrouplistener;
import com.oneapm.redismq.client.common.client.JedisClient;
import com.oneapm.redismq.client.common.serialization.RedisDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Slf4j
public class DefaultMessageConsumer implements MessageConsumer {

    private final JedisClient           client;
    private final ConsumerGrouplistener grouplistener;
    private final Time                  time;
    private final long                  maxWait;
    private final Map<TopicInfo, ConsumerGroup> batches = new CopyOnWriteMap<>();
    private final String groupId;

    public DefaultMessageConsumer(Properties prop) {
        this.client = ConsumerConfigUtil.client(prop);
        this.time = new SystemTime();
        this.maxWait = ConsumerConfigUtil.maxWait(prop);
        long heartbeat = ConsumerConfigUtil.metadataFetchTimeoutMs(prop);
        this.groupId = ConsumerConfigUtil.consumerGroup(prop);
        String[] topicNames = ConsumerConfigUtil.topicNames(prop);
        for (String topicName : topicNames) {
            batches.put(new TopicInfo(topicName), new ConsumerGroup(Sets.newHashSet(groupId)));
        }
        this.grouplistener = new ConsumerGrouplistener(this.client, heartbeat, batches);
        grouplistener.consumerStart(this.batches);
        grouplistener.waitOnMetaData();
    }

    @Override
    public <K, V> Map<String, RedisStream<K, V>> createMessageStreams(
            Collection<String> topics, RedisDeserializer<K> keyDeserializer, RedisDeserializer<V> valueDeserializer
    ) {
        Map<String, RedisStream<K, V>> topicVsRedisStream = Maps.newHashMap();
        topics.stream().filter(topic -> grouplistener.getTopics().contains(topic)).forEach(topic -> {
            RedisStream<K, V> kvRedisStream = RedisStream.of(
                    client,
                    new TopicInfo(topic),
                    Utils.mergeByteArray(topic, groupId),
                    keyDeserializer,
                    valueDeserializer,
                    grouplistener
            );
            topicVsRedisStream.put(topic, kvRedisStream);
        });
        return topicVsRedisStream;
    }
}
