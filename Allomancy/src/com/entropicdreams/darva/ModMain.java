package com.entropicdreams.darva;



import org.lwjgl.input.Keyboard;

import com.entropicdreams.darva.handlers.CraftingHandler;
import com.entropicdreams.darva.handlers.OreGenerator;
import com.entropicdreams.darva.handlers.PlayerTracker;
import com.entropicdreams.darva.items.ItemGrinder;
import com.entropicdreams.darva.items.ItemVial;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import com.entropicdreams.darva.handlers.PacketHandler;
import com.entropicdreams.darva.handlers.keyhandlers.BurnFirstKeyBind;
import com.entropicdreams.darva.handlers.keyhandlers.BurnSecondKeyBind;
import com.entropicdreams.darva.handlers.keyhandlers.SwitchMetalKeybind;
 
@NetworkMod(clientSideRequired=true, channels={"Allomancy_Data"}, packetHandler = PacketHandler.class)
@Mod(modid ="allomancyMod", name = "Allomancy", version = "0.0.1" )
public class ModMain {

	public static ItemGrinder itemGrinder;
	public static Item itemTinIngot;
	public static Item itemTinFlakes;
	public static Item itemLeadIngot;
	public static Item itemLeadFlakes;
	public static Item itemCopperIngot;
	public static Item itemCopperFlakes;
	public static Item itemZincIngot;
	public static Item itemZincFlakes;
	
	public static Item itemBrassFlakes;
	public static Item itemSteelFlakes;
	public static Item itemPewterFlakes;
	public static Item itemIronFlakes;
	public static Item itemBronzeFlakes;
	
	
	public static Block oreTin;
	public static Block oreLead;
	public static Block oreCopper;
	public static Block oreZinc;
	
	public static ItemVial itemVial;
	
	public static CraftingHandler craftingHandler;
	
	//public static OreGenerator oreGenerator;
	
	
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
		
		//oreGenerator = new OreGenerator();
		GameRegistry.registerWorldGenerator(new OreGenerator());
		
