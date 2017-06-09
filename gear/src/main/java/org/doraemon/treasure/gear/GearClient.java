package org.doraemon.treasure.gear;

import org.doraemon.treasure.gear.api.Gear;

public class GearClient implements Gear {

    private final GearConfig gearConfig;

    public GearClient(GearConfig gearConfig) {
        this.gearConfig = gearConfig;
    }

    @Override
    public void start() {

    }
}
