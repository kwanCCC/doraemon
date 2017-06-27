package org.doraemon.treasure.ares;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * parse data source configurations, default returns DruidDataSource instances.
 *
 */
@Deprecated
public class DataSourceConfiguration {
    private URI configPath;

    public DataSourceConfiguration(String path) throws URISyntaxException, FileNotFoundException {
        if (path.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
            this.configPath = ResourceUtils.getURL(path).toURI();
        } else {
            this.configPath = new URI(path);
        }
    }

    public Map<Object, Object> getDataSources() throws IOException, SQLException {
        final Map<Object, Object> result = new HashMap<Object, Object>();
        final String json = IOUtils.toString(this.configPath);
        if (json == null || json.trim().equals("")) {
            return result;
        }
        final JSONObject config = JSON.parseObject(json);
        final Iterator<String> keys = config.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject object = config.getJSONObject(key);
            result.put(key, getDataSource(object));
        }
        return result;
    }

    private DataSource getDataSource(JSONObject config) throws SQLException {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(config.getString("url"));
        ds.setUsername(config.getString("username"));
        ds.setPassword(config.getString("password"));

        ds.setDriverClassName(config.getString("driverClassName") == null ? "com.mysql.jdbc.Driver" : config.getString("driverClassName"));
        ds.setInitialSize(getOrDefault(config, "initialSize", 5));
        ds.setMinIdle(getOrDefault(config, "minIdle", 5));
        ds.setMaxActive(getOrDefault(config, "maxActive", 50));
        ds.setFilters(getOrDefault(config, "filters", "stat,log4j"));
        ds.setConnectionProperties(getOrDefault(config, "connectionProperties", "druid.stat.slowSqlMillis=500;druid.stat.logSlowSql=true"));
        ds.setMaxWait(getOrDefault(config, "maxWait", 5000L));
        ds.setDefaultAutoCommit(getOrDefault(config, "defaultAutoCommit", true));

        ds.setTimeBetweenEvictionRunsMillis(getOrDefault(config, "timeBetweenEvictionRunsMillis", 300000L));
        ds.setMinEvictableIdleTimeMillis(getOrDefault(config, "minEvictableIdleTimeMillis", 300000L));

        ds.setValidationQuery(getOrDefault(config, "validationQuery", "SELECT 'x' FROM DUAL"));
        ds.setTestWhileIdle(getOrDefault(config, "testWhileIdle", true));
        ds.setTestOnBorrow(getOrDefault(config, "testOnBorrow", false));
        ds.setTestOnReturn(getOrDefault(config, "testOnReturn", false));

        ds.setPoolPreparedStatements(getOrDefault(config, "poolPreparedStatements", true));
        ds.setMaxPoolPreparedStatementPerConnectionSize(getOrDefault(config, "maxPoolPreparedStatementPerConnectionSize", 20));

        ds.setRemoveAbandoned(getOrDefault(config, "removeAbandoned", true));
        ds.setRemoveAbandonedTimeout(getOrDefault(config, "removeAbandonedTimeout", 1200));
        ds.setLogAbandoned(getOrDefault(config, "logAbandoned", true));
        return ds;
    }

    private static boolean getOrDefault(JSONObject config, String field, boolean defaults) {
        Boolean val = config.getBoolean(field);
        return val == null ? defaults : val;
    }

    private static long getOrDefault(JSONObject config, String field, long defaults) {
        long val = config.getIntValue(field);
        return val == 0 ? defaults : val;
    }

    private static int getOrDefault(JSONObject config, String field, int defaults) {
        int val = config.getIntValue(field);
        return val == 0 ? defaults : val;
    }

    private static String getOrDefault(JSONObject config, String field, String defaults) {
        String val = config.getString(field);
        return val == null ? defaults : val;
    }
}