		setupKeybinds();
	}
	
	@EventHandler 
    public void load(FMLInitializationEvent event) {
		setupItems();
		setupBlocks();
		setupRecipies();
		
		
		
	}
	 @EventHandler
	    public void postInit(FMLPostInitializationEvent event) {
		proxy.RegisterTickHandlers();
		MinecraftForge.EVENT_BUS.register(new PlayerTracker());
		
	 }
	 
	 private void setupKeybinds()
	 {
			KeyBinding[] key = {new KeyBinding("Select Metal", Keyboard.KEY_TAB)};
	        boolean[] repeat = {false};
	        KeyBindingRegistry.registerKeyBinding(new SwitchMetalKeybind(key, repeat));		 
	        
			KeyBinding[] key2 = {new KeyBinding("Burn First", Keyboard.KEY_F)};
	        KeyBindingRegistry.registerKeyBinding(new BurnFirstKeyBind(key2, repeat));
	        KeyBinding[] key3 = {new KeyBinding("Burn Second", Keyboard.KEY_G)};
	        KeyBindingRegistry.registerKeyBinding(new BurnSecondKeyBind(key3, repeat));
	 }
	 
	 
	 private void setupRecipies()
	 {
		 
		 GameRegistry.addSmelting(oreTin.blockID, new ItemStack(itemTinIngot,1), 5);
		 GameRegistry.addShapelessRecipe(new ItemStack(itemTinFlakes,1), new ItemStack(itemTinIngot), new ItemStack(itemGrinder,1,OreDictionary.WILDCARD_VALUE));			 
		 GameRegistry.addShapelessRecipe(new ItemStack(itemLeadFlakes,1), new ItemStack(itemLeadIngot), new ItemStack(itemGrinder,1,OreDictionary.WILDCARD_VALUE));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemCopperFlakes,1), new ItemStack(itemCopperIngot), new ItemStack(itemGrinder,1,OreDictionary.WILDCARD_VALUE));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemZincFlakes,1), new ItemStack(itemZincIngot), new ItemStack(itemGrinder,1,OreDictionary.WILDCARD_VALUE));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemIronFlakes,1), new ItemStack(Item.ingotIron), new ItemStack(itemGrinder,1,OreDictionary.WILDCARD_VALUE));
		 
		 GameRegistry.addShapelessRecipe(new ItemStack(itemSteelFlakes,1), new ItemStack(itemIronFlakes), new ItemStack(Item.coal));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemBrassFlakes,1), new ItemStack(itemZincFlakes), new ItemStack(itemCopperFlakes));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemPewterFlakes,1), new ItemStack(itemPewterFlakes), new ItemStack(itemLeadFlakes));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemBronzeFlakes,1), new ItemStack(itemCopperFlakes), new ItemStack(itemTinFlakes));

		 GameRegistry.addShapelessRecipe(new ItemStack(itemVial,1,1),new ItemStack(itemCopperFlakes), new ItemStack(itemVial,1,0), new ItemStack(Item.bucketWater));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemVial,1,2),new ItemStack(itemIronFlakes), new ItemStack(itemVial,1,0), new ItemStack(Item.bucketWater));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemVial,1,3),new ItemStack(itemTinFlakes), new ItemStack(itemVial,1,0), new ItemStack(Item.bucketWater));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemVial,1,4),new ItemStack(itemZincFlakes), new ItemStack(itemVial,1,0), new ItemStack(Item.bucketWater));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemVial,1,5),new ItemStack(itemBrassFlakes), new ItemStack(itemVial,1,0), new ItemStack(Item.bucketWater));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemVial,1,6),new ItemStack(itemSteelFlakes), new ItemStack(itemVial,1,0), new ItemStack(Item.bucketWater));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemVial,1,7),new ItemStack(itemPewterFlakes), new ItemStack(itemVial,1,0), new ItemStack(Item.bucketWater));
		 GameRegistry.addShapelessRecipe(new ItemStack(itemVial,1,8),new ItemStack(itemBronzeFlakes), new ItemStack(itemVial,1,0), new ItemStack(Item.bucketWater));

		 
		 
		 GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemVial,1,0), " x ", "y y", " y ", 'x', "slabWood", 'y', Block.glass));
		 
	 }
	 
	 private void initBlocks()
	 {
		 oreTin = new Block(240, Material.rock).setHardness(.5f).setStepSound(Block.soundStoneFootstep)
				 .setCreativeTab(CreativeTabs.tabBlock).setTextureName("allomancy:tinore").setUnlocalizedName("allomancy:tinore");
		 oreLead = new Block(241, Material.rock).setHardness(.5f).setStepSound(Block.soundStoneFootstep)
				 .setCreativeTab(CreativeTabs.tabBlock).setTextureName("allomancy:leadore").setUnlocalizedName("allomancy:leadore");

		 oreCopper = new Block(242, Material.rock).setHardness(.5f).setStepSound(Block.soundStoneFootstep)
				 .setCreativeTab(CreativeTabs.tabBlock).setTextureName("allomancy:copperore").setUnlocalizedName("allomancy:copperore")				 ;

		 oreZinc = new Block(243, Material.rock).setHardness(.5f).setStepSound(Block.soundStoneFootstep)
				 .setCreativeTab(CreativeTabs.tabBlock).setTextureName("allomancy:zincore").setUnlocalizedName("allomancy:zincore")				 ;

	 }
	 private void setupBlocks()
	 {
		 GameRegistry.registerBlock(oreTin, "allomancy:tinore");
		 LanguageRegistry.addName(oreTin, "Tin Ore");
		 MinecraftForge.setBlockHarvestLevel(oreTin, "pick", 1);

		 GameRegistry.registerBlock(oreLead, "allomancy:leadore");
		 LanguageRegistry.addName(oreLead, "Lead Ore");
		 MinecraftForge.setBlockHarvestLevel(oreLead, "pick", 1);

		 GameRegistry.registerBlock(oreCopper, "allomancy:copperore");
		 LanguageRegistry.addName(oreCopper, "Copper Ore");
		 MinecraftForge.setBlockHarvestLevel(oreCopper, "pick", 1);

		 GameRegistry.registerBlock(oreZinc, "allomancy:zincore");
		 LanguageRegistry.addName(oreZinc, "Zinc Ore");
		 MinecraftForge.setBlockHarvestLevel(oreZinc, "pick", 1);


	 }
	 
	 private void initItems()
	 {
		itemGrinder = new ItemGrinder(500);
		
		itemTinIngot = new Item(801).setUnlocalizedName("allomancy:tiningot").setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		itemTinFlakes = new Item(802).setUnlocalizedName("allomancy:tinflakes").setCreativeTab(CreativeTabs.tabMaterials);
		itemLeadIngot = new Item(803).setUnlocalizedName("allomancy:leadingot").setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		itemLeadFlakes = new Item(804).setUnlocalizedName("allomancy:leadflakes").setCreativeTab(CreativeTabs.tabMaterials);
		itemCopperIngot = new Item(805).setUnlocalizedName("allomancy:copperingot").setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		itemCopperFlakes = new Item(806).setUnlocalizedName("allomancy:copperflakes").setCreativeTab(CreativeTabs.tabMaterials);
		itemZincIngot = new Item(807).setUnlocalizedName("allomancy:zincingot").setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		itemZincFlakes = new Item(808).setUnlocalizedName("allomancy:zincflakes").setCreativeTab(CreativeTabs.tabMaterials);

		itemIronFlakes = new Item(809).setUnlocalizedName("allomancy:ironflakes").setCreativeTab(CreativeTabs.tabMaterials);
		itemSteelFlakes = new Item(810).setUnlocalizedName("allomancy:steelflakes").setCreativeTab(CreativeTabs.tabMaterials);
		itemBrassFlakes = new Item(811).setUnlocalizedName("allomancy:brassflakes").setCreativeTab(CreativeTabs.tabMaterials);
		itemPewterFlakes = new Item(812).setUnlocalizedName("allomancy:pewterflakes").setCreativeTab(CreativeTabs.tabMaterials);
		itemBronzeFlakes = new Item(813).setUnlocalizedName("allomancy:bronzeflakes").setCreativeTab(CreativeTabs.tabMaterials);
		
		itemVial = new ItemVial(814);
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

		 GameRegistry.registerItem(itemLeadIngot, "allomancy:leadingot");
		 LanguageRegistry.addName(itemLeadIngot, "Lead Ingot");
		 itemLeadIngot.setTextureName("allomancy:leadingot");
		 
		 GameRegistry.registerItem(itemLeadFlakes, "allomancy:leadflakes");
		 LanguageRegistry.addName(itemLeadFlakes, "Lead Flakes");
		 itemLeadFlakes.setTextureName("allomancy:leadflakes");

		 GameRegistry.registerItem(itemCopperIngot, "allomancy:copperingot");
		 LanguageRegistry.addName(itemCopperIngot, "Copper Ingot");
		 itemCopperIngot.setTextureName("allomancy:copperingot");
		 
		 GameRegistry.registerItem(itemCopperFlakes, "allomancy:copperflakes");
		 LanguageRegistry.addName(itemCopperFlakes, "Copper Flakes");
		 itemCopperFlakes.setTextureName("allomancy:copperflakes");
		 
		 GameRegistry.registerItem(itemZincIngot, "allomancy:zincingot");
		 LanguageRegistry.addName(itemZincIngot, "Zinc Ingot");
		 itemZincIngot.setTextureName("allomancy:zincingot");
		 
		 GameRegistry.registerItem(itemZincFlakes, "allomancy:zincflakes");
		 LanguageRegistry.addName(itemZincFlakes, "Zinc Flakes");
		 itemZincFlakes.setTextureName("allomancy:zincflakes");

		 GameRegistry.registerItem(itemIronFlakes, "allomancy:ironflakes");
		 LanguageRegistry.addName(itemIronFlakes, "Iron Flakes");
		 itemIronFlakes.setTextureName("allomancy:ironflakes");

		 GameRegistry.registerItem(itemSteelFlakes, "allomancy:steelflakes");
		 LanguageRegistry.addName(itemSteelFlakes, "Steel Flakes");
		 itemSteelFlakes.setTextureName("allomancy:steelflakes");

		 GameRegistry.registerItem(itemBrassFlakes, "allomancy:brassflakes");
		 LanguageRegistry.addName(itemBrassFlakes, "Brass Flakes");
		 itemBrassFlakes.setTextureName("allomancy:brassflakes");

		 GameRegistry.registerItem(itemPewterFlakes, "allomancy:pewterflakes");
		 LanguageRegistry.addName(itemPewterFlakes, "Pewter Flakes");
		 itemPewterFlakes.setTextureName("allomancy:pewterflakes");

		 GameRegistry.registerItem(itemBronzeFlakes, "allomancy:bronzeflakes");
		 LanguageRegistry.addName(itemBronzeFlakes, "Bronze Flakes");
		 itemBronzeFlakes.setTextureName("allomancy:bronzeflakes");

		 ItemStack item;
		 
			for(int i = 0; i < ItemVial.localName.length; i++) {
					ItemStack Item;
					Item = new ItemStack(itemVial,1,i);
					LanguageRegistry.addName(Item, ItemVial.localName[i]);
				}
	 }
	 
}
