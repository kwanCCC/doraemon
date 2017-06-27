package com.oneapm.redismq.client.consumer;

import com.oneapm.redismq.client.common.Entry;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.serialization.RedisDeserializer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KeyAndMessage<K, V> {

    private RedisDeserializer<K> keyDeserializer;
    private RedisDeserializer<V> valueDeserializer;
    private Entry                rawMessage;
    private TopicInfo            topicInfo;

    public K key() {
        byte[] key = rawMessage.key();
        return keyDeserializer.redisDeserialize(topicInfo.topic(), key);
    }

    public V value() {
        byte[] value = rawMessage.value();
        return valueDeserializer.redisDeserialize(topicInfo.topic(), value);
    }
}
