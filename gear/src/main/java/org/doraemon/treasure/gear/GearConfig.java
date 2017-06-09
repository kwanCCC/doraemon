package org.doraemon.treasure.gear;

import lombok.Builder;
import lombok.Data;

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
}
