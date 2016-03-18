package common.legobmw99.allomancy.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelBakery;
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
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.lwjgl.input.Keyboard;

import common.legobmw99.allomancy.blocks.OreBlock;
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.items.ItemCoinBag;
import common.legobmw99.allomancy.items.ItemGrinder;
import common.legobmw99.allomancy.items.ItemMistcloak;
import common.legobmw99.allomancy.items.ItemVial;
import common.legobmw99.allomancy.items.NuggetLerasium;
import common.legobmw99.allomancy.network.packets.AllomancyDataPacket;
import common.legobmw99.allomancy.network.packets.BecomeMistbornPacket;
import common.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import common.legobmw99.allomancy.network.packets.MoveEntityPacket;
import common.legobmw99.allomancy.network.packets.SelectMetalPacket;
import common.legobmw99.allomancy.network.packets.StopFallPacket;
import common.legobmw99.allomancy.network.packets.UpdateBurnPacket;

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
	public static NuggetLerasium nuggetLerasium;
	public static ItemVial itemVial;
	public static String[] flakeMetals = {"Iron","Steel", "Tin", "Pewter", "Zinc", "Brass", "Copper", "Bronze", "Lead"};
	public static Achievement becomeMistborn;
	public static CreativeTabs tabsAllomancy = new CreativeTabAllomancy(CreativeTabs.getNextID(), "allomancy");
	public static ArmorMaterial WoolArmor = net.minecraftforge.common.util.EnumHelper.addArmorMaterial("Wool", "allomancy:wool", 5, new int[] { 0, 4, 0, 0 }, 15);

	public static void addAchievements() {
		becomeMistborn = new Achievement("achievement.becomeMistborn","becomeMistborn", -5, -2, Registry.nuggetLerasium, null).initIndependentStat().registerStat().setSpecial();
	}

	public static void initBlocks() {
		OreBlock.init();
	}

	public static void initItems() {
		//Register the basic, not-metallic items
		GameRegistry.registerItem(itemAllomancyGrinder = new ItemGrinder(), "grinder");
		GameRegistry.registerItem(itemCoinBag = new ItemCoinBag(), "coinbag");
		GameRegistry.registerItem(Mistcloak = new ItemMistcloak(WoolArmor, 1, 1), "mistcloak");
		GameRegistry.registerItem(nuggetLerasium = new NuggetLerasium(), "nuggetLerasium");

		//Register ItemVial and its subtypes
		GameRegistry.registerItem(itemVial = new ItemVial(), "itemVial");
		for (int i = 0; i < ItemVial.unlocalName.length; i++) {
			ItemStack Item;
			Item = new ItemStack(itemVial, 1, i);
		}	
		
		
		//Register ingots and add them to the ore dictionary
		GameRegistry.registerItem(itemTinIngot = new Item().setUnlocalizedName("ingotTin").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0), "ingotTin");
		GameRegistry.registerItem(itemLeadIngot = new Item().setUnlocalizedName("ingotLead").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0), "ingotLead");
		GameRegistry.registerItem(itemCopperIngot = new Item().setUnlocalizedName("ingotCopper").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0), "ingotCopper");
		GameRegistry.registerItem(itemZincIngot = new Item().setUnlocalizedName("ingotZinc").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0), "ingotZinc");
		
		OreDictionary.registerOre("ingotZinc", itemZincIngot);
		OreDictionary.registerOre("ingotTin", itemTinIngot);
		OreDictionary.registerOre("ingotCopper", itemCopperIngot);
		OreDictionary.registerOre("ingotLead", itemLeadIngot);
		
		//Register flakes
		for (int i = 0; i < flakeMetals.length; i++) {
			GameRegistry.registerItem(new Item().setUnlocalizedName("flake" + flakeMetals[i]).setCreativeTab(Registry.tabsAllomancy), "flake" + flakeMetals[i]);
		}	
	}

	public static void initKeyBindings(){
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
	       network.registerMessage(BecomeMistbornPacket.Handler.class, BecomeMistbornPacket.class, 1, Side.CLIENT);
	       network.registerMessage(SelectMetalPacket.Handler.class, SelectMetalPacket.class, 2, Side.SERVER);
	       network.registerMessage(MoveEntityPacket.Handler.class, MoveEntityPacket.class, 3, Side.SERVER);
	       network.registerMessage(UpdateBurnPacket.Handler.class, UpdateBurnPacket.class, 4, Side.SERVER);
	       network.registerMessage(AllomancyDataPacket.Handler.class, AllomancyDataPacket.class, 5, Side.CLIENT);
	       network.registerMessage(ChangeEmotionPacket.Handler.class, ChangeEmotionPacket.class, 6, Side.SERVER);

	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenders() {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		//Call on rendersnowball for gold nugget projectile
		RenderingRegistry.registerEntityRenderingHandler(EntityGoldNugget.class , new RenderSnowball((Minecraft.getMinecraft().getRenderManager()), Items.gold_nugget, renderItem));
		
		//Register ore models individually.
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(OreBlock.oreTin), 0, new ModelResourceLocation("allomancy:oreTin", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(OreBlock.oreZinc), 0, new ModelResourceLocation("allomancy:oreZinc", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(OreBlock.oreCopper), 0, new ModelResourceLocation("allomancy:oreCopper", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(OreBlock.oreLead), 0, new ModelResourceLocation("allomancy:oreLead", "inventory"));
    	
		//Register ingot models individually
    	renderItem.getItemModelMesher().register(itemZincIngot, 0, new ModelResourceLocation("allomancy:ingotZinc", "inventory"));
    	renderItem.getItemModelMesher().register(itemLeadIngot, 0, new ModelResourceLocation("allomancy:ingotLead", "inventory"));
    	renderItem.getItemModelMesher().register(itemCopperIngot, 0, new ModelResourceLocation("allomancy:ingotCopper", "inventory"));
    	renderItem.getItemModelMesher().register(itemTinIngot, 0, new ModelResourceLocation("allomancy:ingotTin", "inventory"));
    	
    	//Register misc item models individually
    	renderItem.getItemModelMesher().register(nuggetLerasium, 0, new ModelResourceLocation("allomancy:nuggetLerasium", "inventory"));
    	renderItem.getItemModelMesher().register(itemAllomancyGrinder, 0, new ModelResourceLocation("allomancy:grinder", "inventory"));
    	renderItem.getItemModelMesher().register(Mistcloak, 0, new ModelResourceLocation("allomancy:mistcloak", "inventory"));
    	renderItem.getItemModelMesher().register(itemCoinBag, 0, new ModelResourceLocation("allomancy:coinbag", "inventory"));

    	//Register flake models
		for (int i = 0; i < flakeMetals.length; i++) {
	    	renderItem.getItemModelMesher().register(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[i]), 0, new ModelResourceLocation("allomancy:" + "flake" + flakeMetals[i], "inventory"));
		}
    	
		//Register vial model and all variants
    	ModelBakery.registerItemVariants(itemVial,new ModelResourceLocation("allomancy:itemVial.emptyvial", "inventory"),new ModelResourceLocation("allomancy:itemVial.ironelixer", "inventory"),new ModelResourceLocation("allomancy:itemVial.steelelixer", "inventory"),new ModelResourceLocation("allomancy:itemVial.tinelixer", "inventory"),new ModelResourceLocation("allomancy:itemVial.pewterelixer", "inventory"), new ModelResourceLocation("allomancy:itemVial.zincelixer", "inventory"),new ModelResourceLocation("allomancy:itemVial.brasselixer", "inventory"), new ModelResourceLocation("allomancy:itemVial.copperelixer", "inventory"),new ModelResourceLocation("allomancy:itemVial.bronzeelixer", "inventory"));
		for (int i = 0; i < ItemVial.unlocalName.length; i++) {
	    	renderItem.getItemModelMesher().register(itemVial, i, new ModelResourceLocation("allomancy:itemVial." + ItemVial.unlocalName[i], "inventory"));

		}	
	}
	public static void setupRecipes() {
		
		GameRegistry.addSmelting(OreBlock.oreTin, new ItemStack(itemTinIngot, 1), 5);
		GameRegistry.addSmelting(OreBlock.oreCopper, new ItemStack(itemCopperIngot, 1),5);
		GameRegistry.addSmelting(OreBlock.oreLead, new ItemStack(itemLeadIngot, 1), 5);
		GameRegistry.addSmelting(OreBlock.oreZinc, new ItemStack(itemZincIngot, 1), 5);
		
		//Add a recipe for each flake using the ore dictionary
		for (int i = 0; i < flakeMetals.length; i++) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(new Item().getByNameOrId("allomancy:"+"flake" + flakeMetals[i]), 2), new Object[] {"ingot" + flakeMetals[i],  new ItemStack(itemAllomancyGrinder.setContainerItem(itemAllomancyGrinder))}));
		}
		
		GameRegistry.addShapelessRecipe(new ItemStack(new Item().getByNameOrId("allomancy:flakeSteel"), 2),new ItemStack(new Item().getByNameOrId("allomancy:flakeIron")), new ItemStack(Items.coal));
		GameRegistry.addShapelessRecipe(new ItemStack(new Item().getByNameOrId("allomancy:flakeBrass"), 2),new ItemStack(new Item().getByNameOrId("allomancy:flakeZinc")), new ItemStack(new Item().getByNameOrId("allomancy:flakeCopper")));
		GameRegistry.addShapelessRecipe(new ItemStack(new Item().getByNameOrId("allomancy:flakePewter"), 2),new ItemStack(new Item().getByNameOrId("allomancy:flakeTin")), new ItemStack(new Item().getByNameOrId("allomancy:flakeLead")));
		GameRegistry.addShapelessRecipe(new ItemStack(new Item().getByNameOrId("allomancy:flakeBronze"), 2),new ItemStack(new Item().getByNameOrId("allomancy:flakeCopper")), new ItemStack(new Item().getByNameOrId("allomancy:flakeTin")));
		
		//Add all the metal vial recipes
		for (int i = 1; i < ItemVial.unlocalName.length; i++) {
			GameRegistry.addShapelessRecipe(new ItemStack(itemVial, 1, i),new ItemStack(new Item().getByNameOrId("allomancy:" + "flake" + flakeMetals[i-1])), new ItemStack(itemVial, 1, 0));
		}	
		

		GameRegistry.addRecipe(new ItemStack(Mistcloak, 1), new Object[] {
				"W W", "WWW", "WWW", 'W', new ItemStack(Blocks.wool, 1, 7) });
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(itemVial, 3, 0), " x ", "y y", " y ", 'x',
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

}
