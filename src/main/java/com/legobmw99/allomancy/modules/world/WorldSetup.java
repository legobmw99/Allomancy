package com.legobmw99.allomancy.modules.world;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.world.block.LerasiumFluid;
import com.legobmw99.allomancy.modules.world.block.LiquidLerasiumBlock;
import com.legobmw99.allomancy.modules.world.loot.DaggerLootModifier;
import com.legobmw99.allomancy.modules.world.loot.LerasiumLootModifier;
import com.legobmw99.allomancy.modules.world.recipe.InvestingRecipe;
import com.legobmw99.allomancy.util.AllomancyTags;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class WorldSetup {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allomancy.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Allomancy.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Allomancy.MODID);


    public static final Supplier<FluidType> LERAS_TYPE = FLUID_TYPES.register("lerasium", () -> new FluidType(
            FluidType.Properties
                    .create()
                    .canConvertToSource(false)
                    .lightLevel(14)
                    .fallDistanceModifier(0)
                    .canHydrate(false)
                    .canSwim(false)
                    .supportsBoating(false)
                    .canDrown(false)
                    .rarity(Rarity.EPIC)
                    .pathType(PathType.LAVA)));

    private static BaseFlowingFluid.Properties makeProps() {
        return new BaseFlowingFluid.Properties(LERAS_TYPE, LERASIUM_FLUID, LERASIUM_FLUID)
                .block(LIQUID_LERASIUM)
                .explosionResistance(100)
                .levelDecreasePerBlock(8);
    }

    public static final Supplier<FlowingFluid> LERASIUM_FLUID =
            FLUIDS.register("lerasium", () -> new LerasiumFluid(makeProps()));


    public static final DeferredBlock<LiquidBlock> LIQUID_LERASIUM =
            BLOCKS.registerBlock("liquid_lerasium", props -> new LiquidLerasiumBlock(LERASIUM_FLUID.get(), props),
                                 BlockBehaviour.Properties
                                         .of()
                                         .mapColor(MapColor.SNOW)
                                         .noCollission()
                                         .strength(100.0F)
                                         .pushReaction(PushReaction.DESTROY)
                                         .noLootTable()
                                         .lightLevel((state) -> 14)
                                         .liquid()
                                         .sound(SoundType.EMPTY));


    public record OreConfig(String name, int size, int placementCount, int minHeight, int maxHeight) {

        <T> ResourceKey<T> getRegistryKey(ResourceKey<Registry<T>> registry, String suffix) {
            return ResourceKey.create(registry, Allomancy.rl(this.name + suffix));
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static final OreConfig[] ORE_METALS =
            {new OreConfig("aluminum", 9, 11, 40, 120), new OreConfig("cadmium", 7, 4, -60, 0),
             new OreConfig("chromium", 6, 6, -30, 30), new OreConfig("lead", 9, 12, -40, 30),
             new OreConfig("silver", 7, 8, -40, 30), new OreConfig("tin", 11, 12, 30, 112),
             new OreConfig("zinc", 8, 9, 40, 80)};


    public static final ResourceKey<Structure> WELL = ResourceKey.create(Registries.STRUCTURE, Allomancy.rl("well"));
    private static final ResourceKey<StructureTemplatePool> WELL_POOL =
            ResourceKey.create(Registries.TEMPLATE_POOL, Allomancy.rl("well_pool"));
    private static final ResourceKey<StructureSet> WELLS =
            ResourceKey.create(Registries.STRUCTURE_SET, Allomancy.rl("wells"));
    private static final ResourceKey<BiomeModifier> ADD_ALLOMANCY_ORES =
            ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Allomancy.rl("overworld_ores"));

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

    private static final DeferredRegister<RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Allomancy.MODID);
    public static final Supplier<RecipeSerializer<InvestingRecipe>> INVESTING_RECIPE_SERIALIZER =
            RECIPES.register("investing", () -> new RecipeSerializer<InvestingRecipe>() {
                @Override
                public MapCodec<InvestingRecipe> codec() {
                    return RecordCodecBuilder.mapCodec(instance -> instance
                            .group(Ingredient.CODEC.fieldOf("ingredient").forGetter(InvestingRecipe::getIngredient),
                                   ItemStack.STRICT_CODEC.fieldOf("result").forGetter(InvestingRecipe::getResult))
                            .apply(instance, InvestingRecipe::new));
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, InvestingRecipe> streamCodec() {
                    return StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, InvestingRecipe::getIngredient,
                                                 ItemStack.STREAM_CODEC, InvestingRecipe::getResult,
                                                 InvestingRecipe::new);
                }
            });

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Allomancy.MODID);
    public static final Supplier<RecipeType<InvestingRecipe>> INVESTING_RECIPE =
            RECIPE_TYPES.register("investing", () -> RecipeType.simple(Allomancy.rl("investing")));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        GLM.register(bus);
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        RECIPES.register(bus);
        RECIPE_TYPES.register(bus);
    }

    private static DeferredBlock<Block> registerStandardBlock(String name) {
        return BLOCKS.registerBlock(name, Block::new,
                                    Blocks.STONE.properties().strength(2.1F).requiresCorrectToolForDrops());
    }

    private static DeferredBlock<Block> registerStandardOre(String name) {
        return BLOCKS.registerBlock(name, Block::new, Blocks.IRON_ORE.properties());
    }

    private static DeferredBlock<Block> registerDeepslateOre(String name) {
        return BLOCKS.registerBlock(name, Block::new, Blocks.DEEPSLATE_IRON_ORE.properties().strength(4.5F, 3.0F));
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
                                                       .defaultBlockState())), ore.size())));
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

    public static void bootstrapStructures(BootstrapContext<Structure> bootstrapContext) {
        bootstrapContext.register(WELL, new JigsawStructure(

                new Structure.StructureSettings.Builder(
                        bootstrapContext.lookup(Registries.BIOME).getOrThrow(AllomancyTags.SPAWNS_WELLS))
                        .generationStep(GenerationStep.Decoration.UNDERGROUND_STRUCTURES)
                        .terrainAdapation(TerrainAdjustment.NONE)
                        .spawnOverrides(Map.of(MobCategory.AMBIENT,

                                               new StructureSpawnOverride(
                                                       StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                                       WeightedList.of()), MobCategory.MONSTER,
                                               new StructureSpawnOverride(
                                                       StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                                       WeightedList.of())))
                        .build(), bootstrapContext.lookup(Registries.TEMPLATE_POOL).getOrThrow(WELL_POOL),
                Optional.empty(), 1, ConstantHeight.of(VerticalAnchor.absolute(-16)), false,
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG), 3, List.of(), DimensionPadding.ZERO,
                LiquidSettings.IGNORE_WATERLOGGING));
    }

    public static void bootstrapTemplatePools(BootstrapContext<StructureTemplatePool> bootstrapContext) {
        bootstrapContext.register(WELL_POOL, new StructureTemplatePool(
                bootstrapContext.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY), List.of(Pair.of(
                StructurePoolElement.single(Allomancy.rl("mountain_well").toString(),
                                            LiquidSettings.IGNORE_WATERLOGGING), 1)),
                StructureTemplatePool.Projection.RIGID));
    }

    public static void bootstrapStructureSets(BootstrapContext<StructureSet> bootstrapContext) {
        bootstrapContext.register(WELLS,
                                  new StructureSet(bootstrapContext.lookup(Registries.STRUCTURE).getOrThrow(WELL),
                                                   new RandomSpreadStructurePlacement(Vec3i.ZERO,
                                                                                      StructurePlacement.FrequencyReductionMethod.DEFAULT,
                                                                                      1, 161616, Optional.empty(), 16,
                                                                                      8, RandomSpreadType.LINEAR)));
    }


    private WorldSetup() {}

}

