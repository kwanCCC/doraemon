package com.oneapm.redismq.client.common.Util;

import org.junit.Test;

import java.util.Properties;

public class PropertiesConfigTest {

    static Properties properties    = new Properties();
    static long       ping_time_out = 2000;
    static String     localhost     = "localhost";
    static String     port          = "6378";
    static String     pwd           = "";

    static {
        properties.setProperty(PropertiesConfig.REDIS_HOST, localhost);
        properties.setProperty(PropertiesConfig.REDIS_PORT, port);
        properties.setProperty(PropertiesConfig.REDIS_PASSWORD, pwd);
        properties.setProperty(PropertiesConfig.PING_TIME_OUT_MS, String.valueOf(ping_time_out));
    }

    @Test
    public void metadataFetchTimeoutMs() throws Exception {
        long l = PropertiesConfig.metadataFetchTimeoutMs(properties);
        org.junit.Assert.assertTrue(ping_time_out == l);
    }

    @Test
    public void redisHost() throws Exception {
        String s = PropertiesConfig.redisHost(properties);
        org.junit.Assert.assertTrue(localhost.equals(s));
    }

    @Test
    public void redisPort() throws Exception {
        int i = PropertiesConfig.redisPort(properties);
        org.junit.Assert.assertTrue(Integer.parseInt(port) == i);
    }

    @Test
    public void redisPassword() throws Exception {
        String s = PropertiesConfig.redisPassword(properties);
        org.junit.Assert.assertTrue(pwd.equals(s));
    }

    @Test
    public void client() throws Exception {

    }

}