package com.legobmw99.allomancy;

import com.legobmw99.allomancy.handlers.AllomancyEventHandler;
import com.legobmw99.allomancy.util.AllomancyCapabilities;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.PowerCommand;
import com.legobmw99.allomancy.util.Registry;
import com.legobmw99.allomancy.world.OreGenerator;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Allomancy.MODID, version = Allomancy.VERSION)
public class Allomancy {
    public static final String MODID = "allomancy";
    public static final String VERSION = "@VERSION@";

    @Instance(value = "allomancy")
    public static Allomancy instance;

    @CapabilityInject(AllomancyCapabilities.class)
    public static final Capability<AllomancyCapabilities> PLAYER_CAP = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	
        // Load most of the mod's content
        AllomancyConfig.initProps(event.getSuggestedConfigurationFile());
        Registry.initItems();
        Registry.initBlocks();
        Registry.setupRecipes();
        Registry.registerPackets();
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        // register server commands
        event.registerServerCommand(new PowerCommand());
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new AllomancyEventHandler(event));

        // Register world gen
        GameRegistry.registerWorldGenerator(new OreGenerator(), 0);
        AllomancyCapabilities.register();

        // Initialize client-only code like keys and rendering code
        if (event.getSide() == Side.CLIENT) {
            Registry.registerRenders();
            Registry.initKeyBindings();
        }

        // Achievements must come after rendering, otherwise it will crash or not display properly
        Registry.addAdvancement();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
