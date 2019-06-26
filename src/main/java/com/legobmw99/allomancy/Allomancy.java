package com.legobmw99.allomancy;

import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.proxy.ClientProxy;
import com.legobmw99.allomancy.util.proxy.CommonProxy;
import com.legobmw99.allomancy.util.proxy.ServerProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(Allomancy.MODID)
public class Allomancy {
    public static final String MODID = "allomancy";

    public static File configDirectory;

    public static CommonProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
    ;

    public static final Logger LOGGER = LogManager.getLogger();

    public static Allomancy instance;



    public Allomancy() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modConfig);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AllomancyConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AllomancyConfig.CLIENT_SPEC);

    }

    public void clientInit(final FMLClientSetupEvent e) {
        proxy.clientInit(e);
    }

    public void serverInit(FMLServerStartingEvent e) {
        proxy.serverInit(e);
    }

    public void init(final FMLCommonSetupEvent e) {
        proxy.init(e);
    }

    public void loadComplete(final FMLLoadCompleteEvent e) {
        proxy.loadComplete(e);
    }

    public void modConfig(final ModConfig.ModConfigEvent e){
        ModConfig cfg = e.getConfig();
        if(cfg.getSpec() == AllomancyConfig.CLIENT_SPEC){
            AllomancyConfig.refreshClient();
        } else if (cfg.getSpec() == AllomancyConfig.COMMON_SPEC){
            AllomancyConfig.refreshCommon();
        }
    }


}
