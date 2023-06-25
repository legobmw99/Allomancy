package com.legobmw99.allomancy.modules.materials;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.world.LootTableInjector;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class MaterialsSetup {

    public static final String[] ORE_METALS = {"aluminum", "cadmium", "chromium", "lead", "silver", "tin", "zinc"};

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Allomancy.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Allomancy.MODID);

    public static final List<RegistryObject<Item>> FLAKES = new ArrayList<>();
    public static final List<RegistryObject<Item>> NUGGETS = new ArrayList<>();
    public static final List<RegistryObject<Item>> INGOTS = new ArrayList<>();
    public static final List<RegistryObject<Block>> STORAGE_BLOCKS = new ArrayList<>();
    public static final List<RegistryObject<Item>> STORAGE_BLOCK_ITEMS = new ArrayList<>();


    public static final List<RegistryObject<Block>> ORE_BLOCKS = new ArrayList<>();
    public static final List<RegistryObject<Item>> ORE_BLOCKS_ITEMS = new ArrayList<>();
    public static final List<RegistryObject<Block>> DEEPSLATE_ORE_BLOCKS = new ArrayList<>();
    public static final List<RegistryObject<Item>> DEEPSLATE_ORE_BLOCKS_ITEMS = new ArrayList<>();
    public static final List<RegistryObject<Block>> RAW_ORE_BLOCKS = new ArrayList<>();
    public static final List<RegistryObject<Item>> RAW_ORE_BLOCKS_ITEMS = new ArrayList<>();
    public static final List<RegistryObject<Item>> RAW_ORE_ITEMS = new ArrayList<>();

    public static int METAL_ITEM_LEN = Metal.values().length;
    public static final int LEAD = METAL_ITEM_LEN++;
    public static final int SILVER = METAL_ITEM_LEN++;

    static {
        for (Metal mt : Metal.values()) {
            String name = mt.getName();
            FLAKES.add(MaterialsSetup.ITEMS.register(name + "_flakes", MaterialsSetup::createStandardItem));

            if (mt.isVanilla()) {
                NUGGETS.add(null);
                INGOTS.add(null);
                STORAGE_BLOCKS.add(null);
                STORAGE_BLOCK_ITEMS.add(null);
            } else {
                NUGGETS.add(ITEMS.register(name + "_nugget", MaterialsSetup::createStandardItem));
                INGOTS.add(ITEMS.register(name + "_ingot", MaterialsSetup::createStandardItem));
                STORAGE_BLOCKS.add(BLOCKS.register(name + "_block", MaterialsSetup::createStandardBlock));
                STORAGE_BLOCK_ITEMS.add(ITEMS.register(name + "_block", () -> new BlockItem(STORAGE_BLOCKS.get(mt.getIndex()).get(), new Item.Properties())));
            }
        }
        FLAKES.add(MaterialsSetup.ITEMS.register("lead_flakes", MaterialsSetup::createStandardItem));
        NUGGETS.add(ITEMS.register("lead_nugget", MaterialsSetup::createStandardItem));
        INGOTS.add(ITEMS.register("lead_ingot", MaterialsSetup::createStandardItem));
        STORAGE_BLOCKS.add(BLOCKS.register("lead_block", MaterialsSetup::createStandardBlock));
        STORAGE_BLOCK_ITEMS.add(ITEMS.register("lead_block", () -> new BlockItem(STORAGE_BLOCKS.get(LEAD).get(), new Item.Properties())));

        FLAKES.add(MaterialsSetup.ITEMS.register("silver_flakes", MaterialsSetup::createStandardItem));
        NUGGETS.add(ITEMS.register("silver_nugget", MaterialsSetup::createStandardItem));
        INGOTS.add(ITEMS.register("silver_ingot", MaterialsSetup::createStandardItem));
        STORAGE_BLOCKS.add(BLOCKS.register("silver_block", MaterialsSetup::createStandardBlock));
        STORAGE_BLOCK_ITEMS.add(ITEMS.register("silver_block", () -> new BlockItem(STORAGE_BLOCKS.get(SILVER).get(), new Item.Properties())));

        for (String ore : ORE_METALS) {
            var ore_block = BLOCKS.register(ore + "_ore", MaterialsSetup::createStandardOre);
            ORE_BLOCKS.add(ore_block);
            ORE_BLOCKS_ITEMS.add(ITEMS.register(ore + "_ore", () -> new BlockItem(ore_block.get(), new Item.Properties())));

            var ds_ore_block = BLOCKS.register("deepslate_" + ore + "_ore", MaterialsSetup::createDeepslateBlock);
            DEEPSLATE_ORE_BLOCKS.add(ds_ore_block);
            DEEPSLATE_ORE_BLOCKS_ITEMS.add(ITEMS.register("deepslate_" + ore + "_ore", () -> new BlockItem(ds_ore_block.get(), new Item.Properties())));

            var raw_ore_block = BLOCKS.register("raw_" + ore + "_block", MaterialsSetup::createStandardBlock);
            RAW_ORE_BLOCKS.add(raw_ore_block);
            RAW_ORE_BLOCKS_ITEMS.add(ITEMS.register("raw_" + ore + "_block", () -> new BlockItem(raw_ore_block.get(), new Item.Properties())));

            RAW_ORE_ITEMS.add(ITEMS.register("raw_" + ore, MaterialsSetup::createStandardItem));
        }
    }


    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void init(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            MinecraftForge.EVENT_BUS.register(LootTableInjector.class);
        });
    }

    public static Block createStandardBlock() {
        return new Block(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2.1F).requiresCorrectToolForDrops());
    }

    public static Block createStandardOre() {
        return new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE));
    }

    public static Block createDeepslateBlock() {
        return new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE).strength(4.5F, 3.0F));
    }

    public static Item createStandardItem() {
        return new Item(new Item.Properties());
    }
}

