package com.oneapm.redismq.client.common.buffer;

import com.oneapm.redismq.client.common.CallBack;
import com.oneapm.redismq.client.common.Entry;
import com.oneapm.redismq.client.common.Record;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.Util.SystemTime;
import com.oneapm.redismq.client.common.Util.Time;
import com.oneapm.redismq.client.producer.FutureRecordMetaData;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AccumulatorTest {

    static int    batchSize = 100;
    static Time   time      = new SystemTime();
    static long   lingMs    = 5;
    static String TEST      = "test";

    static Accumulator accumulator = new Accumulator(batchSize, time, lingMs);
    static TopicInfo   tp          = new TopicInfo(TEST);

    static CallBack callBackTopicInMetaDataNotNull = (metaData, exception) -> {
        Assert.assertTrue(Objects.nonNull(metaData));
    };

    private Accumulator.AccumulatorResult append_and_doNotingInCallBack() {
        return accumulator.append(
                tp,
                new Record<>("test", TEST.getBytes(), TEST.getBytes()),
                callBackTopicInMetaDataNotNull
        );
    }

    @Test
    public void append() throws Exception {
        Accumulator.AccumulatorResult result = append_and_doNotingInCallBack();
        Assert.assertTrue(!result.batchIsFull);
        Assert.assertTrue(!result.newBatchCreated);
    }

    @Test
    public void drain() throws Exception {
        List<RecordBatch> drain = accumulator.drain(time.milliseconds(), new TopicInfo(TEST));
        Assert.assertTrue(drain != null);
    }

    @Test
    public void reenqueue() throws Exception {
        TopicInfo topicInfo = new TopicInfo(TEST);
        long longMS = time.milliseconds();
        RecordBatch recordBatch = new RecordBatch(topicInfo, longMS, batchSize);
        Record<byte[], byte[]> test = new Record<>("test", TEST.getBytes(), TEST.getBytes());
        recordBatch.tryAppend(
                new Entry(test),
                callBackTopicInMetaDataNotNull
        );
        accumulator.reenqueue(recordBatch, longMS);
        Assert.assertTrue(accumulator.hasUnsent());
    }

    @Test
    public void hasUnsent() throws Exception {
        append_and_doNotingInCallBack();
        Assert.assertTrue(accumulator.hasUnsent());
    }

    @Test
    public void ready() throws Exception {
        try {
            while (CollectionUtils.isNotEmpty(accumulator.ready(time.milliseconds()))) {
                append_and_doNotingInCallBack();
            }
        } catch (Throwable tx) {
            Assert.fail(tx.getLocalizedMessage());
        } finally {
            return;
        }
    }
}