package com.legobmw99.allomancy.util.proxy;

import com.legobmw99.allomancy.handlers.CommonEventHandler;
import com.legobmw99.allomancy.util.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public abstract class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        // Load most of the mod's content
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
        AllomancyConfig.initProps(e.getSuggestedConfigurationFile());
        configDirectory = e.getModConfigurationDirectory();
        Registry.registerPackets();
    }

    public void postInit(FMLPostInitializationEvent e) {
        AllomancyUtils.init();

    }

    public void serverInit(FMLServerStartingEvent e) {
        e.registerServerCommand(new PowerCommand());
    }

    public void init(FMLCommonSetupEvent  e) {
        //GameRegistry.registerWorldGenerator(new OreGenerator(), 0);
        AllomancyCapability.register();
    }

    public abstract World getClientWorld();
    public abstract PlayerEntity getClientPlayer();
}

