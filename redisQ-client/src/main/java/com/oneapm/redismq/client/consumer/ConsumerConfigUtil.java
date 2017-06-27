package com.oneapm.redismq.client.consumer;

import com.oneapm.redismq.client.common.Util.PropertiesConfig;
import com.oneapm.redismq.client.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

@Slf4j
public class ConsumerConfigUtil extends PropertiesConfig {

    public static final String CONSUMER_MAX_WAIT        = "redis.consumer.maxwait";
    public static final String CONSUMER_GROUPID         = "redis.consumer.groupid";
    public static final String REDIS_KEY_DESERIALIZER   = "redis.key.deserializer";
    public static final String REDIS_VALUE_DESERIALIZER = "redis.value.deserializer";

    public static final String DEFAULT_MAX_WAIT = "-1";

    public static long maxWait(Properties prop) {
        String smaxWait = prop.getProperty(CONSUMER_MAX_WAIT, DEFAULT_MAX_WAIT);
        return Long.parseLong(smaxWait.trim());
    }

    public static String consumerGroup(Properties properties) {
        String groupid = properties.getProperty(CONSUMER_GROUPID);
        if (StringUtils.isNotBlank(groupid)) {
            return groupid;
        } else {
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                log.info("load redis consumer groupid fail", e);
                throw new RedisException(e);
            }
        }
    }
}
