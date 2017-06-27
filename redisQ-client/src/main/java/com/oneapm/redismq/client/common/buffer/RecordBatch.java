package com.oneapm.redismq.client.common.buffer;

import com.google.common.collect.Lists;
import com.oneapm.redismq.client.common.CallBack;
import com.oneapm.redismq.client.common.Entry;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.producer.FutureRecordMetaData;
import com.oneapm.redismq.client.producer.ProducerRequestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Batch sender in pipeline
 */
public final class RecordBatch {
    private static final Logger log = LoggerFactory.getLogger(RecordBatch.class);

    public volatile int attempts = 0;

    public        long                  drainedMs;
    public        long                  lastAttemptMs;
    public final  long                  createdMs;
    public final  TopicInfo             topicInfo;
    private final ProducerRequestResult future;
    private final List<Handle>          handles;
    private final int                   capacity;
    private final Deque<Entry>          toBeSend;

    public RecordBatch(TopicInfo tp, long now, int capacity) {
        this.createdMs = now;
        this.lastAttemptMs = now;
        this.topicInfo = tp;
        this.future = new ProducerRequestResult();
        this.capacity = capacity;
        this.handles = new ArrayList<>();
        this.toBeSend = new ArrayDeque<>(capacity);
    }

    /**
     * try append message at the end of queue
     *
     * @param entry key and message
     * @param callBack call back function
     *
     * @return metaData in future
     */
    public FutureRecordMetaData tryAppend(Entry entry, CallBack callBack) {
        synchronized (toBeSend) {
            if (toBeSend.size() > capacity) {
                return null;
            }
        }
        toBeSend.addLast(entry);
        if (callBack != null) {
            handles.add(new Handle(callBack, new FutureRecordMetaData(future)));
        }
        return new FutureRecordMetaData(future);
    }

    /**
     * Complete this request
     *
     * @param exception exeception occur
     */
    public void done(RuntimeException exception) {
        this.future.done(topicInfo, exception);
        for (int i = 0; i < this.handles.size(); i++) {
            try {
                Handle handle = this.handles.get(i);
                if (exception == null) {
                    handle.callback.onCompletion(handle.future.get(), null);
                } else {
                    handle.callback.onCompletion(null, exception);
                }
            } catch (Exception e) {
                log.error(
                        "Error executing user-provided callback on message for topic {}:",
                        topicInfo.toString(),
                        e
                );
            }
        }
    }

    public List<Entry> drain() {
        synchronized (toBeSend) {
            List<Entry> tmp = Lists.newArrayListWithCapacity(toBeSend.size());
            while (toBeSend.peekFirst() != null) {
                tmp.add(toBeSend.pollFirst());
            }
            return tmp;
        }
    }

    public boolean isFull() {
        return !(toBeSend.size() < capacity);
    }

    /**
     * A callback and the associated FutureRecordMetadata argument to pass to it.
     */
    final private static class Handle {
        final CallBack             callback;
        final FutureRecordMetaData future;

        public Handle(CallBack callback, FutureRecordMetaData future) {
            this.callback = callback;
            this.future = future;
        }
    }
}
