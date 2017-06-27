package com.oneapm.redismq.client.common;

/**
 * A callback interface that the user can implement to allow code to execute when the request is complete.
 */
public interface CallBack {

    void onCompletion(RecordMetaData metaData, Exception exception);
}
