package com.legobmw99.allomancy.modules.materials;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.world.LootTableInjector;
import com.legobmw99.allomancy.modules.materials.world.OreGenerator;
import com.legobmw99.allomancy.setup.AllomancySetup;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class MaterialsSetup {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Allomancy.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Allomancy.MODID);

    public static final List<RegistryObject<Item>> FLAKES = new ArrayList<>();
    public static final List<RegistryObject<Item>> NUGGETS = new ArrayList<>();
    public static final List<RegistryObject<Item>> INGOTS = new ArrayList<>();
    public static final List<RegistryObject<Block>> STORAGE_BLOCKS = new ArrayList<>();
    public static final List<RegistryObject<Item>> STORAGE_BLOCK_ITEMS = new ArrayList<>();

    public static final RegistryObject<Block> ALUMINUM_ORE = BLOCKS.register("aluminum_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> ALUMINUM_ORE_ITEM = ITEMS.register("aluminum_ore",
                                                                                () -> new BlockItem(ALUMINUM_ORE.get(), AllomancySetup.createStandardItemProperties()));
    public static final RegistryObject<Block> CADMIUM_ORE = BLOCKS.register("cadmium_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> CADMIUM_ORE_ITEM = ITEMS.register("cadmium_ore",
                                                                               () -> new BlockItem(CADMIUM_ORE.get(), AllomancySetup.createStandardItemProperties()));
    public static final RegistryObject<Block> CHROMIUM_ORE = BLOCKS.register("chromium_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> CHROMIUM_ORE_ITEM = ITEMS.register("chromium_ore",
                                                                                () -> new BlockItem(CHROMIUM_ORE.get(), AllomancySetup.createStandardItemProperties()));
    public static final RegistryObject<Block> COPPER_ORE = BLOCKS.register("copper_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> COPPER_ORE_ITEM = ITEMS.register("copper_ore", () -> new BlockItem(COPPER_ORE.get(), AllomancySetup.createStandardItemProperties()));
    public static final RegistryObject<Block> LEAD_ORE = BLOCKS.register("lead_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> LEAD_ORE_ITEM = ITEMS.register("lead_ore", () -> new BlockItem(LEAD_ORE.get(), AllomancySetup.createStandardItemProperties()));
    public static final RegistryObject<Block> SILVER_ORE = BLOCKS.register("silver_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> SILVER_ORE_ITEM = ITEMS.register("silver_ore", () -> new BlockItem(SILVER_ORE.get(), AllomancySetup.createStandardItemProperties()));
    public static final RegistryObject<Block> TIN_ORE = BLOCKS.register("tin_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> TIN_ORE_ITEM = ITEMS.register("tin_ore", () -> new BlockItem(TIN_ORE.get(), AllomancySetup.createStandardItemProperties()));
    public static final RegistryObject<Block> ZINC_ORE = BLOCKS.register("zinc_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> ZINC_ORE_ITEM = ITEMS.register("zinc_ore", () -> new BlockItem(ZINC_ORE.get(), AllomancySetup.createStandardItemProperties()));

    public static int METAL_ITEM_LEN = Metal.values().length;
    public static final int LEAD = METAL_ITEM_LEN++;
    public static final int SILVER = METAL_ITEM_LEN++;

    static {
        for (Metal mt : Metal.values()) {
            String name = mt.getName();
            FLAKES.add(MaterialsSetup.ITEMS.register(name + "_flakes", AllomancySetup::createStandardItem));

            if (mt == Metal.GOLD || mt == Metal.IRON) {
                NUGGETS.add(null);
                INGOTS.add(null);
                STORAGE_BLOCKS.add(null);
                STORAGE_BLOCK_ITEMS.add(null);
            } else {
                NUGGETS.add(ITEMS.register(name + "_nugget", AllomancySetup::createStandardItem));
                INGOTS.add(ITEMS.register(name + "_ingot", AllomancySetup::createStandardItem));
                STORAGE_BLOCKS.add(BLOCKS.register(name + "_block", AllomancySetup::createStandardBlock));
                STORAGE_BLOCK_ITEMS.add(
                        ITEMS.register(name + "_block", () -> new BlockItem(STORAGE_BLOCKS.get(mt.getIndex()).get(), AllomancySetup.createStandardItemProperties())));
            }
        }
        FLAKES.add(MaterialsSetup.ITEMS.register("lead_flakes", AllomancySetup::createStandardItem));
        NUGGETS.add(ITEMS.register("lead_nugget", AllomancySetup::createStandardItem));
        INGOTS.add(ITEMS.register("lead_ingot", AllomancySetup::createStandardItem));
        STORAGE_BLOCKS.add(BLOCKS.register("lead_block", AllomancySetup::createStandardBlock));
        STORAGE_BLOCK_ITEMS.add(ITEMS.register("lead_block", () -> new BlockItem(STORAGE_BLOCKS.get(LEAD).get(), AllomancySetup.createStandardItemProperties())));

        FLAKES.add(MaterialsSetup.ITEMS.register("silver_flakes", AllomancySetup::createStandardItem));
        NUGGETS.add(ITEMS.register("silver_nugget", AllomancySetup::createStandardItem));
        INGOTS.add(ITEMS.register("silver_ingot", AllomancySetup::createStandardItem));
        STORAGE_BLOCKS.add(BLOCKS.register("silver_block", AllomancySetup::createStandardBlock));
        STORAGE_BLOCK_ITEMS.add(ITEMS.register("silver_block", () -> new BlockItem(STORAGE_BLOCKS.get(SILVER).get(), AllomancySetup.createStandardItemProperties())));

    }


    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void init(final FMLCommonSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(LootTableInjector.class);
        OreGenerator.registerFeatures();
    }


}

