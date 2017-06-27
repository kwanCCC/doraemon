package com.oneapm.redismq.client.consumer;

import com.oneapm.redismq.client.common.serialization.RedisDeserializer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MessageConsumer {
    <K, V> Map<String, RedisStream<K, V>> createMessageStreams(
            Collection<String> topics,
            RedisDeserializer<K> keyDeserializer,
            RedisDeserializer<V> valueDeserializer
    );
}
