package com.oneapm.redismq.client.common;

public final class RecordMetaData {
    private final TopicInfo topicInfo;

    public RecordMetaData(TopicInfo topicInfo) {
        super();
        this.topicInfo = topicInfo;
    }

    /**
     * The topic the record was appended to
     * @return topic infomation metaData
     */
    public String topic() {
        return this.topicInfo.topic();
    }
}
