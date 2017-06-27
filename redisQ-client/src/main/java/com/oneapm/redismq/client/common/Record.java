package com.oneapm.redismq.client.common;

public final class Record<K, V> {
    private final String topic;
    private final K      key;
    private final V      value;

    /**
     * create a record to be sent to a specified topic
     *
     * @param topic topic name
     * @param key key
     * @param value message
     */
    public Record(String topic, K key, V value) {
        if (topic == null) {
            throw new IllegalArgumentException("topic cannot by null");
        }
        this.topic = topic;
        this.key = key;
        this.value = value;
    }

    /**
     * create record withOut key
     *
     * @param topic topic name
     * @param value message
     */
    public Record(String topic, V value) {
        this(topic, null, value);
    }

    /**
     * The topic this record is being sent to
     *
     * @return topic name
     */
    public String topic() {
        return topic;
    }

    /**
     * The key (or null if no key is specified)
     *
     * @return K key
     */
    public K key() {
        return key;
    }

    /**
     * @return The value
     */
    public V value() {
        return value;
    }

    @Override
    public String toString() {
        return "Record{" +
               "topic='" + topic + '\'' +
               ", key=" + key +
               ", value=" + value +
               '}';
    }
}
