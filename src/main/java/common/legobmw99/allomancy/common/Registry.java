package common.legobmw99.allomancy.common;

import org.lwjgl.input.Keyboard;

import common.legobmw99.allomancy.Allomancy;
import common.legobmw99.allomancy.blocks.OreBlock;
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.items.ItemCoinBag;
import common.legobmw99.allomancy.items.ItemGrinder;
import common.legobmw99.allomancy.items.ItemMistcloak;
import common.legobmw99.allomancy.items.ItemVial;
import common.legobmw99.allomancy.items.NuggetLerasium;
import common.legobmw99.allomancy.network.packets.AllomancyCapabiltiesPacket;
import common.legobmw99.allomancy.network.packets.AllomancyPowerPacket;
import common.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import common.legobmw99.allomancy.network.packets.GetCapabilitiesPacket;
import common.legobmw99.allomancy.network.packets.MoveEntityPacket;
import common.legobmw99.allomancy.network.packets.SelectMetalPacket;
import common.legobmw99.allomancy.network.packets.StopFallPacket;
import common.legobmw99.allomancy.network.packets.UpdateBurnPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class Registry {
	public static KeyBinding changeGroup;
	public static KeyBinding burnFirst;
	public static KeyBinding burnSecond;
	public static SimpleNetworkWrapper network;
	public static ItemGrinder itemAllomancyGrinder;
	public static Item itemTinIngot;
	public static Item itemLeadIngot;
	public static Item itemCopperIngot;
	public static Item itemZincIngot;
	public static Item itemCoinBag;
	public static Item Mistcloak;
	public static Item nuggetLerasium;
	public static ItemVial itemVial;
	public static String[] flakeMetals = { "Iron", "Steel", "Tin", "Pewter", "Zinc", "Brass", "Copper", "Bronze",
			"Lead" };
	public static Achievement becomeMistborn;
	public static CreativeTabs tabsAllomancy = new CreativeTabAllomancy(CreativeTabs.getNextID(), "allomancy");
	public static ArmorMaterial WoolArmor = net.minecraftforge.common.util.EnumHelper.addArmorMaterial("Wool",
			"allomancy:wool", 5, new int[] { 0, 4, 0, 0 }, 15, null, 0);

	public static void addAchievements() {
		becomeMistborn = new Achievement("achievement.becomeMistborn", "becomeMistborn", -5, -2,
				Registry.nuggetLerasium, null).initIndependentStat().registerStat().setSpecial();
	}

	public static void initBlocks() {
		OreBlock.init();
	}

	public static void initItems() {
		// Register the basic, not-metallic items
		GameRegistry.register(itemAllomancyGrinder = new ItemGrinder(),
				new ResourceLocation(Allomancy.MODID, "grinder"));
		GameRegistry.register(itemCoinBag = new ItemCoinBag(),
				new ResourceLocation(Allomancy.MODID, "coinbag"));
		GameRegistry.register(Mistcloak = new ItemMistcloak(WoolArmor, 1, EntityEquipmentSlot.CHEST),
				new ResourceLocation(Allomancy.MODID, "mistcloak"));
		GameRegistry.register(nuggetLerasium = new NuggetLerasium(),
				new ResourceLocation(Allomancy.MODID, "nuggetLerasium"));

		// Register ItemVial and its subtypes
		GameRegistry.register(itemVial = new ItemVial(), new ResourceLocation(Allomancy.MODID, "itemVial"));
		for (int i = 0; i < ItemVial.unlocalName.length; i++) {
			ItemStack Item;
			Item = new ItemStack(itemVial, 1, i);
		}

		// Register ingots and add them to the ore dictionary
		GameRegistry.register(
						itemTinIngot = new Item().setUnlocalizedName("ingotTin")
							.setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0),
						new ResourceLocation(Allomancy.MODID, "ingotTin"));
		GameRegistry.register(
						itemLeadIngot = new Item().setUnlocalizedName("ingotLead")
								.setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0),
						new ResourceLocation(Allomancy.MODID, "ingotLead"));
		GameRegistry.register(
						itemCopperIngot = new Item().setUnlocalizedName("ingotCopper")
								.setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0),
						new ResourceLocation(Allomancy.MODID, "ingotCopper"));
		GameRegistry.register(
						itemZincIngot = new Item().setUnlocalizedName("ingotZinc")
								.setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0),
						new ResourceLocation(Allomancy.MODID, "ingotZinc"));

		OreDictionary.registerOre("ingotZinc", itemZincIngot);
		OreDictionary.registerOre("ingotTin", itemTinIngot);
		OreDictionary.registerOre("ingotCopper", itemCopperIngot);
		OreDictionary.registerOre("ingotLead", itemLeadIngot);

		// Register flakes
		for (int i = 0; i < flakeMetals.length; i++) {
			GameRegistry.register(
					new Item().setUnlocalizedName("flake" + flakeMetals[i]).setCreativeTab(Registry.tabsAllomancy),
					new ResourceLocation(Allomancy.MODID, "flake" + flakeMetals[i]));
		}
	}

	public static void initKeyBindings() {
		changeGroup = new KeyBinding("key.changeGroup", Keyboard.KEY_R, "key.categories.allomancy");
		burnFirst = new KeyBinding("key.burnFirst", Keyboard.KEY_F, "key.categories.allomancy");
		burnSecond = new KeyBinding("key.burnSecond", Keyboard.KEY_G, "key.categories.allomancy");

		ClientRegistry.registerKeyBinding(changeGroup);
		ClientRegistry.registerKeyBinding(burnFirst);
		ClientRegistry.registerKeyBinding(burnSecond);
	}

	public static void registerPackets() {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("allomancy");
		network.registerMessage(StopFallPacket.Handler.class, StopFallPacket.class, 0, Side.SERVER);
		network.registerMessage(AllomancyPowerPacket.Handler.class, AllomancyPowerPacket.class, 1, Side.CLIENT);
		network.registerMessage(SelectMetalPacket.Handler.class, SelectMetalPacket.class, 2, Side.SERVER);
		network.registerMessage(MoveEntityPacket.Handler.class, MoveEntityPacket.class, 3, Side.SERVER);
		network.registerMessage(UpdateBurnPacket.Handler.class, UpdateBurnPacket.class, 4, Side.SERVER);
		network.registerMessage(AllomancyCapabiltiesPacket.Handler.class, AllomancyCapabiltiesPacket.class, 5,Side.CLIENT);
		network.registerMessage(ChangeEmotionPacket.Handler.class, ChangeEmotionPacket.class, 6, Side.SERVER);
		network.registerMessage(GetCapabilitiesPacket.Handler.class, GetCapabilitiesPacket.class, 7, Side.SERVER);

	}

	@SideOnly(Side.CLIENT)
	public static void registerRenders() {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

		// Call on rendersnowball for gold nugget projectile
		RenderingRegistry.registerEntityRenderingHandler(EntityGoldNugget.class,
				new RenderSnowball((Minecraft.getMinecraft().getRenderManager()), Items.GOLD_NUGGET, renderItem));

		// Register ore models individually.
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(OreBlock.oreTin), 0,
				new ModelResourceLocation("allomancy:oreTin", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(OreBlock.oreZinc), 0,
				new ModelResourceLocation("allomancy:oreZinc", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(OreBlock.oreCopper), 0,
				new ModelResourceLocation("allomancy:oreCopper", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(OreBlock.oreLead), 0,
				new ModelResourceLocation("allomancy:oreLead", "inventory"));

		// Register ingot models individually
		renderItem.getItemModelMesher().register(itemZincIngot, 0,
				new ModelResourceLocation("allomancy:ingotZinc", "inventory"));
		renderItem.getItemModelMesher().register(itemLeadIngot, 0,
				new ModelResourceLocation("allomancy:ingotLead", "inventory"));
		renderItem.getItemModelMesher().register(itemCopperIngot, 0,
				new ModelResourceLocation("allomancy:ingotCopper", "inventory"));
		renderItem.getItemModelMesher().register(itemTinIngot, 0,
				new ModelResourceLocation("allomancy:ingotTin", "inventory"));

		// Register misc item models individually
		renderItem.getItemModelMesher().register(nuggetLerasium, 0,
				new ModelResourceLocation("allomancy:nuggetLerasium", "inventory"));
		renderItem.getItemModelMesher().register(itemAllomancyGrinder, 0,
				new ModelResourceLocation("allomancy:grinder", "inventory"));
		renderItem.getItemModelMesher().register(Mistcloak, 0,
				new ModelResourceLocation("allomancy:mistcloak", "inventory"));
		renderItem.getItemModelMesher().register(itemCoinBag, 0,
				new ModelResourceLocation("allomancy:coinbag", "inventory"));

		// Register flake models
		for (int i = 0; i < flakeMetals.length; i++) {
			renderItem.getItemModelMesher().register(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[i]),
					0, new ModelResourceLocation("allomancy:" + "flake" + flakeMetals[i], "inventory"));
		}

		// Register vial model and all variants
		ModelBakery.registerItemVariants(itemVial,
				new ModelResourceLocation("allomancy:itemVial.emptyvial", "inventory"),
				new ModelResourceLocation("allomancy:itemVial.ironelixer", "inventory"),
				new ModelResourceLocation("allomancy:itemVial.steelelixer", "inventory"),
				new ModelResourceLocation("allomancy:itemVial.tinelixer", "inventory"),
				new ModelResourceLocation("allomancy:itemVial.pewterelixer", "inventory"),
				new ModelResourceLocation("allomancy:itemVial.zincelixer", "inventory"),
				new ModelResourceLocation("allomancy:itemVial.brasselixer", "inventory"),
				new ModelResourceLocation("allomancy:itemVial.copperelixer", "inventory"),
				new ModelResourceLocation("allomancy:itemVial.bronzeelixer", "inventory"),
				new ModelResourceLocation("allomancy:itemVial.ultimateelixer", "inventory"));
		for (int i = 0; i < ItemVial.unlocalName.length; i++) {
			renderItem.getItemModelMesher().register(itemVial, i,
					new ModelResourceLocation("allomancy:itemVial." + ItemVial.unlocalName[i], "inventory"));

		}
	}

	public static void setupRecipes() {

		GameRegistry.addSmelting(OreBlock.oreTin, new ItemStack(itemTinIngot, 1), 5);
		GameRegistry.addSmelting(OreBlock.oreCopper, new ItemStack(itemCopperIngot, 1), 5);
		GameRegistry.addSmelting(OreBlock.oreLead, new ItemStack(itemLeadIngot, 1), 5);
		GameRegistry.addSmelting(OreBlock.oreZinc, new ItemStack(itemZincIngot, 1), 5);

		// Add a recipe for each flake using the ore dictionary
		for (int i = 0; i < flakeMetals.length; i++) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(
					new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[i]), 2),
					new Object[] { "ingot" + flakeMetals[i],
							new ItemStack(itemAllomancyGrinder.setContainerItem(itemAllomancyGrinder)) }));
		}

		GameRegistry.addShapelessRecipe(new ItemStack(new Item().getByNameOrId("allomancy:flakeSteel"), 2),
				new ItemStack(new Item().getByNameOrId("allomancy:flakeIron")), new ItemStack(Items.COAL));
		GameRegistry.addShapelessRecipe(new ItemStack(new Item().getByNameOrId("allomancy:flakeBrass"), 2),
				new ItemStack(new Item().getByNameOrId("allomancy:flakeZinc")),
				new ItemStack(new Item().getByNameOrId("allomancy:flakeCopper")));
		GameRegistry.addShapelessRecipe(new ItemStack(new Item().getByNameOrId("allomancy:flakePewter"), 2),
				new ItemStack(new Item().getByNameOrId("allomancy:flakeTin")),
				new ItemStack(new Item().getByNameOrId("allomancy:flakeLead")));
		GameRegistry.addShapelessRecipe(new ItemStack(new Item().getByNameOrId("allomancy:flakeBronze"), 2),
				new ItemStack(new Item().getByNameOrId("allomancy:flakeCopper")),
				new ItemStack(new Item().getByNameOrId("allomancy:flakeTin")));

		// Add the basic eight metal vial recipes 
		for (int i = 1; i < ItemVial.unlocalName.length - 1; i++) {
			GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, i),
					new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[i - 1])),
					new ItemStack(itemVial, 1, 0));
		}
		// Add the ultimate vial recipe 
	      GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, 9),
                  new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[0])),
                  new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[1])),
                  new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[2])),
                  new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[3])),
                  new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[4])),
                  new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[5])),
                  new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[6])),
                  new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[7])),
                  new ItemStack(itemVial, 1, 0));

		GameRegistry.addRecipe(new ItemStack(Mistcloak, 1),
				new Object[] { "W W", "WWW", "WWW", 'W', new ItemStack(Blocks.WOOL, 1, 7) });
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemVial, 3, 0), " x ", "y y", " y ", 'x', "slabWood",
				'y', Blocks.GLASS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCoinBag, 1, 0), " xy", "l l", " l ", 'l',
				Items.LEATHER, 'y', Items.GOLD_NUGGET, 'x', Items.LEAD));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemAllomancyGrinder, 1, 0), "xxx", "yyy", "xxx", 'x',
				Items.IRON_INGOT, 'y', Items.GOLD_NUGGET));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(nuggetLerasium),
				new Object[] { new ItemStack(Blocks.GOLD_BLOCK, 1), new ItemStack(Items.NETHER_STAR, 1) }));

	}

}
