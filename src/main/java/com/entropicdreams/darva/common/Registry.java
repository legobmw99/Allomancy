package com.entropicdreams.darva.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.entropicdreams.darva.items.ItemCoinBag;
import com.entropicdreams.darva.items.ItemGrinder;
import com.entropicdreams.darva.items.ItemMistcloak;
import com.entropicdreams.darva.items.ItemVial;
import com.entropicdreams.darva.items.NuggetLerasium;
import com.entropicdreams.darva.util.AllomancyConfig;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Registry {

	public static void ModContent() {
		initItems();
		initBlocks();
		setupItems();
		setupBlocks();
		setupRecipies();
		setupKeybinds();
		oreRegistration();
		addAchievements();

	}

	private static void addAchievements() {
		becomeMistborn = new Achievement(450, "becomeMistborn", -5, -2,
				Registry.Mistcloak, null).setIndependent().setSpecial()
				.registerAchievement();
		LanguageRegistry.instance().addStringLocalization(
				"achievement.becomeMistborn", "en_US", "Become Mistborn!");
		LanguageRegistry.instance().addStringLocalization(
				"achievement.becomeMistborn.desc", "en_US",
				"You have a power most people envy...");
	}

	public static void oreRegistration() {
		OreDictionary.registerOre("ingotCopper", new ItemStack(itemCopperIngot));
		OreDictionary.registerOre("ingotZinc", new ItemStack(itemZincIngot));
		OreDictionary.registerOre("ingotTin", new ItemStack(itemTinIngot));
		OreDictionary.registerOre("ingotLead", new ItemStack(itemLeadIngot));
		OreDictionary.registerOre("oreCopper", new ItemStack(oreCopper));
		OreDictionary.registerOre("oreZinc", new ItemStack(oreZinc));
		OreDictionary.registerOre("oreTin", new ItemStack(oreTin));
		OreDictionary.registerOre("oreLead", new ItemStack(oreLead));
		OreDictionary.registerOre("glass", new ItemStack(Block.glass));

	}

	public static void setupRecipies() {

		GameRegistry.addSmelting(oreTin.blockID,
				new ItemStack(itemTinIngot, 1), 5);
		GameRegistry.addSmelting(oreCopper.blockID, new ItemStack(
				itemCopperIngot, 1), 5);
		GameRegistry.addSmelting(oreLead.blockID, new ItemStack(itemLeadIngot,
				1), 5);
		GameRegistry.addSmelting(oreZinc.blockID, new ItemStack(itemZincIngot,
				1), 5);

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				itemTinFlakes, 2), new Object[] {
				"ingotTin",
				new ItemStack(itemAllomancyGrinder, 1,
						OreDictionary.WILDCARD_VALUE) }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				itemLeadFlakes, 2), new Object[] {
				"ingotLead",
				new ItemStack(itemAllomancyGrinder, 1,
						OreDictionary.WILDCARD_VALUE) }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				itemZincFlakes, 2), new Object[] {
				"ingotZinc",
				new ItemStack(itemAllomancyGrinder, 1,
						OreDictionary.WILDCARD_VALUE) }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				itemCopperFlakes, 2), new Object[] {
				"ingotCopper",
				new ItemStack(itemAllomancyGrinder, 1,
						OreDictionary.WILDCARD_VALUE) }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				itemBronzeFlakes, 2), new Object[] {
				"ingotBronze",
				new ItemStack(itemAllomancyGrinder, 1,
						OreDictionary.WILDCARD_VALUE) }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				itemSteelFlakes, 2), new Object[] {
				"ingotSteel",
				new ItemStack(itemAllomancyGrinder, 1,
						OreDictionary.WILDCARD_VALUE) }));

		GameRegistry.addShapelessRecipe(new ItemStack(itemIronFlakes, 2),
				new ItemStack(Item.ingotIron), new ItemStack(
						itemAllomancyGrinder, 1, OreDictionary.WILDCARD_VALUE));
		GameRegistry.addShapelessRecipe(new ItemStack(itemSteelFlakes, 2),
				new ItemStack(itemIronFlakes), new ItemStack(Item.coal));
		GameRegistry.addShapelessRecipe(new ItemStack(itemBrassFlakes, 2),
				new ItemStack(itemZincFlakes), new ItemStack(itemCopperFlakes));
		GameRegistry.addShapelessRecipe(new ItemStack(itemPewterFlakes, 2),
				new ItemStack(itemTinFlakes), new ItemStack(itemLeadFlakes));
		GameRegistry.addShapelessRecipe(new ItemStack(itemBronzeFlakes, 2),
				new ItemStack(itemCopperFlakes), new ItemStack(itemTinFlakes));

		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 1),
				new ItemStack(itemIronFlakes), new ItemStack(itemVial, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 2),
				new ItemStack(itemSteelFlakes), new ItemStack(itemVial, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 3),
				new ItemStack(itemTinFlakes), new ItemStack(itemVial, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 4),
				new ItemStack(itemPewterFlakes), new ItemStack(itemVial, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 7),
				new ItemStack(itemZincFlakes), new ItemStack(itemVial, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 8),
				new ItemStack(itemBrassFlakes), new ItemStack(itemVial, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 5),
				new ItemStack(itemCopperFlakes), new ItemStack(itemVial, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 6),
				new ItemStack(itemBronzeFlakes), new ItemStack(itemVial, 1, 0));

		GameRegistry.addRecipe(new ItemStack(Mistcloak, 1), new Object[] {
				"W W", "WWW", "WWW", 'W', new ItemStack(Block.cloth, 1, 7) });
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(itemVial, 3, 0), " x ", "y y", " y ", 'x',
				"slabWood", 'y', "glass"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				itemAllomancyGrinder, 1, 0), "xxx", "yyy", "xxx", 'x',
				Item.ingotIron, 'y', Item.goldNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				itemCoinBag, 1, 0), " xy", "l l", " l ", 'l',
				Item.leather, 'y', Item.goldNugget, 'x', Item.leash));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				nuggetLerasium), new Object[] {
				new ItemStack(Block.blockGold, 1),
				new ItemStack(Item.netherStar, 1) }));

	}

	public static CreativeTabs tabsAllomancy = new CreativeTabAllomancy(
			CreativeTabs.getNextID(), "allomancy");

	public static void initBlocks() {
		oreTin = new Block(AllomancyConfig.oreTin, Material.rock)
				.setHardness(.5f).setStepSound(Block.soundStoneFootstep)
				.setCreativeTab(CreativeTabs.tabBlock)
				.setTextureName("allomancy:tinore")
				.setUnlocalizedName("allomancy:tinore");

		oreLead = new Block(AllomancyConfig.oreLead, Material.rock)
				.setHardness(.5f).setStepSound(Block.soundStoneFootstep)
				.setCreativeTab(CreativeTabs.tabBlock)
				.setTextureName("allomancy:leadore")
				.setUnlocalizedName("allomancy:leadore");

		oreCopper = new Block(AllomancyConfig.oreCopper, Material.rock)
				.setHardness(.5f).setStepSound(Block.soundStoneFootstep)
				.setCreativeTab(CreativeTabs.tabBlock)
				.setTextureName("allomancy:copperore")
				.setUnlocalizedName("allomancy:copperore");

		oreZinc = new Block(AllomancyConfig.oreZinc, Material.rock)
				.setHardness(.5f).setStepSound(Block.soundStoneFootstep)
				.setCreativeTab(CreativeTabs.tabBlock)
				.setTextureName("allomancy:zincore")
				.setUnlocalizedName("allomancy:zincore");

	}

	public static void setupBlocks() {
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

	public static EnumArmorMaterial WoolArmor = EnumHelper.addArmorMaterial(
			"Wool", 5, new int[] { 0, 4, 0, 0 }, 15);

	public static void initItems() {
		itemAllomancyGrinder = new ItemGrinder(AllomancyConfig.itemGrinder);
		
		itemCoinBag = new ItemCoinBag(AllomancyConfig.itemCoinBag).setUnlocalizedName("allomancy:coinbag").setCreativeTab(
				Registry.tabsAllomancy);
		
		itemTinIngot = new Item(AllomancyConfig.itemTinIngot)
				.setUnlocalizedName("allomancy:tiningot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		itemTinFlakes = new Item(AllomancyConfig.itemTinFlakes)
				.setUnlocalizedName("allomancy:tinflakes").setCreativeTab(
						Registry.tabsAllomancy);
		itemLeadIngot = new Item(AllomancyConfig.itemLeadIngot)
				.setUnlocalizedName("allomancy:leadingot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		itemLeadFlakes = new Item(AllomancyConfig.itemLeadFlakes)
				.setUnlocalizedName("allomancy:leadflakes").setCreativeTab(
						Registry.tabsAllomancy);
		itemCopperIngot = new Item(AllomancyConfig.itemCopperIngot)
				.setUnlocalizedName("allomancy:copperingot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		itemCopperFlakes = new Item(AllomancyConfig.itemCopperFlakes)
				.setUnlocalizedName("allomancy:copperflakes").setCreativeTab(
						Registry.tabsAllomancy);
		itemZincIngot = new Item(AllomancyConfig.itemZincIngot)
				.setUnlocalizedName("allomancy:zincingot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		itemZincFlakes = new Item(AllomancyConfig.itemZincFlakes)
				.setUnlocalizedName("allomancy:zincflakes").setCreativeTab(
						Registry.tabsAllomancy);
		itemIronFlakes = new Item(AllomancyConfig.itemIronFlakes)
				.setUnlocalizedName("allomancy:ironflakes").setCreativeTab(
						Registry.tabsAllomancy);
		itemSteelFlakes = new Item(AllomancyConfig.itemSteelFlakes)
				.setUnlocalizedName("allomancy:steelflakes").setCreativeTab(
						Registry.tabsAllomancy);
		itemBrassFlakes = new Item(AllomancyConfig.itemBrassFlakes)
				.setUnlocalizedName("allomancy:brassflakes").setCreativeTab(
						Registry.tabsAllomancy);
		itemPewterFlakes = new Item(AllomancyConfig.itemPewterFlakes)
				.setUnlocalizedName("allomancy:pewterflakes").setCreativeTab(
						Registry.tabsAllomancy);
		itemBronzeFlakes = new Item(AllomancyConfig.itemBronzeFlakes)
				.setUnlocalizedName("allomancy:bronzeflakes").setCreativeTab(
						Registry.tabsAllomancy);

		Mistcloak = new ItemMistcloak(AllomancyConfig.Mistcloak, WoolArmor, 5,
				1).setUnlocalizedName("allomancy:mistcloak").setCreativeTab(
				CreativeTabs.tabCombat);
		nuggetLerasium = new NuggetLerasium(AllomancyConfig.nuggetLerasium);
		itemVial = new ItemVial(AllomancyConfig.itemVial);

	}

	public static void setupItems() {
		GameRegistry.registerItem(itemAllomancyGrinder, "allomancy:Grinder");
		LanguageRegistry.addName(itemAllomancyGrinder, "Hand Grinder");
		itemAllomancyGrinder.setTextureName("allomancy:handgrinder");

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

		GameRegistry.registerItem(Mistcloak, "allomancy:mistcloak");
		LanguageRegistry.addName(Mistcloak, "Mistcloak");
		Mistcloak.setTextureName("allomancy:mistcloak");

		GameRegistry.registerItem(nuggetLerasium, "allomancy:lerasium");
		LanguageRegistry.addName(nuggetLerasium, "\u00A7bLerasium Nugget");
		nuggetLerasium.setTextureName("allomancy:lerasium");

		LanguageRegistry.instance().addStringLocalization(
				"itemGroup.Allomancy", "Allomancy");

		GameRegistry.registerItem(itemCoinBag, "allomancy:CoinBag");
		LanguageRegistry.addName(itemCoinBag, "Coin Bag");
		itemCoinBag.setTextureName("allomancy:CoinBag");
		
		GameRegistry.registerItem(itemVial, "vial");
		for (int i = 0; i < ItemVial.localName.length; i++) {
			ItemStack Item;
			Item = new ItemStack(itemVial, 1, i);
			LanguageRegistry.addName(Item, ItemVial.localName[i]);

		}
	}

	public static void setupKeybinds() {

	}

	public static ItemGrinder itemAllomancyGrinder;
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
	public static Item Mistcloak;
	public static Item itemCoinBag;
	public static Block oreTin;
	public static Block oreLead;
	public static Block oreCopper;
	public static Block oreZinc;
	public static NuggetLerasium nuggetLerasium;
	public static ItemVial itemVial;
	public static Achievement becomeMistborn;
}
