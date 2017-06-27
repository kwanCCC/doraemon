package org.doraemon.treasure.ares.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "jakiro")
@Data
public class DataSourceConfiguration {
    private Map<String, DruidDSProperties> ds;
    private String                         name;
}
