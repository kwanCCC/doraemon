package com.oneapm.redismq.client.common.serialization;

import java.nio.charset.Charset;
import java.util.Map;

public class StringRedisDeserializer implements RedisDeserializer<String> {
    private Charset encoding = Charset.forName("UTF-8");

    @Override
    public void setConfigs(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? KEY_DESERIALIZER_ENCODING : VALUE_DESERIALIZER_ENCODING;
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) {
            encodingValue = configs.get(DESERIALIZER_ENCODING);
        }
        if (encodingValue != null && encodingValue instanceof String) {
            encoding = Charset.forName((String) encodingValue);
        }
    }

    @Override
    public String redisDeserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        } else {
            return new String(data, encoding);
        }
    }

    @Override
    public void closeSerialize() {
    }
}
