package com.oneapm.redismq.client.common;

import com.oneapm.redismq.client.common.Util.Time;
import com.oneapm.redismq.client.common.buffer.Accumulator;
import com.oneapm.redismq.client.common.buffer.RecordBatch;
import com.oneapm.redismq.client.common.client.ConsumerGrouplistener;
import com.oneapm.redismq.client.common.client.JedisClient;
import com.oneapm.redismq.client.exception.NullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * sender as a future Buffer Drainer
 */
public class Sender implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Sender.class);

    /* the record accumulator that batches records */
    private final Accumulator accumulator;

    /* true while the sender thread is still running */
    private volatile boolean running;

    /* the clock instance used for getting the time */
    private final Time time;

    private final JedisClient client;

    private final ConsumerGrouplistener listener;

    public Sender(Accumulator accumulator, Time time, JedisClient client, ConsumerGrouplistener listener) {
        this.accumulator = accumulator;
        this.time = time;
        this.client = client;
        this.running = true;
        this.listener = listener;
    }

    @Override
    public void run() {
        log.debug("Starting Redis producer I/O thread.");
        while (running) {
            try {
                run(time.milliseconds());
            } catch (Exception e) {
                log.error("Uncaught error in kafka producer I/O thread: ", e);
            }
        }

        log.debug("Beginning shutdown of Kafka producer I/O thread, sending remaining records.");

        while (this.accumulator.hasUnsent() && this.client.ping()) {
            try {
                run(time.milliseconds());
            } catch (Exception e) {
                log.error("Uncaught error in Redis producer I/O thread: ", e);
            }
        }

        this.client.clientShutdown();

        log.debug("Shutdown of Redis producer I/O thread has completed.");
    }

    public void run(long now) {
        Set<TopicInfo> ready = this.accumulator.ready(now);
        RuntimeException e = new NullException();
        for (TopicInfo tp : ready) {
            List<RecordBatch> drain = this.accumulator.drain(now, tp);
            Set<byte[]> topicCgpInfomation = listener.getTopicCgpInfomation(tp);
            for (RecordBatch recordBatch : drain) {
                try {
                    client.pipeline(topicCgpInfomation, recordBatch);
                } catch (RuntimeException ex) {
                    e = ex;
                    log.error("send to topic.consumergroup ", e);
                } finally {
                    recordBatch.done(e);
                }
            }
        }
    }

    /**
     * Start closing the sender (won't actually complete until all data is sent out)
     */
    public void initiateClose() {
        this.running = false;
        this.accumulator.close();
    }
}
