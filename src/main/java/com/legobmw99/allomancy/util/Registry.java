package com.legobmw99.allomancy.util;

import org.lwjgl.input.Keyboard;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.block.BlockIronLever;
import com.legobmw99.allomancy.entities.EntityGoldNugget;
import com.legobmw99.allomancy.entities.EntityIronNugget;
import com.legobmw99.allomancy.entities.EntityRenderFactories;
import com.legobmw99.allomancy.items.ItemCoinBag;
import com.legobmw99.allomancy.items.ItemGrinder;
import com.legobmw99.allomancy.items.ItemMistcloak;
import com.legobmw99.allomancy.items.ItemVial;
import com.legobmw99.allomancy.items.NuggetLerasium;
import com.legobmw99.allomancy.network.packets.AllomancyCapabilityPacket;
import com.legobmw99.allomancy.network.packets.AllomancyPowerPacket;
import com.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import com.legobmw99.allomancy.network.packets.GetCapabilitiesPacket;
import com.legobmw99.allomancy.network.packets.TryPushPullBlock;
import com.legobmw99.allomancy.network.packets.TryPushPullEntity;
import com.legobmw99.allomancy.network.packets.UpdateBurnPacket;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class Registry {
	public static KeyBinding burn;
	public static SimpleNetworkWrapper network;
	public static Item itemAllomancyGrinder;
	public static Item itemTinIngot;
	public static Item itemLeadIngot;
	public static Item itemCopperIngot;
	public static Item itemZincIngot;
	public static Item itemBronzeIngot;
	public static Item itemBrassIngot;
	public static Item itemAluminmIngot;
	public static Item itemCoinBag;
	public static Item Mistcloak;
	public static Item nuggetLerasium;
	public static Item itemVial;
	public static Block oreTin;
	public static Block oreLead;
	public static Block oreCopper;
	public static Block oreZinc;
	public static Block oreAluminum;
	public static Block blockIronLever;
	
	public static final String[] flakeMetals = { "Iron", "Steel", "Tin", "Pewter", "Zinc", "Brass", "Copper", "Bronze", "Aluminum", "Duralumin",
			"Lead" };
	
	
	public static CreativeTabs tabsAllomancy = new CreativeTabAllomancy(CreativeTabs.getNextID(), "allomancy");
	public static ArmorMaterial WoolArmor = net.minecraftforge.common.util.EnumHelper.addArmorMaterial("Wool",
			"allomancy:wool", 5, new int[] { 0, 4, 0, 0 }, 15, null, 0);

	
	public static void initBlocks(Register event) {
		oreTin = new Block(Material.ROCK).setHardness(.5F).setUnlocalizedName("oreTin").setCreativeTab(Registry.tabsAllomancy).setRegistryName(new ResourceLocation(Allomancy.MODID,"oreTin"));
		oreTin.setHarvestLevel("pickaxe", 1);
		oreLead = new Block(Material.ROCK).setHardness(.5F).setUnlocalizedName("oreLead").setCreativeTab(Registry.tabsAllomancy).setRegistryName(new ResourceLocation(Allomancy.MODID,"oreLead"));
		oreLead.setHarvestLevel("pickaxe", 1);
		oreCopper = new Block(Material.ROCK).setHardness(.5F).setUnlocalizedName("oreCopper").setCreativeTab(Registry.tabsAllomancy).setRegistryName(new ResourceLocation(Allomancy.MODID,"oreCopper"));
		oreCopper.setHarvestLevel("pickaxe", 1);
		oreZinc = new Block(Material.ROCK).setHardness(.5F).setUnlocalizedName("oreZinc").setCreativeTab(Registry.tabsAllomancy).setRegistryName(new ResourceLocation(Allomancy.MODID,"oreZinc"));
		oreZinc.setHarvestLevel("pickaxe", 1);
		oreAluminum = new Block(Material.ROCK).setHardness(.5F).setUnlocalizedName("oreAluminum").setCreativeTab(Registry.tabsAllomancy).setRegistryName(new ResourceLocation(Allomancy.MODID,"oreAluminum"));
		oreAluminum.setHarvestLevel("pickaxe", 1);
		blockIronLever = new BlockIronLever();
		
		event.getRegistry().registerAll(oreTin,oreLead,oreCopper,oreZinc, oreAluminum, blockIronLever);
	}

	public static void initItems(Register event) {
		// Register the basic, not-metallic items
		event.getRegistry().registerAll(
				itemAllomancyGrinder = new ItemGrinder(),
				itemCoinBag = new ItemCoinBag(),
				Mistcloak = new ItemMistcloak(WoolArmor, 1, EntityEquipmentSlot.CHEST),
				nuggetLerasium = new NuggetLerasium(),
				// Register ItemVial and its subtypes
				itemVial = new ItemVial(),
				// Register ingots and add them to the ore dictionary
				itemTinIngot = new Item().setUnlocalizedName("ingotTin").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0).setRegistryName(new ResourceLocation(Allomancy.MODID, "ingotTin")),
				itemLeadIngot = new Item().setUnlocalizedName("ingotLead").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0).setRegistryName(new ResourceLocation(Allomancy.MODID, "ingotLead")),		
				itemCopperIngot = new Item().setUnlocalizedName("ingotCopper").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0).setRegistryName(new ResourceLocation(Allomancy.MODID, "ingotCopper")),
				itemZincIngot = new Item().setUnlocalizedName("ingotZinc").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0).setRegistryName(new ResourceLocation(Allomancy.MODID, "ingotZinc")),
				itemBrassIngot = new Item().setUnlocalizedName("ingotBrass").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0).setRegistryName(new ResourceLocation(Allomancy.MODID, "ingotBrass")),
				itemBronzeIngot = new Item().setUnlocalizedName("ingotBronze").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0).setRegistryName(new ResourceLocation(Allomancy.MODID, "ingotBronze")),
				itemAluminmIngot = new Item().setUnlocalizedName("ingotAluminum").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0).setRegistryName(new ResourceLocation(Allomancy.MODID, "ingotAluminum"))
				);



		// Register flakes
		for (int i = 0; i < flakeMetals.length; i++) {
			event.getRegistry().register(new Item().setUnlocalizedName("flake" + flakeMetals[i]).setCreativeTab(Registry.tabsAllomancy).setRegistryName(new ResourceLocation(Allomancy.MODID, "flake" + flakeMetals[i])));
		}
		
		//Register ore block items
		event.getRegistry().registerAll(
				new ItemBlock(oreTin).setRegistryName(oreTin.getRegistryName()),
				new ItemBlock(oreLead).setRegistryName(oreLead.getRegistryName()),
				new ItemBlock(oreCopper).setRegistryName(oreCopper.getRegistryName()),
				new ItemBlock(oreZinc).setRegistryName(oreZinc.getRegistryName()),
				new ItemBlock(oreAluminum).setRegistryName(oreAluminum.getRegistryName()),
				new ItemBlock(blockIronLever).setRegistryName(blockIronLever.getRegistryName())
				);
		
				
		OreDictionary.registerOre("ingotZinc", itemZincIngot);
		OreDictionary.registerOre("ingotTin", itemTinIngot);
		OreDictionary.registerOre("ingotCopper", itemCopperIngot);
		OreDictionary.registerOre("ingotLead", itemLeadIngot);
		OreDictionary.registerOre("ingotBronze", itemBronzeIngot);
		OreDictionary.registerOre("ingotBrass", itemBrassIngot);
		OreDictionary.registerOre("ingotAluminum", itemAluminmIngot);
		OreDictionary.registerOre("oreZinc", oreZinc);
		OreDictionary.registerOre("oreTin", oreTin);
		OreDictionary.registerOre("oreCopper", oreCopper);
		OreDictionary.registerOre("oreLead", oreLead);
		OreDictionary.registerOre("oreAluminum", oreAluminum);
	}

	public static void initKeyBindings() {
		burn = new KeyBinding("key.burn", Keyboard.KEY_F, "key.categories.allomancy");
		ClientRegistry.registerKeyBinding(burn);
	}

	public static void registerPackets() {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("allomancy");
		network.registerMessage(AllomancyPowerPacket.Handler.class, AllomancyPowerPacket.class, 0, Side.CLIENT);
		network.registerMessage(UpdateBurnPacket.Handler.class, UpdateBurnPacket.class, 1, Side.SERVER);
		network.registerMessage(AllomancyCapabilityPacket.Handler.class, AllomancyCapabilityPacket.class, 2,Side.CLIENT);
		network.registerMessage(ChangeEmotionPacket.Handler.class, ChangeEmotionPacket.class, 3, Side.SERVER);
		network.registerMessage(GetCapabilitiesPacket.Handler.class, GetCapabilitiesPacket.class, 4, Side.SERVER);
		network.registerMessage(TryPushPullEntity.Handler.class, TryPushPullEntity.class, 5, Side.SERVER);
		network.registerMessage(TryPushPullBlock.Handler.class, TryPushPullBlock.class, 6, Side.SERVER);
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerEntityRenders(){
		//Use renderSnowball for nugget projectiles
		RenderingRegistry.registerEntityRenderingHandler(EntityGoldNugget.class,EntityRenderFactories.GOLD_FACTORY);
		RenderingRegistry.registerEntityRenderingHandler(EntityIronNugget.class,EntityRenderFactories.IRON_FACTORY);
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemRenders() {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		// Register ore models individually.
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreTin), 0,
				new ModelResourceLocation("allomancy:oreTin", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreZinc), 0,
				new ModelResourceLocation("allomancy:oreZinc", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreCopper), 0,
				new ModelResourceLocation("allomancy:oreCopper", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreLead), 0,
				new ModelResourceLocation("allomancy:oreLead", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreAluminum), 0,
				new ModelResourceLocation("allomancy:oreAluminum", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(blockIronLever), 0,
				new ModelResourceLocation("allomancy:iron_lever", "inventory"));

		// Register ingot models individually
		renderItem.getItemModelMesher().register(itemZincIngot, 0,
				new ModelResourceLocation("allomancy:ingotZinc", "inventory"));
		renderItem.getItemModelMesher().register(itemLeadIngot, 0,
				new ModelResourceLocation("allomancy:ingotLead", "inventory"));
		renderItem.getItemModelMesher().register(itemCopperIngot, 0,
				new ModelResourceLocation("allomancy:ingotCopper", "inventory"));
		renderItem.getItemModelMesher().register(itemTinIngot, 0,
				new ModelResourceLocation("allomancy:ingotTin", "inventory"));
		renderItem.getItemModelMesher().register(itemBrassIngot, 0,
				new ModelResourceLocation("allomancy:ingotBrass", "inventory"));
		renderItem.getItemModelMesher().register(itemBronzeIngot, 0,
				new ModelResourceLocation("allomancy:ingotBronze", "inventory"));
		renderItem.getItemModelMesher().register(itemAluminmIngot, 0,
				new ModelResourceLocation("allomancy:ingotAluminum", "inventory"));

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
		
		renderItem.getItemModelMesher().register(itemVial, 0,
				new ModelResourceLocation("allomancy:itemVial", "inventory"));
	}
	
	//only does furnace recipes, rest are handled in JSON
	public static void setupRecipes(Register event) {
		event.getRegistry().register(new RecipeItemVial());
		GameRegistry.addSmelting(oreTin, new ItemStack(itemTinIngot, 1), 5);
		GameRegistry.addSmelting(oreCopper, new ItemStack(itemCopperIngot, 1), 5);
		GameRegistry.addSmelting(oreLead, new ItemStack(itemLeadIngot, 1), 5);
		GameRegistry.addSmelting(oreZinc, new ItemStack(itemZincIngot, 1), 5);
		GameRegistry.addSmelting(oreAluminum, new ItemStack(itemAluminmIngot, 1), 5);
	}
}
