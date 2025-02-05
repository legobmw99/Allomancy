package com.legobmw99.allomancy.modules.powers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class PowersSetup {
    private PowersSetup() {}

    private static void init(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> NeoForge.EVENT_BUS.register(CommonEventHandler.class));
    }

    public static void register(IEventBus bus) {
        bus.addListener(PowersSetup::init);
    }
}
