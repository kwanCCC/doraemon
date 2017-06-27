package com.oneapm.redismq.client.common.buffer;

import com.oneapm.redismq.client.common.CallBack;
import com.oneapm.redismq.client.common.Entry;
import com.oneapm.redismq.client.common.Record;
import com.oneapm.redismq.client.common.TopicInfo;
import com.oneapm.redismq.client.common.Util.SystemTime;
import com.oneapm.redismq.client.common.Util.Time;
import com.oneapm.redismq.client.producer.FutureRecordMetaData;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

public class RecordBatchTest {

    static String TEST = "test";

    static TopicInfo tp = new TopicInfo(TEST);

    static Time time = new SystemTime();

    static int capacity = 100;

    static RecordBatch batch = new RecordBatch(tp, time.milliseconds(), capacity);

    static CallBack callBackTopicInMetaDataNotNull = (metaData, exception) -> {
        Assert.assertTrue(Objects.nonNull(metaData));
    };

    static Record<byte[], byte[]> test = new Record<>("test", TEST.getBytes(), TEST.getBytes());

    @Test
    public void tryAppend() throws Exception {
        Entry entry = new Entry(test);
        FutureRecordMetaData futureRecordMetaData = batch.tryAppend(entry, callBackTopicInMetaDataNotNull);
        Assert.assertTrue(futureRecordMetaData != null);
    }

    @Test
    public void done() throws Exception {

    }

    @Test
    public void drain() throws Exception {

    }

    @Test
    public void isFull() throws Exception {

    }

}