package common.legobmw99.allomancy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import common.legobmw99.allomancy.common.AllomancyCapabilities;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.handlers.AllomancyEventHandler;
import common.legobmw99.allomancy.handlers.AllomancyTickHandler;
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
	
    @CapabilityInject(AllomancyCapabilities.class)
    public static final Capability<AllomancyCapabilities> PLAYER_CAP = null;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		//Load most of the mod's content
		AllomancyConfig.initProps(event.getSuggestedConfigurationFile());
		Registry.initItems();		
		Registry.initBlocks();
		Registry.setupRecipes();
		Registry.registerPackets();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new AllomancyEventHandler());
		
		//Register world gen
		GameRegistry.registerWorldGenerator(new OreGenerator(), 0);
		AllomancyCapabilities.register();
		/* Depreciated. TODO: figure out loot tables
		 * ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Registry.nuggetLerasium), 1, 1, 40));
		 * ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(new ItemStack(Registry.nuggetLerasium), 1, 1, 40));
		 */
		
		
		//Initialize client-only code like XPC and rendering code
		if(event.getSide() == Side.CLIENT)
    	{
			Registry.registerRenders();
			Allomancy.XPC = new ExternalPowerController();
			Registry.initKeyBindings();
    		}
		
		//Achievements must come after rendering, otherwise it will crash or not display properly
		Registry.addAchievements();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
