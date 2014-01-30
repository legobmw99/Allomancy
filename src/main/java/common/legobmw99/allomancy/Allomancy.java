package common.legobmw99.allomancy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.proxy.CommonProxy;
import common.legobmw99.allomancy.util.AllomancyConfig;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Allomancy.MODID, version = Allomancy.VERSION)
public class Allomancy
{
    public static final String MODID = "allomancy";
    public static final String VERSION = "2.0";
    
	@Instance(value = "allomancy")
	public static Allomancy instance;
	
	@SidedProxy(clientSide = "common.legobmw99.allomancy.proxy.ClientProxy", serverSide = "common.legobmw99.allomancy.proxy.CommonProxy")
	public static CommonProxy proxy;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		AllomancyConfig.initProps(event.getSuggestedConfigurationFile());
		Registry.ModContent();
    }
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(Registry.nuggetLerasium),1,1,40));
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(new ItemStack(Registry.nuggetLerasium),1,1,40));	
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.init();
    }
}
