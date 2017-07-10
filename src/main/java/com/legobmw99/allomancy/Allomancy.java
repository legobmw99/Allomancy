package com.legobmw99.allomancy;

import com.legobmw99.allomancy.handlers.ClientEventHandler;
import com.legobmw99.allomancy.handlers.CommonEventHandler;
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
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Allomancy.MODID, version = Allomancy.VERSION)
public class Allomancy {
	public static final String MODID = "allomancy";
	public static final String VERSION = "@VERSION@";

	@SidedProxy
	public static CommonProxy proxy;

	@Instance(value = "allomancy")
	public static Allomancy instance;

	@CapabilityInject(AllomancyCapabilities.class)
	public static final Capability<AllomancyCapabilities> PLAYER_CAP = null;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void serverInit(FMLServerStartingEvent event) {
		proxy.serverInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	public static class CommonProxy {
		public void preInit(FMLPreInitializationEvent e) {
			// Load most of the mod's content
			MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
			AllomancyConfig.initProps(e.getSuggestedConfigurationFile());
			Registry.registerPackets();
		}

		public void serverInit(FMLServerStartingEvent e) {
			e.registerServerCommand(new PowerCommand());
		}

		public void init(FMLInitializationEvent e) {
			GameRegistry.registerWorldGenerator(new OreGenerator(), 0);
			Registry.setupRecipes();
			AllomancyCapabilities.register();
		}
	}

	public static class ClientProxy extends CommonProxy {
		@Override
		public void init(FMLInitializationEvent e) {
			super.init(e);
			MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
			Registry.registerRenders();
			Registry.initKeyBindings();
		}
	}

	public static class ServerProxy extends CommonProxy {

	}
}
