package com.oneapm.redismq.client.producer;

import com.oneapm.redismq.client.common.CallBack;
import com.oneapm.redismq.client.common.ConsumerGroup;
import com.oneapm.redismq.client.common.Record;
import com.oneapm.redismq.client.common.RecordMetaData;
import com.oneapm.redismq.client.common.RedisThread;
import com.oneapm.redismq.client.common.Sender;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.Util.SystemTime;
import com.oneapm.redismq.client.common.Util.Time;
import com.oneapm.redismq.client.common.buffer.Accumulator;
import com.oneapm.redismq.client.common.client.ConsumerGrouplistener;
import com.oneapm.redismq.client.common.client.JedisClient;
import com.oneapm.redismq.client.common.serialization.RedisSerializer;
import com.oneapm.redismq.client.exception.SerializerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.oneapm.redismq.client.producer.ProducerConfigUtil.getConfiguredInstance;

public class DefaultMessageSender<K, V> implements MessageSender<K, V> {

    private Logger logger = LoggerFactory.getLogger(DefaultMessageSender.class);

    private final JedisClient           client;
    private final RedisSerializer<K>    keySerializer;
    private final RedisSerializer<V>    valueSerializer;
    private final long                  metadataFetchTimeoutMs;
    private final Time                  time;
    private final Accumulator           accumulator;
    private final Sender                sender;
    private final Thread                ioThread;
    private final ConsumerGrouplistener grouplistener;

    @SuppressWarnings("unchecked")
    public DefaultMessageSender(Properties prop) {
        this.client = ProducerConfigUtil.client(prop);
        this.keySerializer = ProducerConfigUtil.getConfiguredInstance(
                prop,
                ProducerConfigUtil.REDIS_KEY_SERIALIZER,
                RedisSerializer.class
        );
        this.valueSerializer = getConfiguredInstance(
                prop,
                ProducerConfigUtil.REDIS_VALUE_SERIALIZER,
                RedisSerializer.class
        );
        Map<TopicInfo, ConsumerGroup> tpcgp = ProducerConfigUtil.getTopicAndConsumerInfo(prop);
        this.metadataFetchTimeoutMs = ProducerConfigUtil.metadataFetchTimeoutMs(prop);
        this.time = new SystemTime();
        this.grouplistener = new ConsumerGrouplistener(client, metadataFetchTimeoutMs);
        this.grouplistener.producerStart(tpcgp);
        this.grouplistener.waitOnMetaData();
        this.accumulator = new Accumulator(
                ProducerConfigUtil.batchSize(prop),
                this.time,
                ProducerConfigUtil.lingerMs(prop)
        );
        this.sender = new Sender(this.accumulator, time, client, grouplistener);
        String clientSSID = ProducerConfigUtil.clientSSID(prop);
        String ioThreadName = "redis-producer-network-thread" + (clientSSID != null && clientSSID.length() > 0
                                                                 ? "|"
                                                                   + clientSSID
                                                                 : "");
        this.ioThread = new RedisThread(ioThreadName, sender, true);
        this.ioThread.start();
    }

    @Override
    public Future<RecordMetaData> send(Record<K, V> record) {
        return send(record, null);
    }

    @Override
    public Future<RecordMetaData> send(
            Record<K, V> record, CallBack callBack
    ) {
        try {
            byte[] keyByte;
            try {
                keyByte = this.keySerializer.redisSerialize(record.topic(), record.key());
            } catch (ClassCastException cce) {
                throw new SerializerException("Can't convert key of class "
                                              + record.key().getClass().getName()
                                              +
                                              " to class "
                                              + keySerializer.getClass().getName()
                                              +
                                              " specified in key.serializer");
            }
            byte[] valueByte;
            try {
                valueByte = this.valueSerializer.redisSerialize(record.topic(), record.value());
            } catch (ClassCastException cce) {
                throw new SerializerException("Can't convert value of class "
                                              + record.key().getClass().getName()
                                              +
                                              " to class "
                                              + valueSerializer.getClass().getName()
                                              +
                                              " specified in value.serializer");
            }
            Record<byte[], byte[]> byteRecord = new Record<>(record.topic(), keyByte, valueByte);
            Accumulator.AccumulatorResult result = accumulator.append(
                    new TopicInfo(record.topic()),
                    byteRecord,
                    callBack
            );
            if (result.batchIsFull || result.newBatchCreated) {
                this.sender.run(time.milliseconds());
            }
            return result.future;
        } catch (RuntimeException e) {
            if (callBack != null) {
                callBack.onCompletion(null, e);
            }
            return new FutureFailure(e);
        }
    }

    @Override
    public void close() {
        logger.warn("Closing the redis producer");
        this.sender.initiateClose();
        this.grouplistener.close();
        try {
            this.ioThread.join();
        } catch (InterruptedException e) {
            logger.error("close producer error");
            Thread.currentThread().interrupt();
        }
        this.keySerializer.closeSerialize();
        this.valueSerializer.closeSerialize();
        logger.info("The redis producer has closed");
    }

    private static class FutureFailure implements Future<RecordMetaData> {

        private final RuntimeException exception;

        public FutureFailure(RuntimeException exception) {
            this.exception = new RuntimeException(exception);
        }

        @Override
        public boolean cancel(boolean interrupt) {
            return false;
        }

        @Override
        public RecordMetaData get() throws RuntimeException {
            throw this.exception;
        }

        @Override
        public RecordMetaData get(long timeout, TimeUnit unit) throws RuntimeException {
            throw this.exception;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

    }

}
