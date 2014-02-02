package common.legobmw99.allomancy.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import common.legobmw99.allomancy.blocks.OreBlock;
import common.legobmw99.allomancy.blocks.OreBlock.OreType;
import common.legobmw99.allomancy.items.ItemGrinder;
import common.legobmw99.allomancy.items.ItemMistcloak;
import common.legobmw99.allomancy.items.ItemVial;
import common.legobmw99.allomancy.items.NuggetLerasium;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Registry {
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
	public static Block oreTin;
	public static Block oreLead;
	public static Block oreCopper;
	public static Block oreZinc;
	public static NuggetLerasium nuggetLerasium;
	public static ItemVial itemVial;
	public static Achievement becomeMistborn;
	public static CreativeTabs tabsAllomancy = new CreativeTabAllomancy(
			CreativeTabs.getNextID(), "allomancy");
	public static ArmorMaterial WoolArmor = net.minecraftforge.common.util.EnumHelper
			.addArmorMaterial("Wool", 5, new int[] { 0, 4, 0, 0 }, 15);

	public static void ModContent() {
		addAchievements();
		oreRegistration();
		setupRecipes();
		initBlocks();
		initItems();
	}

	private static void addAchievements() {
		// TODO nuggetLerasium
		becomeMistborn = new Achievement("achievement.becomeMistborn",
				"becomeMistborn", -5, -2, Blocks.portal, null)
				.initIndependentStat().setSpecial().registerStat();
		LanguageRegistry.instance().addStringLocalization(
				"achievement.becomeMistborn", "en_US", "Become Mistborn!");
		LanguageRegistry.instance().addStringLocalization(
				"achievement.becomeMistborn.desc", "en_US",
				"You have a power most people envy...");
	}

	public static void oreRegistration() {
		OreDictionary
				.registerOre("ingotCopper", new ItemStack(itemCopperIngot));
		OreDictionary.registerOre("ingotZinc", new ItemStack(itemZincIngot));
		OreDictionary.registerOre("ingotTin", new ItemStack(itemTinIngot));
		OreDictionary.registerOre("ingotLead", new ItemStack(itemLeadIngot));
	}

	public static void setupRecipes() {
		GameRegistry.addSmelting(oreTin, new ItemStack(itemTinIngot, 1), 5);
		GameRegistry.addSmelting(oreCopper, new ItemStack(itemCopperIngot, 1),
				5);
		GameRegistry.addSmelting(oreLead, new ItemStack(itemLeadIngot, 1), 5);
		GameRegistry.addSmelting(oreZinc, new ItemStack(itemZincIngot, 1), 5);

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
				new ItemStack(Items.iron_ingot), new ItemStack(
						itemAllomancyGrinder, 1, OreDictionary.WILDCARD_VALUE));
		GameRegistry.addShapelessRecipe(new ItemStack(itemSteelFlakes, 2),
				new ItemStack(itemIronFlakes), new ItemStack(Items.coal));
		GameRegistry.addShapelessRecipe(new ItemStack(itemBrassFlakes, 2),
				new ItemStack(itemZincFlakes), new ItemStack(itemCopperFlakes));
		GameRegistry.addShapelessRecipe(new ItemStack(itemPewterFlakes, 2),
				new ItemStack(itemTinFlakes), new ItemStack(itemLeadFlakes));
		GameRegistry.addShapelessRecipe(new ItemStack(itemBronzeFlakes, 2),
				new ItemStack(itemCopperFlakes), new ItemStack(itemTinFlakes));

		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 1),
				new ItemStack(itemIronFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.water_bucket));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 2),
				new ItemStack(itemSteelFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.water_bucket));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 3),
				new ItemStack(itemTinFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.water_bucket));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 4),
				new ItemStack(itemPewterFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.water_bucket));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 5),
				new ItemStack(itemZincFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.water_bucket));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 6),
				new ItemStack(itemBrassFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.water_bucket));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 7),
				new ItemStack(itemCopperFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.water_bucket));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 8),
				new ItemStack(itemBronzeFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.water_bucket));

		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 1),
				new ItemStack(itemIronFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.potionitem));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 2),
				new ItemStack(itemSteelFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.potionitem));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 3),
				new ItemStack(itemTinFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.potionitem));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 4),
				new ItemStack(itemPewterFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.potionitem));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 5),
				new ItemStack(itemZincFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.potionitem));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 6),
				new ItemStack(itemBrassFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.potionitem));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 7),
				new ItemStack(itemCopperFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.potionitem));
		GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 8),
				new ItemStack(itemBronzeFlakes), new ItemStack(itemVial, 1, 0),
				new ItemStack(Items.potionitem));

		GameRegistry.addRecipe(new ItemStack(Mistcloak, 1), new Object[] {
				"W W", "WWW", "WWW", 'W', new ItemStack(Blocks.wool, 1, 7) });
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(itemVial, 1, 0), " x ", "y y", " y ", 'x',
				"slabWood", 'y', Blocks.glass));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				itemAllomancyGrinder, 1, 0), "xxx", "yyy", "xxx", 'x',
				Items.iron_ingot, 'y', Items.gold_nugget));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				nuggetLerasium), new Object[] {
				new ItemStack(Blocks.gold_block, 1),
				new ItemStack(Items.nether_star, 1) }));

	}

	public static void initBlocks() {
		oreTin = new OreBlock(Material.field_151576_e, OreType.TIN)
				.func_149663_c("allomancy:tinore");
		GameRegistry.registerBlock(oreTin, "allomancy:tinore");
		LanguageRegistry.addName(oreTin, "Tin Ore");

		oreLead = new OreBlock(Material.field_151576_e, OreType.LEAD)
				.func_149663_c("allomancy:leadore");
		GameRegistry.registerBlock(oreLead, "allomancy:leadore");
		LanguageRegistry.addName(oreLead, "Lead Ore");

		oreCopper = new OreBlock(Material.field_151576_e, OreType.COPPER)
				.func_149663_c("allomancy:copperore");
		GameRegistry.registerBlock(oreCopper, "allomancy:copperore");
		LanguageRegistry.addName(oreCopper, "Copper Ore");

		oreZinc = new OreBlock(Material.field_151576_e, OreType.ZINC)
				.func_149663_c("allomancy:zincore");
		GameRegistry.registerBlock(oreZinc, "allomancy:zincore");
		LanguageRegistry.addName(oreZinc, "Zinc Ore");
	}

	public static void initItems() {
		itemAllomancyGrinder = new ItemGrinder();
		GameRegistry.registerItem(itemAllomancyGrinder, "allomancy:Grinder");
		LanguageRegistry.addName(itemAllomancyGrinder, "Hand Grinder");
		itemAllomancyGrinder.setTextureName("allomancy:handgrinder");

		itemTinIngot = new Item().setUnlocalizedName("allomancy:tiningot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		GameRegistry.registerItem(itemTinIngot, "allomancy:tiningot");
		LanguageRegistry.addName(itemTinIngot, "Tin Ingot");
		itemTinIngot.setTextureName("allomancy:tiningot");

		itemTinFlakes = new Item().setUnlocalizedName("allomancy:tinflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemTinFlakes, "allomancy:tinflakes");
		LanguageRegistry.addName(itemTinFlakes, "Tin Flakes");
		itemTinFlakes.setTextureName("allomancy:tinflakes");

		itemLeadIngot = new Item().setUnlocalizedName("allomancy:leadingot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		GameRegistry.registerItem(itemLeadIngot, "allomancy:leadingot");
		LanguageRegistry.addName(itemLeadIngot, "Lead Ingot");
		itemLeadIngot.setTextureName("allomancy:leadingot");

		itemLeadFlakes = new Item().setUnlocalizedName("allomancy:leadflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemLeadFlakes, "allomancy:leadflakes");
		LanguageRegistry.addName(itemLeadFlakes, "Lead Flakes");
		itemLeadFlakes.setTextureName("allomancy:leadflakes");

		itemCopperIngot = new Item()
				.setUnlocalizedName("allomancy:copperingot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		GameRegistry.registerItem(itemCopperIngot, "allomancy:copperingot");
		LanguageRegistry.addName(itemCopperIngot, "Copper Ingot");
		itemCopperIngot.setTextureName("allomancy:copperingot");

		itemCopperFlakes = new Item().setUnlocalizedName(
				"allomancy:copperflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemCopperFlakes, "allomancy:copperflakes");
		LanguageRegistry.addName(itemCopperFlakes, "Copper Flakes");
		itemCopperFlakes.setTextureName("allomancy:copperflakes");

		itemZincIngot = new Item().setUnlocalizedName("allomancy:zincingot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		GameRegistry.registerItem(itemZincIngot, "allomancy:zincingot");
		LanguageRegistry.addName(itemZincIngot, "Zinc Ingot");
		itemZincIngot.setTextureName("allomancy:zincingot");

		itemZincFlakes = new Item().setUnlocalizedName("allomancy:zincflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemZincFlakes, "allomancy:zincflakes");
		LanguageRegistry.addName(itemZincFlakes, "Zinc Flakes");
		itemZincFlakes.setTextureName("allomancy:zincflakes");

		itemIronFlakes = new Item().setUnlocalizedName("allomancy:ironflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemIronFlakes, "allomancy:ironflakes");
		LanguageRegistry.addName(itemIronFlakes, "Iron Flakes");
		itemIronFlakes.setTextureName("allomancy:ironflakes");

		itemSteelFlakes = new Item()
				.setUnlocalizedName("allomancy:steelflakes").setCreativeTab(
						Registry.tabsAllomancy);
		GameRegistry.registerItem(itemSteelFlakes, "allomancy:steelflakes");
		LanguageRegistry.addName(itemSteelFlakes, "Steel Flakes");
		itemSteelFlakes.setTextureName("allomancy:steelflakes");

		itemBrassFlakes = new Item()
				.setUnlocalizedName("allomancy:brassflakes").setCreativeTab(
						Registry.tabsAllomancy);
		GameRegistry.registerItem(itemBrassFlakes, "allomancy:brassflakes");
		LanguageRegistry.addName(itemBrassFlakes, "Brass Flakes");
		itemBrassFlakes.setTextureName("allomancy:brassflakes");

		itemPewterFlakes = new Item().setUnlocalizedName(
				"allomancy:pewterflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemPewterFlakes, "allomancy:pewterflakes");
		LanguageRegistry.addName(itemPewterFlakes, "Pewter Flakes");
		itemPewterFlakes.setTextureName("allomancy:pewterflakes");

		itemBronzeFlakes = new Item().setUnlocalizedName(
				"allomancy:bronzeflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemBronzeFlakes, "allomancy:bronzeflakes");
		LanguageRegistry.addName(itemBronzeFlakes, "Bronze Flakes");
		itemBronzeFlakes.setTextureName("allomancy:bronzeflakes");

		Mistcloak = new ItemMistcloak(WoolArmor, 5, 1).setUnlocalizedName(
				"allomancy:mistcloak").setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(Mistcloak, "allomancy:mistcloak");
		LanguageRegistry.addName(Mistcloak, "Mistcloak");
		Mistcloak.setTextureName("allomancy:mistcloak");

		nuggetLerasium = new NuggetLerasium();
		GameRegistry.registerItem(nuggetLerasium, "allomancy:lerasium");
		LanguageRegistry.addName(nuggetLerasium, "\u00A7bLerasium Nugget");
		nuggetLerasium.setTextureName("allomancy:lerasium");

		itemVial = new ItemVial();
		GameRegistry.registerItem(itemVial, "vial");
		for (int i = 0; i < ItemVial.localName.length; i++) {
			ItemStack Item;
			Item = new ItemStack(itemVial, 1, i);
			LanguageRegistry.addName(Item, ItemVial.localName[i]);
		}
	}
}
