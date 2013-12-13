package com.entropicdreams.darva;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


@Mod(modid ="allomancyMod", name = "Allomancy", version = "0.0.1" )
public class ModMain {

	private Item itemGrinder;
	
	
	@Instance(value = "allomancyMod")
	public static ModMain instance;
	
	@SidedProxy(clientSide="com.entropicdreams.darva.ClientProxy", serverSide="com.entropicdreams.darva.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {
		
		initItems();
	}
	
	@EventHandler 
    public void load(FMLInitializationEvent event) {
		setupItems();
	}
	 @EventHandler
	    public void postInit(FMLPostInitializationEvent event) {
		 
	 }
	 
	 
	 private void initItems()
	 {
		itemGrinder = new Item(500).setUnlocalizedName("allomancy:Grinder").setCreativeTab(CreativeTabs.tabMisc).setMaxDamage(32);
	 }
	 
	 private void setupItems()
	 {
		 GameRegistry.registerItem(itemGrinder, "allomancy:Grinder");
		 LanguageRegistry.addName(itemGrinder, "Hand Grinder");
		 itemGrinder.setTextureName("allomancy:handgrinder");
	 }
	 
}
