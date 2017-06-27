package org.doraemon.treasure.ares.shared;

import org.doraemon.treasure.ares.mybatis.TableNameHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MetricStorePyramidTableNameStrategyTest
{

  private MetricStorePyramidTableNameStrategy strategy;

  @Test
  public void test_successfully() throws Exception
  {
    Map<String, Object> params = new HashMap<>();
    params.put("startTime", System.currentTimeMillis());
    params.put("endTime", System.currentTimeMillis() + 20L * 60000L);
    String targetTableName = strategy.getTargetTableName(TableNameHandler.SQL_TYPE_SELECT, "", params, "");
    Assert.assertEquals("test2_table", targetTableName);
  }

  @Test
  public void test_successfully_use_last_span_time() throws Exception
  {
    Map<String, Object> params = new HashMap<>();
    params.put("startTime", System.currentTimeMillis());
    params.put("endTime", System.currentTimeMillis() + 31L * 60000L);
    String targetTableName = strategy.getTargetTableName(TableNameHandler.SQL_TYPE_SELECT, "", params, "");
    Assert.assertEquals("test3_table", targetTableName);
  }

  @Test
  public void test_successfully_use_first_span_time() throws Exception
  {
    Map<String, Object> params = new HashMap<>();
    params.put("startTime", System.currentTimeMillis());
    params.put("endTime", System.currentTimeMillis() + 60000L);
    String targetTableName = strategy.getTargetTableName(TableNameHandler.SQL_TYPE_SELECT, "", params, "");
    Assert.assertEquals("test1_table", targetTableName);
  }

  @Before
  public void setUp() throws Exception
  {
    Map<String, String> spanTime = new HashMap<>();
    spanTime.put("PT10M", "test1_table");
    spanTime.put("PT30M", "test2_table");
    spanTime.put("DEFAULT", "test3_table");
    strategy = new MetricStorePyramidTableNameStrategy(spanTime, "startTime", "endTime");
  }
}
