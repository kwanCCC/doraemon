package org.doraemon.treasure.gear;

import lombok.Builder;
import lombok.Data;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.doraemon.treasure.gear.beans.ServiceProfile;

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
}
