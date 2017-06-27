package com.oneapm.redismq.client.common.serialization;

import java.util.Map;

public class ByteRedisDeserializer implements RedisDeserializer<byte[]> {

    @Override
    public void setConfigs(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] redisDeserialize(String topic, byte[] data) {
        return data;
    }

    @Override
    public void closeSerialize() {

    }
}
