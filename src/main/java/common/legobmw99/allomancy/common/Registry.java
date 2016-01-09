package common.legobmw99.allomancy.common;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.lwjgl.input.Keyboard;

import common.legobmw99.allomancy.blocks.OreBlock;
import common.legobmw99.allomancy.items.ItemCoinBag;
import common.legobmw99.allomancy.items.ItemGrinder;
import common.legobmw99.allomancy.items.ItemMistcloak;
import common.legobmw99.allomancy.items.ItemVial;
import common.legobmw99.allomancy.items.NuggetLerasium;

public class Registry {
    public static KeyBinding changeGroup;
    public static KeyBinding burnFirst;
    public static KeyBinding burnSecond;
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
	public static Item itemCoinBag;
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
			.addArmorMaterial("Wool", null, 5, new int[] { 0, 4, 0, 0 }, 15);

	public static void ModContent() {
		addAchievements();
		initBlocks();
		initItems();
		oreRegistration();
		setupRecipes();
		initKeyBindings();
		registerPackets();
	}

	private static void addAchievements() {
		becomeMistborn = new Achievement("achievement.becomeMistborn",
				"becomeMistborn", -5, -2, Blocks.beacon, null).func_180788_c().setIndependent().setSpecial(); //registerAchievement


	}

	public static void setupRecipes() {
		GameRegistry.addSmelting(oreTin, new ItemStack(itemTinIngot, 1), 5);
		GameRegistry.addSmelting(oreCopper, new ItemStack(itemCopperIngot, 1),5);
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
				itemCoinBag, 1, 0), " xy", "l l", " l ", 'l',
				Items.leather, 'y', Items.gold_nugget, 'x', Items.lead));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				itemAllomancyGrinder, 1, 0), "xxx", "yyy", "xxx", 'x',
				Items.iron_ingot, 'y', Items.gold_nugget));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(
				nuggetLerasium), new Object[] {
				new ItemStack(Blocks.gold_block, 1),
				new ItemStack(Items.nether_star, 1) }));

	}

	public static void initBlocks() {
		OreBlock.init();
	}

	public static void initItems() {
		itemAllomancyGrinder = new ItemGrinder();
		GameRegistry.registerItem(itemAllomancyGrinder, "grinder");
		itemAllomancyGrinder.setUnlocalizedName("handgrinder");

		itemCoinBag = new ItemCoinBag().setUnlocalizedName("coinbag").setCreativeTab(
				Registry.tabsAllomancy);
		GameRegistry.registerItem(itemCoinBag, "coinbag");

		itemTinIngot = new Item().setUnlocalizedName("tiningot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		GameRegistry.registerItem(itemTinIngot, "tiningot");

		itemTinFlakes = new Item().setUnlocalizedName("tinflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemTinFlakes, "tinflakes");

		itemLeadIngot = new Item().setUnlocalizedName("leadingot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		GameRegistry.registerItem(itemLeadIngot, "leadingot");

		itemLeadFlakes = new Item().setUnlocalizedName("leadflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemLeadFlakes, "leadflakes");

		itemCopperIngot = new Item()
				.setUnlocalizedName("copperingot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		GameRegistry.registerItem(itemCopperIngot, "copperingot");
		

		itemCopperFlakes = new Item().setUnlocalizedName(
				"copperflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemCopperFlakes, "copperflakes");
		
		itemZincIngot = new Item().setUnlocalizedName("zincingot")
				.setCreativeTab(CreativeTabs.tabMaterials).setMaxDamage(0);
		GameRegistry.registerItem(itemZincIngot, "zincingot");


		itemZincFlakes = new Item().setUnlocalizedName("zincflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemZincFlakes, "zincflakes");
		
		itemIronFlakes = new Item().setUnlocalizedName("ironflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemIronFlakes, "ironflakes");
		
		itemSteelFlakes = new Item()
				.setUnlocalizedName("steelflakes").setCreativeTab(
						Registry.tabsAllomancy);
		GameRegistry.registerItem(itemSteelFlakes, "steelflakes");
		
		itemBrassFlakes = new Item()
				.setUnlocalizedName("brassflakes").setCreativeTab(
						Registry.tabsAllomancy);
		GameRegistry.registerItem(itemBrassFlakes, "brassflakes");
		
		itemPewterFlakes = new Item().setUnlocalizedName(
				"pewterflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemPewterFlakes, "pewterflakes");
		
		itemBronzeFlakes = new Item().setUnlocalizedName(
				"bronzeflakes")
				.setCreativeTab(Registry.tabsAllomancy);
		GameRegistry.registerItem(itemBronzeFlakes, "bronzeflakes");
		
		Mistcloak = new ItemMistcloak(WoolArmor, 5, 1).setUnlocalizedName(
				"mistcloak").setCreativeTab(CreativeTabs.tabCombat);
		GameRegistry.registerItem(Mistcloak, "mistcloak");
		
		nuggetLerasium = new NuggetLerasium();
		GameRegistry.registerItem(nuggetLerasium, "lerasium");
		
		itemVial = new ItemVial();
		GameRegistry.registerItem(itemVial, "itemVial");
		for (int i = 0; i < ItemVial.localName.length; i++) {
			ItemStack Item;
			Item = new ItemStack(itemVial, 1, i);
		}
	}
	public static void oreRegistration() {
		OreDictionary.registerOre("ingotCopper", new ItemStack(itemCopperIngot));
		OreDictionary.registerOre("ingotZinc", new ItemStack(itemZincIngot));
		OreDictionary.registerOre("ingotTin", new ItemStack(itemTinIngot));
		OreDictionary.registerOre("ingotLead", new ItemStack(itemLeadIngot));
	}
	public static void initKeyBindings(){
    	changeGroup = new KeyBinding("key.changeGroup", Keyboard.KEY_F, "key.categories.allomancy");
    	burnFirst = new KeyBinding("key.burnFirst", Keyboard.KEY_G, "key.categories.allomancy");
    	burnSecond = new KeyBinding("key.burnSecond", Keyboard.KEY_H, "key.categories.allomancy");

        ClientRegistry.registerKeyBinding(changeGroup);
        ClientRegistry.registerKeyBinding(burnFirst);
        ClientRegistry.registerKeyBinding(burnSecond);
	}
	public static void registerPackets() {
		
	}
	public static void Renders() {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(Registry.oreTin), 0, new ModelResourceLocation("allomancy:tinore", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreZinc), 0, new ModelResourceLocation("allomancy:zincore", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreCopper), 0, new ModelResourceLocation("allomancy:copperore", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreLead), 0, new ModelResourceLocation("allomancy:leadore", "inventory"));

    	renderItem.getItemModelMesher().register(nuggetLerasium, 0, new ModelResourceLocation("allomancy:lerasium", "inventory"));
    	renderItem.getItemModelMesher().register(itemAllomancyGrinder, 0, new ModelResourceLocation("allomancy:grinder", "inventory"));
    	renderItem.getItemModelMesher().register(Mistcloak, 0, new ModelResourceLocation("allomancy:mistcloak", "inventory"));

    	
    	renderItem.getItemModelMesher().register(itemBrassFlakes, 0, new ModelResourceLocation("allomancy:brassflakes", "inventory"));
    	renderItem.getItemModelMesher().register(itemBronzeFlakes, 0, new ModelResourceLocation("allomancy:bronzeflakes", "inventory"));
    	renderItem.getItemModelMesher().register(itemIronFlakes, 0, new ModelResourceLocation("allomancy:ironflakes", "inventory"));
    	renderItem.getItemModelMesher().register(itemSteelFlakes, 0, new ModelResourceLocation("allomancy:steelflakes", "inventory"));
    	renderItem.getItemModelMesher().register(itemCopperFlakes, 0, new ModelResourceLocation("allomancy:copperflakes", "inventory"));
    	renderItem.getItemModelMesher().register(itemPewterFlakes, 0, new ModelResourceLocation("allomancy:pewterflakes", "inventory"));
    	renderItem.getItemModelMesher().register(itemTinFlakes, 0, new ModelResourceLocation("allomancy:tinflakes", "inventory"));
    	renderItem.getItemModelMesher().register(itemLeadFlakes, 0, new ModelResourceLocation("allomancy:leadflakes", "inventory"));
    	renderItem.getItemModelMesher().register(itemZincFlakes, 0, new ModelResourceLocation("allomancy:zincflakes", "inventory"));
    	
    	renderItem.getItemModelMesher().register(itemVial, 0, new ModelResourceLocation("allomancy:emptyvial", "inventory"));
    	renderItem.getItemModelMesher().register(itemVial, 1, new ModelResourceLocation("allomancy:ironelixer", "inventory"));



	}

}
