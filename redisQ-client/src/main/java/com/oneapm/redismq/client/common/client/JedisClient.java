package com.oneapm.redismq.client.common.client;

import com.oneapm.redismq.client.common.ConsumerGroup;
import com.oneapm.redismq.client.common.Utils;
import com.oneapm.redismq.client.common.buffer.RecordBatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
public final class JedisClient {

    private final JedisPool pool;
    private final       String REDIS_PONG = "PONG";
    public static final String HASH       = "hash";
    public static final String LIST       = "list";
    public static final String SET        = "set";
    public static final byte[] SEPARATOR  = {46};


    public JedisClient(String address, int port, String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        if (StringUtils.isNotBlank(password)) {
            pool = new JedisPool(config, address, port, Protocol.DEFAULT_TIMEOUT, password);
        } else {
            pool = new JedisPool(config, address, port);
        }
    }

    public boolean ping() {
        Jedis connection = null;
        try {
            connection = pool.getResource();
            String ping = connection.ping();
            return REDIS_PONG.equalsIgnoreCase(ping);
        } finally {
            close(connection);
        }
    }

    public boolean existsRealTopicName(byte[] realTopicName) {
        Jedis connection = null;
        try {
            connection = pool.getResource();
            return LIST.equals(connection.type(realTopicName));
        } finally {
            close(connection);
        }
    }

    /**
     * Status code reply, specifically: "none" if the key does not exist "string" if the key
     * contains a String value "list" if the key contains a List value "set" if the key
     * contains a Set value "zset" if the key contains a Sorted Set value "hash" if the key
     * contains a Hash value
     *
     * @param topic topicName
     *
     * @return exist or not
     *
     * */
    public boolean exitsTopic(String topic) {
        Jedis connection = null;
        try {
            connection = pool.getResource();
            return SET.equals(connection.type(topic));
        } finally {
            close(connection);
        }
    }

    public long consumerGroupNum(String topic) {
        Jedis connection = null;
        try {
            connection = pool.getResource();
            return connection.hlen(topic);
        } finally {
            close(connection);
        }
    }

    public void destroy(String topic, String consumerGroup) {
        Jedis connection = null;
        try {
            connection = pool.getResource();
            connection.hdel(topic, consumerGroup);
        } finally {
            close(connection);
        }
    }

    /**
     * 多开短连接，提高发送性能
     *
     * @param realTopicNames topicName.consumerGourp
     * @param batches local message to send in a batch mode
     */
    public void pipeline(Set<byte[]> realTopicNames, List<RecordBatch> batches) {
        RuntimeException exception = null;
        for (RecordBatch batch : batches) {
            Jedis connection = pool.getResource();
            Pipeline pipelined = connection.pipelined();
            try {
                byte[][] bytes = batch.drain().stream().map(item -> item.toBytes()).toArray(size -> new byte[size][]);
                log.debug("batch size : {}", bytes.length);
                for (byte[] realTopicName : realTopicNames) {
                    pipelined.lpush(realTopicName, bytes);
                }
            } catch (RuntimeException e) {
                log.error("send to topic.consumergroup ", e);
                exception = e;
            } finally {
                batch.done(exception);
                close(pipelined);
                close(connection);
            }
        }
    }

    /**
     * 多开短连接，提高发送性能
     *
     * @param realTopicNames topicName.consumerGourp
     * @param batch local message to send in a batch mode
     */
    public void pipeline(Set<byte[]> realTopicNames, RecordBatch batch) throws RuntimeException {
        Jedis connection = pool.getResource();
        Pipeline pipelined = connection.pipelined();
        try {
            byte[][] bytes = batch.drain().stream().map(item -> item.toBytes()).toArray(size -> new byte[size][]);
            log.debug("batch size : {}", bytes.length);
            for (byte[] realTopicName : realTopicNames) {
                pipelined.lpush(realTopicName, bytes);
            }
        } finally {
            close(pipelined);
            close(connection);
        }

    }

    public byte[] consumerFromTopics(byte[] topic) {
        Jedis connection = null;
        try {
            connection = pool.getResource();
            return connection.rpop(topic);
        } finally {
            close(connection);
        }
    }

    private void close(Pipeline pipeline) {
        try {
            pipeline.sync();
            pipeline.clear();
            pipeline.close();
        } catch (IOException e) {
            log.error("close pipeline fail", e);
        }
    }

    private void close(Jedis connection) {
        if (connection != null) {
            connection.close();
        }
    }

    public void clientShutdown() {
        if (!pool.isClosed()) {
            pool.destroy();
        }
    }

    /**
     * @param topic topic name
     * @param consumerGroup consumerGourp
     *
     */
    public void addConsumerGroupOfTopic(String topic, String... consumerGroup) {
        Jedis connection = null;
        try {
            connection = pool.getResource();
            connection.sadd(topic, consumerGroup);
        } finally {
            close(connection);
        }
    }

    public ConsumerGroup sycnConsumerGroup(String topic) {
        Jedis connection = null;
        try {
            connection = pool.getResource();
            Set<String> consumerGroup = connection.smembers(topic);
            return new ConsumerGroup(consumerGroup);
        } finally {
            close(connection);
        }
    }

    public long getQueueLength(String topic, String consumerGroup) {
        Jedis connection = null;
        try {
            connection = pool.getResource();
            byte[] bytes = Utils.mergeByteArray(topic, consumerGroup);
            return connection.llen(bytes);
        } finally {
            close(connection);
        }
    }
}
