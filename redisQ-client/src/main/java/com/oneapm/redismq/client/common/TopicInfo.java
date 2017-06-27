package com.oneapm.redismq.client.common;

public final class TopicInfo {
    private final String topic;

    public TopicInfo(String topic) {
        this.topic = topic;
    }

    public String topic() {
        return topic;
    }

    @Override
    public int hashCode() {
        return topic.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TopicInfo other = (TopicInfo) obj;
        if (topic == null) {
            if (other.topic != null) {
                return false;
            }
        } else if (!topic.equals(other.topic)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopicInfo{" +
               "topic='" + topic + '\'' +
               '}';
    }
}
