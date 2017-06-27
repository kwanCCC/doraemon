package com.oneapm.redismq.client.common.buffer;

import com.oneapm.redismq.client.common.CallBack;
import com.oneapm.redismq.client.common.Entry;
import com.oneapm.redismq.client.common.Record;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.Util.Time;
import com.oneapm.redismq.client.common.Utils;
import com.oneapm.redismq.client.producer.FutureRecordMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Accumulate the key and message into a locally buffer
 */
public class Accumulator {
    private static final Logger log = LoggerFactory.getLogger(Accumulator.class);

    private volatile boolean                                      closed;
    private          int                                          drainIndex;
    private final    int                                          batchSize;
    private final    Time                                         time;
    private final    ConcurrentMap<TopicInfo, Deque<RecordBatch>> batches;
    private final    long                                         lingerMs;


    public Accumulator(
            int batchSize,
            Time time,
            long lingMs
    ) {
        this.drainIndex = 0;
        this.closed = false;
        this.batchSize = batchSize;
        this.time = time;
        this.batches = new CopyOnWriteMap<>();
        this.lingerMs = lingMs;
    }

    public AccumulatorResult append(TopicInfo topic, Record<byte[], byte[]> byteRecord, CallBack callBack) {
        if (closed) {
            throw new IllegalStateException("Cannot send after the producer is closed.");
        }
        // check if we have an in-progress batch
        Deque<RecordBatch> dq = dequeFor(topic);
        synchronized (dq) {
            RecordBatch last = dq.peekLast();
            // run in the time
            if (last != null) {
                FutureRecordMetaData futureRecordMetaData = last.tryAppend(new Entry(byteRecord), callBack);
                if (futureRecordMetaData != null) {
                    return new AccumulatorResult(futureRecordMetaData, dq.size() > 1 || last.isFull(), false);
                }
            }
            //try again
            dq.addLast(new RecordBatch(topic, time.milliseconds(), batchSize));
            last = dq.peekLast();
            FutureRecordMetaData future = Utils.notNull(last.tryAppend(new Entry(byteRecord), callBack));
            return new AccumulatorResult(future, dq.size() > 1 || last.isFull(), dq.size() != 1);
        }
    }

    /**
     * drain spec topic data
     *
     * @param now Time.millseconds
     * @param tp  topicInfo
     *
     * @return all Record for one topic
     */
    public List<RecordBatch> drain(long now, TopicInfo tp) {
        Deque<RecordBatch> deque = dequeFor(tp);
        List<RecordBatch> readyToFire = new ArrayList<>();
        synchronized (deque) {
            if (deque.peekLast() != null) {
                RecordBatch recordBatch = deque.pollFirst();
                readyToFire.add(recordBatch);
                recordBatch.drainedMs = now;
            }
        }
        return readyToFire;
    }


    /**
     * Re-enqueue the given record batch in the accumulator to retry
     * @param batch batch re queue
     * @param now time.millseconds
     */
    public void reenqueue(RecordBatch batch, long now) {
        batch.attempts++;
        batch.lastAttemptMs = now;
        Deque<RecordBatch> deque = dequeFor(batch.topicInfo);
        synchronized (deque) {
            deque.addFirst(batch);
        }
    }

    public boolean hasUnsent() {
        for (Map.Entry<TopicInfo, Deque<RecordBatch>> entry : this.batches.entrySet()) {
            Deque<RecordBatch> deque = entry.getValue();
            synchronized (deque) {
                if (deque.size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<TopicInfo> ready(long nowMs) {
        Set<TopicInfo> readyTopics = new HashSet<>();
        for (Map.Entry<TopicInfo, Deque<RecordBatch>> entry : this.batches.entrySet()) {
            TopicInfo part = entry.getKey();
            Deque<RecordBatch> deque = entry.getValue();
            synchronized (deque) {
                RecordBatch first = deque.peekFirst();
                if (first != null) {
                    long waitedTimeMs = nowMs - first.lastAttemptMs;
                    boolean full = deque.size() > 1 || first.isFull();
                    boolean expired = waitedTimeMs > lingerMs;
                    boolean sendAble = full || expired || closed;
                    if (sendAble) {
                        log.debug("full ? " + full + "expired ? " + expired + "closed ? " + closed);
                        readyTopics.add(part);
                    }
                }
            }
        }
        return readyTopics;
    }


    /**
     * Get the deque for the given topic-partition, creating it if necessary. Since new topics will only be added rarely
     * we copy-on-write the hashmap
     *
     * @param tp topicInfo
     *
     * @return deque of TopicInfo
     */
    private Deque<RecordBatch> dequeFor(TopicInfo tp) {
        Deque<RecordBatch> d = this.batches.get(tp);
        if (d != null) {
            return d;
        }
        this.batches.putIfAbsent(tp, new ArrayDeque<>());
        return this.batches.get(tp);
    }

    /**
     * Close this Accumulator and force all the record buffers to be drained
     */
    public void close() {
        this.closed = true;
    }

    public static class AccumulatorResult {
        public final FutureRecordMetaData future;
        public final boolean              batchIsFull;
        public final boolean              newBatchCreated;

        public AccumulatorResult(FutureRecordMetaData future, boolean batchIsFull, boolean newBatchCreated) {
            this.future = future;
            this.batchIsFull = batchIsFull;
            this.newBatchCreated = newBatchCreated;
        }
    }
}
