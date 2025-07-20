package com.legobmw99.allomancy.modules.powers;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class PowersSetup {
    public static void init(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            MinecraftForge.EVENT_BUS.register(CommonEventHandler.class);
        });
    }

}
