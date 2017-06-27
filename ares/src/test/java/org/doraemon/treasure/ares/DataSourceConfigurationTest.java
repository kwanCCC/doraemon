package org.doraemon.treasure.ares;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class DataSourceConfigurationTest {

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Test
    public void testGetDataSources() throws FileNotFoundException {
        DataSourceConfiguration config = null;
        try {
            config = new DataSourceConfiguration("classpath:test.json");
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
        try {
            Map<Object, Object> map = config.getDataSources();
            Assert.assertNotNull(map);
            Assert.assertEquals(12, map.size());
            Assert.assertTrue(map.containsKey("default"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

    }

}
