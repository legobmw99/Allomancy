package com.legobmw99.allomancy.modules.materials;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.world.LootTableInjector;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class MaterialsSetup {

    public record OreConfig(String name, int size, int placementCount, int minHeight, int maxHeight) {

        <T> ResourceKey<T> getRegistryKey(ResourceKey<Registry<T>> registry, String suffix) {
            return ResourceKey.create(registry, new ResourceLocation(Allomancy.MODID, this.name + suffix));
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static final OreConfig[] ORE_METALS = {new OreConfig("aluminum", 9, 14, 40, 120), new OreConfig("cadmium", 7, 5, -60, 0), new OreConfig("chromium", 6, 8, -30, 30),
                                                  new OreConfig("lead", 9, 15, -40, 30), new OreConfig("silver", 7, 11, -40, 30), new OreConfig("tin", 11, 15, 30, 112),
                                                  new OreConfig("zinc", 8, 12, 40, 80)};

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allomancy.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);

    public static final List<DeferredItem<Item>> FLAKES = new ArrayList<>();
    public static final List<DeferredItem<Item>> NUGGETS = new ArrayList<>();
    public static final List<DeferredItem<Item>> INGOTS = new ArrayList<>();
    public static final List<DeferredBlock<Block>> STORAGE_BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<Item>> STORAGE_BLOCK_ITEMS = new ArrayList<>();


    public static final List<DeferredBlock<Block>> ORE_BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<Item>> ORE_BLOCKS_ITEMS = new ArrayList<>();
    public static final List<DeferredBlock<Block>> DEEPSLATE_ORE_BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<Item>> DEEPSLATE_ORE_BLOCKS_ITEMS = new ArrayList<>();
    public static final List<DeferredBlock<Block>> RAW_ORE_BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<Item>> RAW_ORE_BLOCKS_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<Item>> RAW_ORE_ITEMS = new ArrayList<>();

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

        for (var ore_config : ORE_METALS) {
            String ore = ore_config.name();
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


    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    public static void init(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            NeoForge.EVENT_BUS.register(LootTableInjector.class);
        });
    }

    public static Block createStandardBlock() {
        return new Block(Blocks.STONE.properties().strength(2.1F).requiresCorrectToolForDrops());
    }

    public static Block createStandardOre() {
        return new DropExperienceBlock(UniformInt.of(2, 5), Blocks.IRON_ORE.properties());
    }

    public static Block createDeepslateBlock() {
        return new DropExperienceBlock(UniformInt.of(2, 5), Blocks.DEEPSLATE_IRON_ORE.properties().strength(4.5F, 3.0F));
    }

    public static Item createStandardItem() {
        return new Item(new Item.Properties());
    }

    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> bootstrap) {
        RuleTest stone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslate = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        for (int i = 0; i < ORE_METALS.length; i++) {
            OreConfig ore = ORE_METALS[i];
            var ore_block = ORE_BLOCKS.get(i);
            var deepslate_ore_block = DEEPSLATE_ORE_BLOCKS.get(i);

            bootstrap.register(ore.getRegistryKey(Registries.CONFIGURED_FEATURE, "_ore_feature"), new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
                    List.of(OreConfiguration.target(stone, ore_block.get().defaultBlockState()), OreConfiguration.target(deepslate, deepslate_ore_block.get().defaultBlockState())),
                    ore.size(), 0.0f)));
        }
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> bootstrap) {

        for (int i = 0; i < ORE_METALS.length; i++) {
            OreConfig ore = ORE_METALS[i];
            var ore_block = ORE_BLOCKS.get(i);
            var deepslate_ore_block = DEEPSLATE_ORE_BLOCKS.get(i);

            // Get configured feature registry
            HolderGetter<ConfiguredFeature<?, ?>> configured = bootstrap.lookup(Registries.CONFIGURED_FEATURE);

            bootstrap.register(ore.getRegistryKey(Registries.PLACED_FEATURE, "_ore"),
                               new PlacedFeature(configured.getOrThrow(ore.getRegistryKey(Registries.CONFIGURED_FEATURE, "_ore_feature")),
                                                 List.of(CountPlacement.of(ore.placementCount), InSquarePlacement.spread(),
                                                         HeightRangePlacement.triangle(VerticalAnchor.absolute(ore.minHeight), VerticalAnchor.absolute(ore.maxHeight)),
                                                         BiomeFilter.biome())));
        }
    }
}

