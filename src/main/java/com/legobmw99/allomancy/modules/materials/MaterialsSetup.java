package com.legobmw99.allomancy.modules.materials;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.world.DaggerLootModifier;
import com.legobmw99.allomancy.modules.materials.world.LerasiumLootModifier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class MaterialsSetup {

    private MaterialsSetup() {}

    public record OreConfig(String name, int size, int placementCount, int minHeight, int maxHeight) {

        <T> ResourceKey<T> getRegistryKey(ResourceKey<Registry<T>> registry, String suffix) {
            return ResourceKey.create(registry,
                                      ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, this.name + suffix));
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static final OreConfig[] ORE_METALS =
            {new OreConfig("aluminum", 9, 14, 40, 120), new OreConfig("cadmium", 7, 5, -60, 0),
             new OreConfig("chromium", 6, 8, -30, 30), new OreConfig("lead", 9, 15, -40, 30),
             new OreConfig("silver", 7, 11, -40, 30), new OreConfig("tin", 11, 15, 30, 112),
             new OreConfig("zinc", 8, 12, 40, 80)};

    private static final ResourceKey<BiomeModifier> ADD_ALLOMANCY_ORES =
            ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                               ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, "overworld_ores"));

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allomancy.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);

    public static final List<DeferredItem<Item>> FLAKES = new ArrayList<>();
    public static final List<DeferredItem<Item>> NUGGETS = new ArrayList<>();
    public static final List<DeferredItem<Item>> INGOTS = new ArrayList<>();
    public static final List<DeferredBlock<Block>> STORAGE_BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> STORAGE_BLOCK_ITEMS = new ArrayList<>();


    public static final List<DeferredBlock<Block>> ORE_BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> ORE_BLOCKS_ITEMS = new ArrayList<>();
    public static final List<DeferredBlock<Block>> DEEPSLATE_ORE_BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> DEEPSLATE_ORE_BLOCKS_ITEMS = new ArrayList<>();
    public static final List<DeferredBlock<Block>> RAW_ORE_BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> RAW_ORE_BLOCKS_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<Item>> RAW_ORE_ITEMS = new ArrayList<>();

    public static int METAL_ITEM_LEN = Metal.values().length;
    public static final int LEAD = METAL_ITEM_LEN++;
    public static final int SILVER = METAL_ITEM_LEN++;

    static {
        for (Metal mt : Metal.values()) {
            String name = mt.getName();
            FLAKES.add(ITEMS.registerSimpleItem(name + "_flakes"));

            if (mt.isVanilla()) {
                NUGGETS.add(null);
                INGOTS.add(null);
                STORAGE_BLOCKS.add(null);
                STORAGE_BLOCK_ITEMS.add(null);
            } else {
                NUGGETS.add(ITEMS.registerSimpleItem(name + "_nugget"));
                INGOTS.add(ITEMS.registerSimpleItem(name + "_ingot"));
                STORAGE_BLOCKS.add(registerStandardBlock(name + "_block"));
                STORAGE_BLOCK_ITEMS.add(ITEMS.registerSimpleBlockItem(STORAGE_BLOCKS.get(mt.getIndex())));
            }
        }
        FLAKES.add(ITEMS.registerSimpleItem("lead_flakes"));
        NUGGETS.add(ITEMS.registerSimpleItem("lead_nugget"));
        INGOTS.add(ITEMS.registerSimpleItem("lead_ingot"));
        STORAGE_BLOCKS.add(registerStandardBlock("lead_block"));
        STORAGE_BLOCK_ITEMS.add(ITEMS.registerSimpleBlockItem(STORAGE_BLOCKS.get(LEAD)));

        FLAKES.add(ITEMS.registerSimpleItem("silver_flakes"));
        NUGGETS.add(ITEMS.registerSimpleItem("silver_nugget"));
        INGOTS.add(ITEMS.registerSimpleItem("silver_ingot"));
        STORAGE_BLOCKS.add(registerStandardBlock("silver_block"));
        STORAGE_BLOCK_ITEMS.add(ITEMS.registerSimpleBlockItem(STORAGE_BLOCKS.get(SILVER)));

        for (var ore_config : ORE_METALS) {
            String ore = ore_config.name();
            var ore_block = registerStandardOre(ore + "_ore");
            ORE_BLOCKS.add(ore_block);
            ORE_BLOCKS_ITEMS.add(ITEMS.registerSimpleBlockItem(ore_block));

            var ds_ore_block = registerDeepslateOre("deepslate_" + ore + "_ore");
            DEEPSLATE_ORE_BLOCKS.add(ds_ore_block);
            DEEPSLATE_ORE_BLOCKS_ITEMS.add(ITEMS.registerSimpleBlockItem((ds_ore_block)));

            var raw_ore_block = registerStandardBlock("raw_" + ore + "_block");
            RAW_ORE_BLOCKS.add(raw_ore_block);
            RAW_ORE_BLOCKS_ITEMS.add(ITEMS.registerSimpleBlockItem(raw_ore_block));

            RAW_ORE_ITEMS.add(ITEMS.registerSimpleItem("raw_" + ore));
        }
    }

    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Allomancy.MODID);
    public static final Supplier<MapCodec<LerasiumLootModifier>> LERASIUM_LOOT =
            GLM.register("lerasium_loot", LerasiumLootModifier.CODEC);
    public static final Supplier<MapCodec<DaggerLootModifier>> DAGGER_LOOT =
            GLM.register("unbreakable_dagger_loot", DaggerLootModifier.CODEC);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        GLM.register(bus);
    }

    private static DeferredBlock<Block> registerStandardBlock(String name) {
        return BLOCKS.registerBlock(name, Block::new,
                                    Blocks.STONE.properties().strength(2.1F).requiresCorrectToolForDrops());
    }

    private static DeferredBlock<Block> registerStandardOre(String name) {
        return BLOCKS.registerBlock(name, props -> new DropExperienceBlock(UniformInt.of(2, 5), props),
                                    Blocks.IRON_ORE.properties());
    }

    private static DeferredBlock<Block> registerDeepslateOre(String name) {
        return BLOCKS.registerBlock(name, props -> new DropExperienceBlock(UniformInt.of(2, 5), props),
                                    Blocks.DEEPSLATE_IRON_ORE.properties().strength(4.5F, 3.0F));
    }


    public static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> bootstrap) {
        RuleTest stone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslate = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        for (int i = 0; i < ORE_METALS.length; i++) {
            OreConfig ore = ORE_METALS[i];
            var ore_block = ORE_BLOCKS.get(i);
            var deepslate_ore_block = DEEPSLATE_ORE_BLOCKS.get(i);

            bootstrap.register(ore.getRegistryKey(Registries.CONFIGURED_FEATURE, "_ore_feature"),
                               new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
                                       List.of(OreConfiguration.target(stone, ore_block.get().defaultBlockState()),
                                               OreConfiguration.target(deepslate, deepslate_ore_block
                                                       .get()
                                                       .defaultBlockState())), ore.size(), 0.0f)));
        }
    }

    public static void bootstrapPlaced(BootstrapContext<PlacedFeature> bootstrap) {

        for (OreConfig ore : ORE_METALS) {
            // Get configured feature registry
            HolderGetter<ConfiguredFeature<?, ?>> configured = bootstrap.lookup(Registries.CONFIGURED_FEATURE);

            bootstrap.register(ore.getRegistryKey(Registries.PLACED_FEATURE, "_ore"), new PlacedFeature(
                    configured.getOrThrow(ore.getRegistryKey(Registries.CONFIGURED_FEATURE, "_ore_feature")),
                    List.of(CountPlacement.of(ore.placementCount), InSquarePlacement.spread(),
                            HeightRangePlacement.triangle(VerticalAnchor.absolute(ore.minHeight),
                                                          VerticalAnchor.absolute(ore.maxHeight)),
                            BiomeFilter.biome())));
        }
    }

    public static void bootstrapBiomeModifier(BootstrapContext<BiomeModifier> bootstrap) {
        var overworldTag = bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD);
        HolderGetter<PlacedFeature> placed = bootstrap.lookup(Registries.PLACED_FEATURE);

        List<Holder<PlacedFeature>> ores = new ArrayList<>();
        for (OreConfig ore : ORE_METALS) {
            ores.add(placed.getOrThrow(ore.getRegistryKey(Registries.PLACED_FEATURE, "_ore")));
        }
        bootstrap.register(ADD_ALLOMANCY_ORES,
                           new BiomeModifiers.AddFeaturesBiomeModifier(overworldTag, HolderSet.direct(ores),
                                                                       GenerationStep.Decoration.UNDERGROUND_ORES));
    }
}

