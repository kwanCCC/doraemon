package org.doraemon.treasure.gear.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

public class SpringConfigurable {

    private final SpringContextPropertiesInjector springContextPropertiesInjector;

    private static final Logger logger = LoggerFactory.getLogger(SpringConfigurable.class);

    public SpringConfigurable(SpringContextPropertiesInjector springContextPropertiesInjector) {
        this.springContextPropertiesInjector = springContextPropertiesInjector;
    }

    public void config() {
        Properties props = new Properties();
        try {
            props.load(new ByteArrayInputStream(config.getContent().getBytes("UTF-8")));
        } catch (IOException e) {
            logger.error("cannot parse configuration: " + e, e);
        }
        springContextPropertiesInjector.inject("gear-props", props);

        // TODO: test this
        // context.refresh();
    }

    public boolean isReloadable() {
        return true;
    }
}
