package com.oneapm.redismq.client.common;

import java.nio.ByteBuffer;

/**
 * key length | key | value
 * <p>
 * key index is 8
 * </p>
 */
public final class Entry {

    private final byte[] entry;

    public Entry(Record<byte[], byte[]> record) {
        byte[] key = record.key();
        byte[] value = record.value();
        int total = key.length + value.length;
        ByteBuffer allocate = ByteBuffer.allocate(total + 4);
        allocate.putInt(key.length)
                .put(key)
                .put(value);
        entry = allocate.array();
    }

    public Entry(byte[] enrty) {
        this.entry = enrty;
    }

    public byte[] key() {
        ByteBuffer wrap = ByteBuffer.wrap(entry).asReadOnlyBuffer();
        int key_length = wrap.getInt();
        byte[] keyByte = new byte[key_length];
        wrap.position(4).limit(4 + key_length);
        wrap.get(keyByte, 0, key_length);
        return keyByte;
    }

    public byte[] value() {
        ByteBuffer wrap = ByteBuffer.wrap(entry).asReadOnlyBuffer();
        int key_length = wrap.getInt();
        int value_index = key_length + 4;
        int capacity = wrap.capacity();
        int value_length = capacity - value_index;
        wrap.position(value_index);
        byte[] value = new byte[value_length];
        wrap.get(value, 0, value_length);
        return value;
    }

    public byte[] toBytes() {
        return this.entry;
    }
}
