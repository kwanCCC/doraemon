package com.oneapm.redismq.client.producer;


import com.oneapm.redismq.client.common.CallBack;
import com.oneapm.redismq.client.common.Record;
import com.oneapm.redismq.client.common.RecordMetaData;
import com.oneapm.redismq.client.common.Startable;
import com.oneapm.redismq.client.common.Stoppable;

import java.util.concurrent.Future;

public interface MessageSender<K, V> extends Startable, Stoppable {
    Future<RecordMetaData> send(Record<K, V> record);

    Future<RecordMetaData> send(Record<K, V> record, CallBack callBack);

    void close();
}
