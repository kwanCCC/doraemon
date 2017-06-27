package com.oneapm.redismq.client.producer;

import com.google.common.collect.Sets;
import com.oneapm.redismq.client.common.ConsumerGroup;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.Util.PropertiesConfig;
import com.oneapm.redismq.client.common.Utils;
import com.oneapm.redismq.client.exception.SerializerException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProducerConfigUtil extends PropertiesConfig {

    public static final String REDIS_KEY_SERIALIZER   = "redis.key.serializer";
    public static final String REDIS_VALUE_SERIALIZER = "redis.value.serializer";
    public static final String REDIS_BATCH_SIZE       = "redis.producer.batchSize";
    public static final String REDIS_LINGGER_MS       = "redis.linger.ms";
    public static final String REDIS_CLIENT_SSID      = "redis.client.id";
    public static final int    DEFAULT_BATCH_SIZE     = 8;
    public static final long   DEFAULT_LINGERMS       = 10;

    public static int batchSize(Properties prop) {
        String batchSize = prop.getProperty(REDIS_BATCH_SIZE);
        if (StringUtils.isNotBlank(batchSize)) {
            return Integer.parseInt(batchSize.trim());
        } else {
            return DEFAULT_BATCH_SIZE;
        }
    }

    public static long lingerMs(Properties prop) {
        String lingerMs = prop.getProperty(REDIS_LINGGER_MS);
        if (StringUtils.isNotBlank(lingerMs)) {
            return Long.parseLong(lingerMs.trim());
        } else {
            return DEFAULT_LINGERMS;
        }
    }

    public static String clientSSID(Properties prop) {
        String ssid = prop.getProperty(REDIS_CLIENT_SSID);
        if (StringUtils.isNotBlank(ssid)) {
            return ssid;
        }
        return null;
    }

    public static String getString(Properties properties, String key) {
        return properties.getProperty(key);
    }

    public static <T> T getConfiguredInstance(Properties prop, String key, Class<T> t) {
        String value = getString(prop, key);
        return getConfiguredInstance(value, t);
    }

    public static <T> T getConfiguredInstance(String packagePath, Class<T> t) {
        Class<?> c = getClass(packagePath);
        if (c == null) {
            return null;
        }
        Object o = Utils.newInstance(c);
        if (!t.isInstance(o)) {
            throw new SerializerException(c.getName() + " is not an instance of " + t.getName());
        }
        return t.cast(o);
    }

    public static Class<?> getClass(String key) {
        try {
            return Class.forName(key, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
