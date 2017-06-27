package com.oneapm.redismq.client.common;

import java.nio.ByteBuffer;
import java.util.regex.Pattern;

import static com.oneapm.redismq.client.common.client.JedisClient.SEPARATOR;

public class Utils {

    /**
     * Get the absolute value of the given number. If the number is Int.MinValue return 0. This is different from
     * java.lang.Math.abs or scala.math.abs in that they return Int.MinValue (!).
     * @param n inout num
     *
     * @return absoutly num
     */
    public static int abs(int n) {
        return n & 0x7fffffff;
    }


    /**
     * Check that the parameter t is not null
     * @param <T> Generic
     * @param t The object to check
     *
     * @return t if it isn't null
     *
     * @throws NullPointerException if t is null.
     */
    public static <T> T notNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        } else {
            return t;
        }
    }

    /**
     * Get the length for UTF8-encoding a string without encoding it first
     *
     * @param s The string to calculate the length for
     *
     * @return The length when serialized
     */
    public static int utf8Length(CharSequence s) {
        int count = 0;
        for (int i = 0, len = s.length(); i < len; i++) {
            char ch = s.charAt(i);
            if (ch <= 0x7F) {
                count++;
            } else if (ch <= 0x7FF) {
                count += 2;
            } else if (Character.isHighSurrogate(ch)) {
                count += 4;
                ++i;
            } else {
                count += 3;
            }
        }
        return count;
    }

    /**
     * Read the given byte buffer into a byte array
     * @param buffer buffer contains byte array
     *
     * @return get byte array from buffer
     */
    public static byte[] toArray(ByteBuffer buffer) {
        return toArray(buffer, 0, buffer.limit());
    }

    /**
     * Read a byte array from the given offset and size in the buffer
     * @param buffer buffer contains byte array
     * @param offset index
     * @param size byte array size
     *
     * @return get byte array from buffer
     *
     */
    public static byte[] toArray(ByteBuffer buffer, int offset, int size) {
        byte[] dest = new byte[size];
        if (buffer.hasArray()) {
            System.arraycopy(buffer.array(), buffer.arrayOffset() + offset, dest, 0, size);
        } else {
            int pos = buffer.position();
            buffer.get(dest);
            buffer.position(pos);
        }
        return dest;
    }

    /**
     * Instantiate the class
     *
     * @param c class name
     * @return class type of instance
     */
    public static Object newInstance(Class<?> c) {
        try {
            return c.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not instantiate class " + c.getName(), e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not instantiate class "
                                       + c.getName()
                                       + " Does it have a public no-argument constructor?", e);
        }
    }

    /**
     * Generates 32 bit murmur2 hash from byte array
     *
     * @param data byte array to hash
     *
     * @return 32 bit hash of the given array
     */
    public static int murmur2(final byte[] data) {
        int length = data.length;
        int seed = 0x9747b28c;
        // 'm' and 'r' are mixing constants generated offline.
        // They're not really 'magic', they just happen to work well.
        final int m = 0x5bd1e995;
        final int r = 24;

        // Initialize the hash to a random value
        int h = seed ^ length;
        int length4 = length / 4;

        for (int i = 0; i < length4; i++) {
            final int i4 = i * 4;
            int k = (data[i4 + 0] & 0xff) + ((data[i4 + 1] & 0xff) << 8) + ((data[i4 + 2] & 0xff) << 16) + ((data[i4
                                                                                                                  + 3]
                                                                                                             & 0xff)
                                                                                                            << 24);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        // Handle the last few bytes of the input array
        switch (length % 4) {
            case 3:
                h ^= (data[(length & ~3) + 2] & 0xff) << 16;
            case 2:
                h ^= (data[(length & ~3) + 1] & 0xff) << 8;
            case 1:
                h ^= (data[length & ~3] & 0xff);
                h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

    /**
     * @param topicName     toicName
     * @param consumerGroup consumerGourp
     *
     * @return topicName.consumerGroup
     */
    public static byte[] mergeByteArray(String topicName, String consumerGroup) {
        byte[] topicBytes = topicName.getBytes();
        byte[] cgpBytes = consumerGroup.getBytes();
        byte[] realTopicName = new byte[topicBytes.length + cgpBytes.length + 1];
        System.arraycopy(topicBytes, 0, realTopicName, 0, topicBytes.length);
        System.arraycopy(SEPARATOR, 0, realTopicName, topicBytes.length, 1);
        System.arraycopy(cgpBytes, 0, realTopicName, topicBytes.length + 1, cgpBytes.length);
        return realTopicName;
    }

}
