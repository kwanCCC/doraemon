package com.oneapm.redismq.client.producer;

import com.oneapm.redismq.client.common.TopicInfo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class ProducerRequestResult {
    private final    CountDownLatch latch      = new CountDownLatch(1);
    private volatile RuntimeException error;
    private volatile TopicInfo        topicInfo;

    public ProducerRequestResult() {
    }

    /**
     * Mark this request as complete and unblock any threads waiting on its completion.
     *
     * @param topicInfo topic infomation
     * @param error done with exception
     */
    public void done(TopicInfo topicInfo, RuntimeException error) {
        this.topicInfo = topicInfo;
        this.error = error;
        this.latch.countDown();
    }

    /**
     * Await the completion of this request
     *
     * @throws InterruptedException InterruptedException
     */
    public void await() throws InterruptedException {
        latch.await();
    }

    /**
     * Await the completion of this request (up to the given time interval)
     *
     * @param timeout The maximum time to wait
     * @param unit    The unit for the max time
     * @throws InterruptedException InterruptedException
     *
     * @return true if the request completed, false if we timed out
     */
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    /**
     * The error thrown while processing this request
     *
     * @return RuntimeException
     */
    public RuntimeException error() {
        return error;
    }

    /**
     * The topic infomation to which the record was appended
     * @return topicInfo
     */
    public TopicInfo topicPartition() {
        return topicInfo;
    }

    /**
     * Has the request completed?
     * @return complete request
     */
    public boolean completed() {
        return this.latch.getCount() == 0L;
    }

}
