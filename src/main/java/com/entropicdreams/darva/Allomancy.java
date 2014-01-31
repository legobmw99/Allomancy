package com.entropicdreams.darva;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;

import com.entropicdreams.darva.common.Registry;
import com.entropicdreams.darva.handlers.CraftingHandler;
import com.entropicdreams.darva.handlers.DamageHandler;
import com.entropicdreams.darva.handlers.PacketHandler;
import com.entropicdreams.darva.handlers.PlayerTracker;
import com.entropicdreams.darva.proxy.CommonProxy;
import com.entropicdreams.darva.util.AllomancyConfig;
import com.entropicdreams.darva.world.OreGenerator;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@NetworkMod(clientSideRequired = true, channels = { "Allomancy_Data" }, packetHandler = PacketHandler.class)
@Mod(modid = "allomancy", name = "Allomancy", version = "1.1.5")
public class Allomancy {

	public static CraftingHandler craftingHandler;
	public static DamageHandler damageHandler;
    public static PlayerTracker playerTracker;
	public static MetalParticleController MPC;

	@Instance(value = "allomancy")
	public static Allomancy instance;

	@SidedProxy(clientSide = "com.entropicdreams.darva.proxy.ClientProxy", serverSide = "com.entropicdreams.darva.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		AllomancyConfig.initProps(event.getSuggestedConfigurationFile());
		Registry.ModContent();
		craftingHandler = new CraftingHandler();
		GameRegistry.registerCraftingHandler(craftingHandler);
		GameRegistry.registerWorldGenerator(new OreGenerator());
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		playerTracker = new PlayerTracker();
        GameRegistry.registerPlayerTracker(playerTracker);
        MinecraftForge.EVENT_BUS.register(playerTracker);
		damageHandler = new DamageHandler();
		MinecraftForge.EVENT_BUS.register(damageHandler);
		
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Registry.nuggetLerasium),1,1,40));
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(new ItemStack(Registry.nuggetLerasium),1,1,40));
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.RegisterTickHandlers();
	}
}
