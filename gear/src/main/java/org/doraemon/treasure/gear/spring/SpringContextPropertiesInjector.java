package org.doraemon.treasure.gear.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

@Slf4j
public class SpringContextPropertiesInjector {

    private final ConfigurableApplicationContext context;

    public SpringContextPropertiesInjector(ConfigurableApplicationContext context) {
        this.context = context;
    }

    public void inject(String name, Properties props) {
        final String appMode = props.getProperty("app.mode", "product");

        PropertiesPropertySource pps = new PropertiesPropertySource(name, props);
        context.getEnvironment().getPropertySources().addFirst(pps);

        if (appMode != null) {
            context.getEnvironment().setActiveProfiles(appMode.split(","));
            if (!"product".equalsIgnoreCase(appMode)) {
                log.info("application configurations: ");
                for (Object key : props.keySet()) {
                    log.info("{}\t\t\t:\t{}", key, props.get(key));
                }
            }
        }
    }
}
