package org.doraemon.treasure.gear;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZKConfig {

    private final int    sessionTimeout;
    private final int    connectTimeout;
    private final String connect;
}
