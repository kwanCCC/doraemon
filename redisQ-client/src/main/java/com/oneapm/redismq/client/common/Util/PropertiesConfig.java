package com.oneapm.redismq.client.common.Util;

import com.google.common.collect.Sets;
import com.oneapm.redismq.client.common.ConsumerGroup;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.client.JedisClient;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesConfig {

    public static final String REDIS_CONSUMER_GROUPS = "redis.consumer.consumergroups";
    public static final String REDIS_HOST            = "redis.host";
    public static final String REDIS_PORT            = "redis.port";
    public static final String REDIS_PASSWORD        = "redis.passwword";
    public static final String PING_TIME_OUT_MS      = "redis.heart.beat";
    public static final String TOPICNAMES            = "redis.topics";
    public static final String COMMA                 = ",";
    public static final String COLON                 = ":";
    public static final String AND                   = "&";


    public static long metadataFetchTimeoutMs(Properties prop) {
        String orDefault = (String) prop.getOrDefault(PING_TIME_OUT_MS, "2000");
        return Long.parseLong(orDefault);
    }

    public static String redisHost(Properties prop) {
        String redisHost = prop.getProperty(REDIS_HOST);
        if (StringUtils.isNotBlank(redisHost)) {
            return redisHost;
        } else {
            throw new RuntimeException("missing redis host");
        }
    }

    public static int redisPort(Properties prop) {
        String redisHost = prop.getProperty(REDIS_PORT);
        if (StringUtils.isNotBlank(redisHost)) {
            return Integer.parseInt(redisHost);
        } else {
            throw new RuntimeException("missing redis port");
        }
    }

    public static String redisPassword(Properties prop) {
        return prop.getProperty(REDIS_PASSWORD);
    }


    public static JedisClient client(Properties prop) {
        String host = redisHost(prop);
        int redisPort = redisPort(prop);
        String redisPassword = redisPassword(prop);
        JedisClient client = new JedisClient(host, redisPort, redisPassword);
        if (!client.ping()) {
            throw new RuntimeException("ping " + host + redisPort + "redis server failed");
        }
        return client;
    }

    public static String[] topicNames(Properties prop) {
        String topicNames = prop.getProperty(TOPICNAMES);
        if (StringUtils.isNotBlank(topicNames)) {
            return splitByComma(topicNames);
        } else {
            throw new RuntimeException("missing topicNames e.g: redis.topics=topicA,topicB,topicC");
        }
    }

    public static String[] splitByComma(String chars) {
        return chars.split(COMMA);
    }

    public static String[] splitByColon(String chars) {
        return chars.split(COLON);
    }

    public static String[] splitByAND(String chars) {
        return chars.split(AND);

    }

    public static Map<TopicInfo, ConsumerGroup> getTopicAndConsumerInfo(Properties prop) {
        String raw = prop.getProperty(REDIS_CONSUMER_GROUPS);
        try {
            String[] topicsConsumerGroups = splitByAND(raw);
            HashMap<TopicInfo, ConsumerGroup> result = new HashMap<>();
            for (String topicCgps : topicsConsumerGroups) {
                String[] tc = splitByColon(topicCgps);
                TopicInfo topicInfo = new TopicInfo(tc[0]);
                String[] cpgs = splitByComma(tc[1]);
                ConsumerGroup consumerGroup = new ConsumerGroup(Sets.newHashSet(cpgs));
                result.put(topicInfo, consumerGroup);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(
                    "missing redis.consumer.consumergroups e.g: redis.consumer.consumergroups=topicA:consumerGroupA,consumerGroupAA&topicB:consumerGroupB");
        }
    }

}
