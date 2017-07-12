package org.doraemon.treasure.ares.configs;

import org.doraemon.treasure.ares.DynamicDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "jakiro", name = "name")
@ConditionalOnMissingBean(value = DataSource.class)
@EnableConfigurationProperties(DataSourceConfiguration.class)
public class JakiroAutoConfiguration {

    @Bean(name = "dataSource", destroyMethod = "close")
    @ConditionalOnProperty(prefix = "jakiro", name = "name")
    @ConditionalOnMissingBean(value = DataSource.class)
    public DataSource getDataSource(DataSourceConfiguration configuration) {

        Map<Object, Object> dataSources = new HashMap<>();
        configuration.getDs().forEach((key, propes) -> dataSources.put(key, propes.buildDataSource()));

        final DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setDynamicDataSourceName(configuration.getName());
        if (dataSources.containsKey("default")) {
            dataSource.setDefaultTargetDataSource(dataSources.get("default"));
            dataSources.remove("default");
        }
        dataSource.setTargetDataSources(dataSources);
        return dataSource;
    }
}
