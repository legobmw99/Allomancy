package com.legobmw99.allomancy.util.proxy;

import com.legobmw99.allomancy.handlers.CommonEventHandler;
import com.legobmw99.allomancy.util.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
        //GameRegistry.registerWorldGenerator(new OreGenerator(), 0);
        AllomancyCapability.register();
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
        Registry.registerPackets();
    }

    //todo javadoc
    public abstract World getClientWorld();
    public abstract PlayerEntity getClientPlayer();
    public abstract void sendToServer(Object msg);
    public abstract void sendTo(Object msg, ServerPlayerEntity player);

}

