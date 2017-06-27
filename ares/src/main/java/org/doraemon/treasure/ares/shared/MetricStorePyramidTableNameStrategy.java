package org.doraemon.treasure.ares.shared;

import org.doraemon.treasure.ares.mybatis.TableNameHandler;
import org.joda.time.Period;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * 提供MetricStore的金字塔路由功能
 *
 */
public class MetricStorePyramidTableNameStrategy implements TableNameHandler
{
  private final String startTimeField;
  private final String endTimeField;

  private final String defaultTableName;
  private final Map<Long, String> targetTableNames = new TreeMap<>(Comparator.<Long>naturalOrder());


  /**
   * @param targetTableNames 时间区间与表之间的映射关系
   * @param startTimeField
   * @param endTimeField
   */
  public MetricStorePyramidTableNameStrategy(
      Map<String, String> targetTableNames, String startTimeField, String endTimeField
  )
  {
    this.startTimeField = startTimeField;
    this.endTimeField = endTimeField;

    String defaultTargetNameKey = null;
    for (Map.Entry<String, String> targetTableNameEntry : targetTableNames.entrySet()) {
      String duration = targetTableNameEntry.getKey();
      if (!duration.equalsIgnoreCase("default")) {
        String targetTableName = targetTableNameEntry.getValue();
        Long spanTime = Period.parse(duration).toStandardDuration().getMillis();
        this.targetTableNames.put(spanTime, targetTableName);
      } else {
        defaultTargetNameKey = targetTableNameEntry.getKey();
      }
    }

    require(defaultTargetNameKey != null, "TargetTableNames中缺少Default选项");
    defaultTableName = targetTableNames.get(defaultTargetNameKey);
  }

  @Override
  public String getTargetTableName(String sqlType, String tableName, Object params, String mapperId)
  {
    if (TableNameHandler.SQL_TYPE_SELECT.equals(sqlType)) {
      require(params instanceof Map, "无法识别的参数类型");
      return processSelectTableName((Map) params);
    } else {
      return tableName;
    }
  }

  private String processSelectTableName(Map params)
  {
    require(params.containsKey(startTimeField), "无法获取查询的StartTime,请在参数中传入:" + startTimeField);
    require(params.containsKey(endTimeField), "无法获取查询的EndTime,请在参数中传入:" + endTimeField);

    Long spanTime = ((Number) params.get(endTimeField)).longValue() - ((Number) params.get(startTimeField)).longValue();

    for (Map.Entry<Long, String> targetTableNameEntry : targetTableNames.entrySet()) {
      if (spanTime <= targetTableNameEntry.getKey()) {
        return targetTableNameEntry.getValue();
      }
    }
    return defaultTableName;
  }

  private void require(Boolean require, String errorMsg)
  {
    if (!require) {
      throw new IllegalArgumentException(errorMsg);
    }
  }
}
