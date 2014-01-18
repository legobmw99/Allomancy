package com.entropicdreams.darva;

import net.minecraftforge.common.MinecraftForge;

import com.entropicdreams.darva.common.ModRegistry;
import com.entropicdreams.darva.handlers.CraftingHandler;
import com.entropicdreams.darva.handlers.DamageHandler;
import com.entropicdreams.darva.handlers.OreGenerator;
import com.entropicdreams.darva.handlers.PacketHandler;
import com.entropicdreams.darva.handlers.PlayerTracker;
import com.entropicdreams.darva.util.AllomancyConfig;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@NetworkMod(clientSideRequired = true, channels = { "Allomancy_Data" }, packetHandler = PacketHandler.class)
@Mod(modid = "allomancy", name = "Allomancy", version = "@VERSION@")
public class ModMain {

	public static CraftingHandler craftingHandler;
	public static MetalParticleController MPC;

	@Instance(value = "allomancy")
	public static ModMain instance;

	@SidedProxy(clientSide = "com.entropicdreams.darva.ClientProxy", serverSide = "com.entropicdreams.darva.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		AllomancyConfig.initProps(event.getSuggestedConfigurationFile());
		ModRegistry.ModContent();
		craftingHandler = new CraftingHandler();
		GameRegistry.registerCraftingHandler(craftingHandler);
		GameRegistry.registerWorldGenerator(new OreGenerator());
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerTracker());
		MinecraftForge.EVENT_BUS.register(new DamageHandler());
		EntityRegistry.registerModEntity(FlyingItem.class, "Flying Item", 400,
				this.instance, 120, 3, true);

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.RegisterTickHandlers();
	}
}
