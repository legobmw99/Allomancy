package common.legobmw99.allomancy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.handlers.AllomancyTickHandler;
import common.legobmw99.allomancy.proxy.CommonProxy;
import common.legobmw99.allomancy.util.AllomancyConfig;
import common.legobmw99.allomancy.util.ExternalPowerController;
import common.legobmw99.allomancy.world.OreGenerator;

@Mod(modid = Allomancy.MODID, version = Allomancy.VERSION)
public class Allomancy {
	public static final String MODID = "allomancy";
	public static final String VERSION = "@VERSION@";
	public static ExternalPowerController XPC;

	@Instance(value = "allomancy")
	public static Allomancy instance;

	@SidedProxy(clientSide = "common.legobmw99.allomancy.proxy.ClientProxy", serverSide = "common.legobmw99.allomancy.proxy.CommonProxy")
	public static CommonProxy proxy;


	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		AllomancyConfig.initProps(event.getSuggestedConfigurationFile());
		Registry.ModContent();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new AllomancyTickHandler());
		FMLCommonHandler.instance().bus().register(new AllomancyTickHandler());
		
		GameRegistry.registerWorldGenerator(new OreGenerator(), 0);
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
				new WeightedRandomChestContent(new ItemStack(
						Registry.nuggetLerasium), 1, 1, 40));
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(
				new WeightedRandomChestContent(new ItemStack(
						Registry.nuggetLerasium), 1, 1, 40));
		if(event.getSide() == Side.CLIENT)
    	{
			Registry.registerRenders();
			Allomancy.XPC = new ExternalPowerController();
			Registry.initKeyBindings();
    		} 
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
