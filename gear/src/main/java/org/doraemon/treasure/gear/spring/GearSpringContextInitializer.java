package org.doraemon.treasure.gear.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class GearSpringContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public void initialize(ConfigurableApplicationContext context) {
        String mode = System.getProperty("gear.client.mode", "none");
        if (!"none".equals(mode)) {
            log.info("Gear SpringContextInitializer starting...");
            new SpringGearClient(new SpringContextPropertiesInjector(context)).start();
            log.info("Gear SpringContextInitializer started.");
        }
    }
}
