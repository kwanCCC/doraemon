package com.oneapm.redismq.client.common.serialization;

import java.util.Map;

public interface RedisSerializer<T> {
    /**
     * Configure this class.
     *
     * @param configs configs in key/value pairs
     * @param isKey   whether is for key or value
     */
    void setConfigs(Map<String, ?> configs, boolean isKey);

    /**
     * @param topic topic name
     * @param data object
     *
     * @return byte after serializer
     */
    byte[] redisSerialize(String topic, T data);

    /**
     * Close this serialize
     */
    void closeSerialize();

    String KEY_SERIALIZER_ENCODING = "key.serializer.encoding";
    String VALUE_SERIALIZER_ENCODING="value.serializer.encoding";
    String SERIALIZER_ENCODING="serializer.encoding";
}
