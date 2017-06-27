package com.oneapm.redismq.client.common.serialization;

import java.util.Map;

public interface RedisDeserializer<T> {

    /**
     * Configure this class.
     *
     * @param configs configs in key/value pairs
     * @param isKey   whether is for key or value
     */
    void setConfigs(Map<String, ?> configs, boolean isKey);

    /**
     * @param topic topicName
     * @param data message to be sended
     *
     * @return Generic
     */
    T redisDeserialize(String topic, byte[] data);

    /**
     * Close this deserialize
     */
    void closeSerialize();

    String KEY_DESERIALIZER_ENCODING   = "key.deserializer.encoding";
    String VALUE_DESERIALIZER_ENCODING = "value.deserializer.encoding";
    String DESERIALIZER_ENCODING       = "deserializer.encoding";
}
