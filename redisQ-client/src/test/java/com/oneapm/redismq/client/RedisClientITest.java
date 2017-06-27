package com.oneapm.redismq.client;

import com.oneapm.redismq.client.common.Record;
import com.oneapm.redismq.client.common.client.JedisClient;
import com.oneapm.redismq.client.common.serialization.StringRedisDeserializer;
import com.oneapm.redismq.client.consumer.DefaultMessageConsumer;
import com.oneapm.redismq.client.consumer.KeyAndMessage;
import com.oneapm.redismq.client.consumer.RedisStream;
import com.oneapm.redismq.client.producer.DefaultMessageSender;
import com.oneapm.redismq.client.testkit.ReloadProperties;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

@Slf4j
public class RedisClientITest {

    public RedisClientITest() throws Exception {
        port = prepareEnv();
        client = new JedisClient("localhost", port, null);
    }

    final JedisClient client;
    static int port = 6379;

    public int prepareEnv() throws Exception {
        InputStream resource = RedisClientITest.class.getClassLoader().getResourceAsStream("env.properties");
        Properties properties = new Properties();
        properties.load(resource);
        String redis_port = properties.getProperty("REDIS_PORT");
        return Integer.parseInt(redis_port);
    }

    long times = 1000;

    @Test
    public void testProducerToMultiTopicAndConsumerGroup() throws Exception {
        testProducer("test_b", times);
        testProducer("test_a", times);
        Assert.assertTrue(client.exitsTopic("test_b"));
        Assert.assertTrue(client.exitsTopic("test_a"));
        Assert.assertEquals(times, client.getQueueLength("test_b", "consumerGroupA"));
        Assert.assertEquals(times, client.getQueueLength("test_b", "consumerGroupB"));
        Assert.assertEquals(times, client.getQueueLength("test_b", "consumerGroupC"));
        Assert.assertEquals(times, client.getQueueLength("test_a", "consumerGroupD"));
        long l = testConsumer(times);
        Assert.assertEquals(times, l);
    }

    public static long testConsumer(long times) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("redis.host", "localhost");
        properties.setProperty("redis.port", String.valueOf(port));
        properties.setProperty("redis.consumer.maxwait", "1");
        properties.setProperty("redis.consumer.groupid", "consumerGroupC");
        properties.setProperty("redis.heart.beat", "2000");
        properties.setProperty("redis.topics", "test_b");

        DefaultMessageConsumer test_a = new DefaultMessageConsumer(properties);
        List<String> test_b = Arrays.asList("test_b");
        Map<String, RedisStream<String, String>> messageStreams = test_a.createMessageStreams(
                test_b,
                new StringRedisDeserializer(),
                new StringRedisDeserializer()
        );
        Iterator<KeyAndMessage<String, String>> iterator = messageStreams.get("test_b").iterator();
        long i = 0;
        for (long j = 0; j < times; j++) {
            KeyAndMessage<String, String> next = iterator.next();
            if (StringUtils.isNotBlank(next.key()) && StringUtils.isNotBlank(next.value())) {
                ++i;
            }
        }
        return i;
    }

    public static void testProducer(String topic, long send) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("redis.host", "localhost");
        properties.setProperty("redis.port", String.valueOf(port));
        properties.setProperty("redis.key.serializer", "com.oneapm.redismq.client.common.serialization.StringRedisSerializer");
        properties.setProperty("redis.value.serializer", "com.oneapm.redismq.client.common.serialization.StringRedisSerializer");
        properties.setProperty("redis.producer.batchSize", "100");
        properties.setProperty("redis.ping.timeout", "20000");
        properties.setProperty("redis.linger.ms", "2000");
        properties.setProperty("redis.topics", "test_b,test_a");
        properties.setProperty("redis.consumer.consumergroups", "test_b:consumerGroupA,consumerGroupB,consumerGroupC&test_a:consumerGroupD");

        final DefaultMessageSender<String, String> producer = new DefaultMessageSender<>(
                properties
        );

        /**
         *single thread for-loop is Ok
         */
        for (long i = 0; i < send; i++) {
            Record<String, String> stringStringRecordB = new Record<>(topic, "qwdasd", "cxzcxzs" + i);
            producer.send(stringStringRecordB);
        }
        producer.close();
    }
}
