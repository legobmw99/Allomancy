package com.legobmw99.allomancy;

import com.legobmw99.allomancy.commands.AllomancyPowerCommand;
import com.legobmw99.allomancy.commands.AllomancyPowerType;
import com.legobmw99.allomancy.handlers.ClientEventHandler;
import com.legobmw99.allomancy.handlers.CommonEventHandler;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.Registry;
import com.legobmw99.allomancy.world.OreGenerator;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Allomancy.MODID)
public class Allomancy {
    public static final String MODID = "allomancy";

    public static final Logger LOGGER = LogManager.getLogger();

    public static Allomancy instance;


    public Allomancy() {
        instance = this;
        // Register our setup events on the necessary buses
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modConfig);
        MinecraftForge.EVENT_BUS.addListener(this::serverInit);

        //Config init
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AllomancyConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AllomancyConfig.CLIENT_SPEC);

    }

    public void clientInit(final FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        Registry.initKeyBindings();
        Registry.registerEntityRenders();

    }

    public void serverInit(final FMLServerStartingEvent e) {
        AllomancyPowerCommand.register(e.getCommandDispatcher());
    }

    public void init(final FMLCommonSetupEvent e) {
        //Register our ArgumentType so it can be sent over network
        ArgumentTypes.register("allomancy_power", AllomancyPowerType.class, new ArgumentSerializer<>(AllomancyPowerType::powerType));
        OreGenerator.generationSetup();
        AllomancyCapability.register();
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
        Registry.registerPackets();
    }

    public void modConfig(final ModConfig.ModConfigEvent e) {
        ModConfig cfg = e.getConfig();
        if (cfg.getSpec() == AllomancyConfig.CLIENT_SPEC) {
            AllomancyConfig.refreshClient();
        } else if (cfg.getSpec() == AllomancyConfig.COMMON_SPEC) {
            AllomancyConfig.refreshCommon();
        }
    }
}
