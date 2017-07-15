package com.legobmw99.allomancy.util;

import org.lwjgl.input.Keyboard;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.entities.EntityGoldNugget;
import com.legobmw99.allomancy.entities.EntityIronNugget;
import com.legobmw99.allomancy.items.ItemCoinBag;
import com.legobmw99.allomancy.items.ItemGrinder;
import com.legobmw99.allomancy.items.ItemMistcloak;
import com.legobmw99.allomancy.items.ItemVial;
import com.legobmw99.allomancy.items.NuggetLerasium;
import com.legobmw99.allomancy.network.packets.AllomancyCapabiltiesPacket;
import com.legobmw99.allomancy.network.packets.AllomancyPowerPacket;
import com.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import com.legobmw99.allomancy.network.packets.GetCapabilitiesPacket;
import com.legobmw99.allomancy.network.packets.MovePlayerPacket;
import com.legobmw99.allomancy.network.packets.TryPushPullBlock;
import com.legobmw99.allomancy.network.packets.TryPushPullEntity;
import com.legobmw99.allomancy.network.packets.UpdateBurnPacket;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
	public static ItemGrinder itemAllomancyGrinder;
	public static Item itemTinIngot;
	public static Item itemLeadIngot;
	public static Item itemCopperIngot;
	public static Item itemZincIngot;
	public static Item itemCoinBag;
	public static Item Mistcloak;
	public static Item nuggetLerasium;
	public static ItemVial itemVial;
	public static Block oreTin;
	public static Block oreLead;
	public static Block oreCopper;
	public static Block oreZinc;
	public static final String[] flakeMetals = { "Iron", "Steel", "Tin", "Pewter", "Zinc", "Brass", "Copper", "Bronze",
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
		
		event.getRegistry().registerAll(oreTin,oreLead,oreCopper,oreZinc);
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
				itemZincIngot = new Item().setUnlocalizedName("ingotZinc").setCreativeTab(Registry.tabsAllomancy).setMaxDamage(0).setRegistryName(new ResourceLocation(Allomancy.MODID, "ingotZinc"))
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
				new ItemBlock(oreZinc).setRegistryName(oreZinc.getRegistryName())
				);
		
				
		OreDictionary.registerOre("ingotZinc", itemZincIngot);
		OreDictionary.registerOre("ingotTin", itemTinIngot);
		OreDictionary.registerOre("ingotCopper", itemCopperIngot);
		OreDictionary.registerOre("ingotLead", itemLeadIngot);
		OreDictionary.registerOre("oreZinc", oreZinc);
		OreDictionary.registerOre("oreTin", oreTin);
		OreDictionary.registerOre("oreCopper", oreCopper);
		OreDictionary.registerOre("oreLead", oreLead);
	}

	public static void initKeyBindings() {
		burn = new KeyBinding("key.burn", Keyboard.KEY_F, "key.categories.allomancy");

		ClientRegistry.registerKeyBinding(burn);
	}

	public static void registerPackets() {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("allomancy");
		network.registerMessage(AllomancyPowerPacket.Handler.class, AllomancyPowerPacket.class, 0, Side.CLIENT);
		network.registerMessage(UpdateBurnPacket.Handler.class, UpdateBurnPacket.class, 1, Side.SERVER);
		network.registerMessage(AllomancyCapabiltiesPacket.Handler.class, AllomancyCapabiltiesPacket.class, 2,Side.CLIENT);
		network.registerMessage(ChangeEmotionPacket.Handler.class, ChangeEmotionPacket.class, 3, Side.SERVER);
		network.registerMessage(GetCapabilitiesPacket.Handler.class, GetCapabilitiesPacket.class, 4, Side.SERVER);
		network.registerMessage(TryPushPullEntity.Handler.class, TryPushPullEntity.class, 5, Side.SERVER);
		network.registerMessage(TryPushPullBlock.Handler.class, TryPushPullBlock.class, 6, Side.SERVER);
		network.registerMessage(MovePlayerPacket.Handler.class, MovePlayerPacket.class, 7, Side.CLIENT);


	}

	@SideOnly(Side.CLIENT)
	public static void registerRenders() {
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

		// Call on rendersnowball for nugget projectiles
		RenderingRegistry.registerEntityRenderingHandler(EntityGoldNugget.class,
				new RenderSnowball((Minecraft.getMinecraft().getRenderManager()), Items.GOLD_NUGGET, renderItem));
		
	      RenderingRegistry.registerEntityRenderingHandler(EntityIronNugget.class,
	                new RenderSnowball((Minecraft.getMinecraft().getRenderManager()), Items.IRON_NUGGET, renderItem));

		// Register ore models individually.
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreTin), 0,
				new ModelResourceLocation("allomancy:oreTin", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreZinc), 0,
				new ModelResourceLocation("allomancy:oreZinc", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreCopper), 0,
				new ModelResourceLocation("allomancy:oreCopper", "inventory"));
		renderItem.getItemModelMesher().register(Item.getItemFromBlock(oreLead), 0,
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
		
		//Hacky, TODO: investigate other solutions to the vials-not-loading problem
		Minecraft.getMinecraft().refreshResources();
	}
	
	//only does furnace recipes, rest are handled in JSON
	public static void setupRecipes() {
		GameRegistry.addSmelting(oreTin, new ItemStack(itemTinIngot, 1), 5);
		GameRegistry.addSmelting(oreCopper, new ItemStack(itemCopperIngot, 1), 5);
		GameRegistry.addSmelting(oreLead, new ItemStack(itemLeadIngot, 1), 5);
		GameRegistry.addSmelting(oreZinc, new ItemStack(itemZincIngot, 1), 5);
	}

}
