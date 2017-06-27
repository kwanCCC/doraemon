package com.oneapm.redismq.client.consumer;

import com.oneapm.redismq.client.common.Entry;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.client.ConsumerGrouplistener;
import com.oneapm.redismq.client.common.client.JedisClient;
import com.oneapm.redismq.client.common.serialization.RedisDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class RedisStream<K, V> implements Iterable<KeyAndMessage<K, V>> {

    private final JedisClient           client;
    private final TopicInfo             topicInfo;
    private final byte[]                realTopicName;
    private final RedisDeserializer<K>  keyDeserializer;
    private final RedisDeserializer<V>  valueDeserializer;
    private final ConsumerGrouplistener grouplistener;

    private RedisStream(
            JedisClient client,
            TopicInfo topicInfo,
            byte[] realTopicName,
            RedisDeserializer<K> keyDeserializer,
            RedisDeserializer<V> valueDeserializer,
            ConsumerGrouplistener grouplistener
    ) {
        this.client = client;
        this.topicInfo = topicInfo;
        this.realTopicName = realTopicName;
        this.keyDeserializer = keyDeserializer;
        this.valueDeserializer = valueDeserializer;
        this.grouplistener = grouplistener;
    }

    public static <K, V> RedisStream<K, V> of(
            JedisClient client,
            TopicInfo topicInfo,
            byte[] realTopicName,
            RedisDeserializer<K> keyDeserializer,
            RedisDeserializer<V> valueDeserializer,
            ConsumerGrouplistener grouplistener
    ) {
        return new RedisStream<>(
                client,
                topicInfo,
                realTopicName,
                keyDeserializer,
                valueDeserializer,
                grouplistener
        );
    }

    private final ConsumerIterator iterator = new ConsumerIterator();

    private class ConsumerIterator implements Iterator {

        @Override
        public boolean hasNext() {
            boolean exist = grouplistener.exist(topicInfo);
            Set<byte[]> topicCgpInfomation = grouplistener.getTopicCgpInfomation(topicInfo);
            for (byte[] item : topicCgpInfomation) {
                if (Arrays.equals(item, realTopicName)) {
                    return exist && true;
                }
            }
            return false;
        }

        @Override
        public KeyAndMessage next() {
            byte[] bytes = null;
            while (bytes == null || bytes.length == 0) {
                bytes = client.consumerFromTopics(realTopicName);
            }
            return new KeyAndMessage(keyDeserializer, valueDeserializer, new Entry(bytes), topicInfo);
        }
    }

    @Override
    public Iterator<KeyAndMessage<K, V>> iterator() {
        return iterator;
    }
}
