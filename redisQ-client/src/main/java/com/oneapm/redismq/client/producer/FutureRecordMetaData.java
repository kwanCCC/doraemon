package com.oneapm.redismq.client.producer;

import com.oneapm.redismq.client.common.RecordMetaData;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ProducerRequestResult 包装了一个ProducerRequestResult, 且其中的CountDownLatch能支持CallBack同步结果完成整个的sender请求
 * 具体详见 RecordBatch.done(RuntimeException exception)
 */
public class FutureRecordMetaData implements Future<RecordMetaData> {

    private final ProducerRequestResult result;

    public FutureRecordMetaData(ProducerRequestResult result) {
        this.result = result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public RecordMetaData get() throws InterruptedException, ExecutionException {
        this.result.await();
        return valueOrError();
    }

    @Override
    public RecordMetaData get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        boolean await = this.result.await(timeout, unit);
        if (!await) {
            throw new TimeoutException("Timeout after waiting for "
                                       + TimeUnit.MILLISECONDS.convert(timeout, unit)
                                       + " ms.");
        }
        return valueOrError();
    }

    private RecordMetaData valueOrError() throws ExecutionException {
        if (this.result.error() != null) {
            throw new ExecutionException(this.result.error());
        } else {
            return new RecordMetaData(result.topicPartition());
        }
    }
}
