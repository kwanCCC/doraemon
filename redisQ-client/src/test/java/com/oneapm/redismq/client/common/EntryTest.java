package com.oneapm.redismq.client.common;

import com.oneapm.redismq.client.common.serialization.StringRedisSerializer;
import org.junit.Assert;
import org.junit.Test;

public class EntryTest {

    @Test
    public void test_new_Entry(){
        String key = "keykey";
        String value = "valuevalue";
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        byte[] byteKey = stringRedisSerializer.redisSerialize("test_a", key);
        byte[] byteValue = stringRedisSerializer.redisSerialize("test_a", value);
        Record<byte[], byte[]> test_a = new Record<>("test_a", byteKey, byteValue);
        Entry entry = new Entry(test_a);
        Assert.assertTrue(key.equals(new String(entry.key())));
        Assert.assertTrue(value.equals(new String(entry.value())));
    }
}
