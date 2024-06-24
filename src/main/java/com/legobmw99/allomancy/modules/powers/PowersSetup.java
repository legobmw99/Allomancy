package com.legobmw99.allomancy.modules.powers;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class PowersSetup {
    private PowersSetup() {}

    public static void init(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> NeoForge.EVENT_BUS.register(CommonEventHandler.class));
    }

}
