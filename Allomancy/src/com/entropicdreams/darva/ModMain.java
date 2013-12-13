package com.entropicdreams.darva;

import com.entropicdreams.darva.handlers.CraftingHandler;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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

	public static Item itemGrinder;
	public static Item itemTinIngot;
	public static Item itemTinFlakes;
	
	public static Block oreTin;
	
	public static CraftingHandler craftingHandler;
	
	
	@Instance(value = "allomancyMod")
	public static ModMain instance;
	
	@SidedProxy(clientSide="com.entropicdreams.darva.ClientProxy", serverSide="com.entropicdreams.darva.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {
		
		initItems();
		initBlocks();
		craftingHandler = new CraftingHandler();
		GameRegistry.registerCraftingHandler(craftingHandler);
		
	}
	
	@EventHandler 
    public void load(FMLInitializationEvent event) {
		setupItems();
		setupBlocks();
		setupRecipies();
	}
	 @EventHandler
	    public void postInit(FMLPostInitializationEvent event) {
		 
	 }
	 
	 
	 private void setupRecipies()
	 {
		 
		 GameRegistry.addSmelting(oreTin.blockID, new ItemStack(itemTinIngot,1), 5);
		 
		 GameRegistry.addShapelessRecipe(new ItemStack(itemTinFlakes,1), new ItemStack(itemTinIngot), new ItemStack(itemGrinder));
	 }
	 
	 private void initBlocks()
	 {
		 oreTin = new Block(601, Material.rock).setHardness(.5f).setStepSound(Block.soundStoneFootstep)
				 .setCreativeTab(CreativeTabs.tabBlock).setTextureName("allomancy:tinore");
	 }
	 private void setupBlocks()
	 {
		 GameRegistry.registerBlock(oreTin, "allomancy:oreTin");
		 LanguageRegistry.addName(oreTin, "Tin Ore");
	 }
	 
	 private void initItems()
	 {
		itemGrinder = new Item(500).setUnlocalizedName("allomancy:Grinder").setCreativeTab(CreativeTabs.tabMisc).setMaxDamage(32);
		itemTinIngot = new Item(501).setUnlocalizedName("allomancy:tiningot").setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		itemTinFlakes = new Item(502).setUnlocalizedName("allomancy:tinflakes").setCreativeTab(CreativeTabs.tabMaterials);
	 }
	 
	 private void setupItems()
	 {
		 GameRegistry.registerItem(itemGrinder, "allomancy:Grinder");
		 LanguageRegistry.addName(itemGrinder, "Hand Grinder");
		 itemGrinder.setTextureName("allomancy:handgrinder");
		 
		 GameRegistry.registerItem(itemTinIngot, "allomancy:tiningot");
		 LanguageRegistry.addName(itemTinIngot, "Tin Ingot");
		 itemTinIngot.setTextureName("allomancy:tiningot");
		 
		 GameRegistry.registerItem(itemTinFlakes, "allomancy:tinflakes");
		 LanguageRegistry.addName(itemTinFlakes, "Tin Flakes");
		 itemTinFlakes.setTextureName("allomancy:tinflakes");
	 }
	 
}
