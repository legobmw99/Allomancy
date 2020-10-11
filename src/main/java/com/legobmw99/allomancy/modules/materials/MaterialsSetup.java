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
    private static int id = Metal.values().length;
    public static final int LEAD = id++;
    public static final int SILVER = id++;

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Allomancy.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Allomancy.MODID);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }


    public static void init(final FMLCommonSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new LootTableInjector());
    }


    public static final List<RegistryObject<Item>> FLAKES = new ArrayList<>();

    static {
        for (Metal mt : Metal.values()) {
            String name = mt.getName() + "_flakes";
            FLAKES.add(MaterialsSetup.ITEMS.register(name, AllomancySetup::createStandardItem));
        }
        FLAKES.add(MaterialsSetup.ITEMS.register("lead_flakes", AllomancySetup::createStandardItem));
        FLAKES.add(MaterialsSetup.ITEMS.register("silver_flakes", AllomancySetup::createStandardItem));
    }

    public static final RegistryObject<Block> ALUMINUM_ORE = BLOCKS.register("aluminum_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> ALUMINUM_ORE_ITEM = ITEMS.register("aluminum_ore", () -> new BlockItem(ALUMINUM_ORE.get(), AllomancySetup.createStandardItemProperties()));
    public static final RegistryObject<Block> CADMIUM_ORE = BLOCKS.register("cadmium_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> CADMIUM_ORE_ITEM = ITEMS.register("cadmium_ore", () -> new BlockItem(CADMIUM_ORE.get(), AllomancySetup.createStandardItemProperties()));
    public static final RegistryObject<Block> CHROMIUM_ORE = BLOCKS.register("chromium_ore", AllomancySetup::createStandardBlock);
    public static final RegistryObject<Item> CHROMIUM_ORE_ITEM = ITEMS.register("chromium_ore", () -> new BlockItem(CHROMIUM_ORE.get(), AllomancySetup.createStandardItemProperties()));
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


    public static final RegistryObject<Item> ALUMINUM_INGOT = ITEMS.register("aluminum_ingot", AllomancySetup::createStandardItem);
    public static final RegistryObject<Item> CADMIUM_INGOT = ITEMS.register("cadmium_ingot", AllomancySetup::createStandardItem);
    public static final RegistryObject<Item> CHROMIUM_INGOT = ITEMS.register("chromium_ingot", AllomancySetup::createStandardItem);
    public static final RegistryObject<Item> COPPER_INGOT = ITEMS.register("copper_ingot", AllomancySetup::createStandardItem);
    public static final RegistryObject<Item> LEAD_INGOT = ITEMS.register("lead_ingot", AllomancySetup::createStandardItem);
    public static final RegistryObject<Item> SILVER_INGOT = ITEMS.register("silver_ingot", AllomancySetup::createStandardItem);
    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot", AllomancySetup::createStandardItem);
    public static final RegistryObject<Item> ZINC_INGOT = ITEMS.register("zinc_ingot", AllomancySetup::createStandardItem);
    public static final RegistryObject<Item> BRASS_INGOT = ITEMS.register("brass_ingot", AllomancySetup::createStandardItem);
    public static final RegistryObject<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot", AllomancySetup::createStandardItem);


}

