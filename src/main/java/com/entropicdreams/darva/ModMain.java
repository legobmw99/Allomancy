package com.entropicdreams.darva;

import org.lwjgl.input.Keyboard;

import com.entropicdreams.darva.common.ModRegistry;
import com.entropicdreams.darva.handlers.CraftingHandler;
import com.entropicdreams.darva.handlers.DamageHandler;
import com.entropicdreams.darva.handlers.OreGenerator;
import com.entropicdreams.darva.handlers.PlayerTracker;
import com.entropicdreams.darva.items.ItemGrinder;
import com.entropicdreams.darva.items.ItemMistcloak;
import com.entropicdreams.darva.items.ItemVial;
import com.entropicdreams.darva.util.AllomancyConfig;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import com.entropicdreams.darva.handlers.PacketHandler;

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
