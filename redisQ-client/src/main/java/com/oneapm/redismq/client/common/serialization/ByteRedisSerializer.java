package com.oneapm.redismq.client.common.serialization;

import java.util.Map;

public class ByteRedisSerializer implements RedisSerializer<byte[]> {

    @Override
    public void setConfigs(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] redisSerialize(String topic, byte[] data) {
        return data;
    }

    @Override
    public void closeSerialize() {

    }
}
