package com.legobmw99.allomancy.util.proxy;

import com.legobmw99.allomancy.handlers.CommonEventHandler;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.Registry;
import com.legobmw99.allomancy.world.OreGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public abstract class CommonProxy {

    public void loadComplete(final FMLLoadCompleteEvent e) {
        // no-op
    }

    public abstract void clientInit( final FMLClientSetupEvent e);

    public abstract void serverInit(final FMLServerStartingEvent e);

    public void init(final FMLCommonSetupEvent e) {
        // Load most of the mod's content
        OreGenerator.generationSetup();
        AllomancyCapability.register();
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
        Registry.registerPackets();
    }

    public abstract World getClientWorld();
    public abstract PlayerEntity getClientPlayer();

}

