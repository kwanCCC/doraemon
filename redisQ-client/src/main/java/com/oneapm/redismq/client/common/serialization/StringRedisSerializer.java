package com.oneapm.redismq.client.common.serialization;

import java.nio.charset.Charset;
import java.util.Map;

public class StringRedisSerializer implements RedisSerializer<String> {
    private Charset encoding = Charset.forName("UTF-8");

    @Override
    public void setConfigs(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? KEY_SERIALIZER_ENCODING : VALUE_SERIALIZER_ENCODING;
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) {
            encodingValue = configs.get(SERIALIZER_ENCODING);
        }
        if (encodingValue != null && encodingValue instanceof String) {
            encoding = Charset.forName((String) encodingValue);
        }
    }

    @Override
    public byte[] redisSerialize(String topic, String data) {
        if (data == null) {
            return null;
        } else {
            return data.getBytes(encoding);
        }
    }

    @Override
    public void closeSerialize() {
    }

}
