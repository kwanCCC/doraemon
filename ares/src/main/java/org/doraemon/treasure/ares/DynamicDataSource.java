package org.doraemon.treasure.ares;

import org.doraemon.treasure.ares.datasource.DynamicCoreDataSource;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Map;

public class DynamicDataSource extends LazyConnectionDataSourceProxy {

    protected DynamicCoreDataSource core = new DynamicCoreDataSource();
    private String                  configPath;

    /**
     * @see java.net.URI
     * @param value
     */
    @Deprecated
    public void setConfigPath(String value) {
        this.configPath = value;
    }

    public void setDynamicDataSourceName(String name) {
        core.setDynamicCoreDataSourceName(name);
    }

    public void setDataSourceClassName(String name) {
        core.setDataSourceClassName(name);
    }

    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        core.setTargetDataSources(targetDataSources);
    }

    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        core.setDefaultTargetDataSource(defaultTargetDataSource);
    }

    @Override
    public void afterPropertiesSet() {
        // parse configurations
        if (this.configPath != null && !this.configPath.trim().equals("")) {
            Map<Object, Object> map = null;
            try {
                final DataSourceConfiguration config = new DataSourceConfiguration(this.configPath);
                map = config.getDataSources();
            } catch (IOException e) {
                throw new RuntimeException("cannot load config file: " + this.configPath, e);
            } catch (SQLException e) {
                throw new RuntimeException("create data source failed: " + e.getMessage(), e);
            } catch (URISyntaxException e) {
                throw new RuntimeException("cannot load config file: " + this.configPath, e);
            }
            if (map.containsKey("default")) {
                this.setDefaultTargetDataSource(map.get("default"));
                map.remove("default");
            }
            this.setTargetDataSources(map);
        }
        core.afterPropertiesSet();
        super.setTargetDataSource(core);
        super.afterPropertiesSet();
    }

    public Map<Object, Object> allDataSources() {
        return core.getTmp_targetDataSources();
    }

    @Override
    @Deprecated
    public void setTargetDataSource(DataSource targetDataSource) {}

    public void close() {
        core.close();
    }
}
