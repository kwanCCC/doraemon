package org.doraemon.treasure.gear;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.doraemon.treasure.gear.beans.ServiceProfile;
import org.doraemon.treasure.gear.exception.GearRuntimeException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@Slf4j
@Data
@Builder
public class GearConfig {
    private final String   base;
    private final ZKConfig zkConfig;
    private final boolean  localConfigEnable;
    private final String   appName;
    private final String   version;
    private final String   address;
    private final String   profile;
    private final int      port;
    private final Class payload = ServiceProfile.class;
    private final InstanceSerializer serializer;

    public static GearConfig load() {
        return load("gear.properties");
    }

    /**
     * load gear configuration
     *
     * @return
     */
    public static GearConfig load(String configFile) {
        String _configFile = configFile.trim();
        Properties props;
        try {
            props = PropertiesLoaderUtils.loadProperties(new ClassPathResource(_configFile));
        } catch (IOException e) {
            log.error("cannot load gear configurations, please make sure the "
                      + _configFile
                      + " exists in classpath", e);
            throw new GearRuntimeException(e);
        }
        String appName = System.getProperty("gear.app.name", props.getProperty("app.name"));
        String version = System.getProperty("gear.app.version", props.getProperty("app.version"));
        String zkHost = System.getProperty("gear.zk.host", props.getProperty("zk.host"));
        String profile = System.getProperty("gear.app.profile", props.getProperty("app.profile"));
        Integer connectionTimeOut = Integer.getInteger(
                "gear.zk.connect.timeout.ms",
                getInt(props, "zk.connect.timeout.ms", 10000)
        );
        Integer sessionTimeOut = Integer.getInteger(
                "gear.zk.session.timeout.ms",
                getInt(props, "zk.session.timeout.ms", 10000)
        );
        String zkRoot = System.getProperty("gear.zk.root", props.getProperty("zk.root", "gear"));
        String appAddress = System.getProperty("gear.app.address", props.getProperty("app.address"));
        Integer port = Integer.getInteger("gear.app.port", getInt(props, "app.port", 80));
        ZKConfig zk = ZKConfig.builder()
                              .connect(zkHost)
                              .connectTimeout(connectionTimeOut)
                              .sessionTimeout(sessionTimeOut)
                              .retry(4)
                              .build();
        return GearConfig.builder()
                         .base(zkRoot)
                         .appName(appName)
                         .version(version)
                         .address(appAddress)
                         .port(port)
                         .zkConfig(zk)
                         .profile(profile)
                         .build();
    }

    private static Boolean getBoolean(Properties props, String prop, boolean def) {
        String b = System.getProperty(prop);
        if (b != null && ("true".equalsIgnoreCase(b) || "false".equalsIgnoreCase(b))) {
            return "true".equalsIgnoreCase(b);
        }

        String v = props.getProperty(prop);
        if (v != null) {
            return "true".equalsIgnoreCase(v.trim());
        }
        return def;
    }

    private static Integer getInt(Properties props, String prop, Integer def) {
        String v = props.getProperty(prop);
        if (v != null) {
            try {
                return Integer.decode(v);
            } catch (NumberFormatException e) {
            }
        }
        return def;
    }
}
