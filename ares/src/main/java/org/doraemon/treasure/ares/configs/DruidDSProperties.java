package org.doraemon.treasure.ares.configs;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;

import java.sql.SQLException;

/**
 * Druid DataSource properties
 */
@Data
public class DruidDSProperties {
    private String url;
    private String username;
    private String password;
    private String  driverClassName                           = "com.mysql.jdbc.Driver";
    private int     initialSize                               = 5;
    private int     minIdle                                   = 5;
    private int     maxActive                                 = 50;
    private String  filters                                   = "stat,log4j";
    private String  connectionProperties                      = "druid.stat.slowSqlMillis=500;druid.stat.logSlowSql=true";
    private long    maxWait                                   = 5000L;
    private boolean defaultAutoCommit                         = true;
    private long    timeBetweenEvictionRunsMillis             = 300000L;
    private long    minEvictableIdleTimeMillis                = 300000L;
    private String  validationQuery                           = "SELECT 'x' FROM DUAL";
    private boolean testWhileIdle                             = true;
    private boolean testOnBorrow                              = false;
    private boolean testOnReturn                              = false;
    private boolean poolPreparedStatements                    = true;
    private int     maxPoolPreparedStatementPerConnectionSize = 20;
    private boolean removeAbandoned                           = true;
    private int     removeAbandonedTimeout                    = 1200;
    private boolean logAbandoned                              = true;

    DruidDataSource buildDataSource() {
        final DruidDataSource ds = new DruidDataSource();
        ds.setUrl(getUrl());
        ds.setUsername(getUsername());
        ds.setPassword(getPassword());
        ds.setDriverClassName(getDriverClassName());
        ds.setInitialSize(getInitialSize());
        ds.setMinIdle(getMinIdle());
        ds.setMaxActive(getMaxActive());
        try {
            ds.setFilters(getFilters());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        ds.setConnectionProperties(getConnectionProperties());
        ds.setMaxWait(getMaxWait());
        ds.setDefaultAutoCommit(isDefaultAutoCommit());
        ds.setTimeBetweenEvictionRunsMillis(getTimeBetweenEvictionRunsMillis());
        ds.setMinEvictableIdleTimeMillis(getMinEvictableIdleTimeMillis());
        ds.setValidationQuery(getValidationQuery());
        ds.setTestWhileIdle(isTestWhileIdle());
        ds.setTestOnBorrow(isTestOnBorrow());
        ds.setTestOnReturn(isTestOnReturn());
        ds.setPoolPreparedStatements(isPoolPreparedStatements());
        ds.setMaxPoolPreparedStatementPerConnectionSize(getMaxPoolPreparedStatementPerConnectionSize());
        ds.setRemoveAbandoned(isRemoveAbandoned());
        ds.setRemoveAbandonedTimeout(getRemoveAbandonedTimeout());
        ds.setLogAbandoned(isLogAbandoned());
        return ds;
    }
}
