package com.legobmw99.allomancy.modules.world;

import com.google.gson.JsonObject;
import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.world.block.LerasiumFluid;
import com.legobmw99.allomancy.modules.world.block.LiquidLerasiumBlock;
import com.legobmw99.allomancy.modules.world.client.WorldClientSetup;
import com.legobmw99.allomancy.modules.world.loot.LootTableInjector;
import com.legobmw99.allomancy.modules.world.recipe.InvestingRecipe;
import com.legobmw99.allomancy.util.AllomancyTags;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.random.WeightedRandomList;
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
import net.minecraft.world.level.block.DropExperienceBlock;
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
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WorldSetup {

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
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Allomancy.rl("overworld_ores"));

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Allomancy.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Allomancy.MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Allomancy.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Allomancy.MODID);

    public static final Supplier<FluidType> LERAS_TYPE = FLUID_TYPES.register("lerasium", () -> new FluidType(
            FluidType.Properties
                    .create()
                    .canConvertToSource(false)
                    .lightLevel(14)
                    .canHydrate(false)
                    .canSwim(false)
                    .supportsBoating(false)
                    .canDrown(false)
                    .rarity(Rarity.EPIC)
                    .pathType(BlockPathTypes.LAVA)) {
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new WorldClientSetup.LerasiumFluidExtension());
        }
    });

    private static ForgeFlowingFluid.Properties makeProps() {
        return new ForgeFlowingFluid.Properties(LERAS_TYPE, LERASIUM_FLUID, LERASIUM_FLUID)
                .block(LIQUID_LERASIUM)
                .explosionResistance(100)
                .levelDecreasePerBlock(8);
    }

    public static final Supplier<FlowingFluid> LERASIUM_FLUID =
            FLUIDS.register("lerasium", () -> new LerasiumFluid(makeProps()));


    public static final RegistryObject<LiquidLerasiumBlock> LIQUID_LERASIUM = BLOCKS.register("liquid_lerasium",
                                                                                              () -> new LiquidLerasiumBlock(
                                                                                                      LERASIUM_FLUID,
                                                                                                      BlockBehaviour.Properties
                                                                                                              .of()
                                                                                                              .mapColor(
                                                                                                                      MapColor.SNOW)
                                                                                                              .noCollission()
                                                                                                              .strength(
                                                                                                                      100.0F)
                                                                                                              .pushReaction(
                                                                                                                      PushReaction.DESTROY)
                                                                                                              .noLootTable()
                                                                                                              .lightLevel(
                                                                                                                      (state) -> 14)
                                                                                                              .liquid()
                                                                                                              .sound(SoundType.EMPTY)));

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
            FLAKES.add(WorldSetup.ITEMS.register(name + "_flakes", WorldSetup::createStandardItem));

            if (mt.isVanilla()) {
                NUGGETS.add(null);
                INGOTS.add(null);
                STORAGE_BLOCKS.add(null);
                STORAGE_BLOCK_ITEMS.add(null);
            } else {
                NUGGETS.add(ITEMS.register(name + "_nugget", WorldSetup::createStandardItem));
                INGOTS.add(ITEMS.register(name + "_ingot", WorldSetup::createStandardItem));
                STORAGE_BLOCKS.add(BLOCKS.register(name + "_block", WorldSetup::createStandardBlock));
                STORAGE_BLOCK_ITEMS.add(ITEMS.register(name + "_block",
                                                       () -> new BlockItem(STORAGE_BLOCKS.get(mt.getIndex()).get(),
                                                                           new Item.Properties())));
            }
        }
        FLAKES.add(WorldSetup.ITEMS.register("lead_flakes", WorldSetup::createStandardItem));
        NUGGETS.add(ITEMS.register("lead_nugget", WorldSetup::createStandardItem));
        INGOTS.add(ITEMS.register("lead_ingot", WorldSetup::createStandardItem));
        STORAGE_BLOCKS.add(BLOCKS.register("lead_block", WorldSetup::createStandardBlock));
        STORAGE_BLOCK_ITEMS.add(ITEMS.register("lead_block", () -> new BlockItem(STORAGE_BLOCKS.get(LEAD).get(),
                                                                                 new Item.Properties())));

        FLAKES.add(WorldSetup.ITEMS.register("silver_flakes", WorldSetup::createStandardItem));
        NUGGETS.add(ITEMS.register("silver_nugget", WorldSetup::createStandardItem));
        INGOTS.add(ITEMS.register("silver_ingot", WorldSetup::createStandardItem));
        STORAGE_BLOCKS.add(BLOCKS.register("silver_block", WorldSetup::createStandardBlock));
        STORAGE_BLOCK_ITEMS.add(ITEMS.register("silver_block", () -> new BlockItem(STORAGE_BLOCKS.get(SILVER).get(),
                                                                                   new Item.Properties())));

        for (var ore : ORE_METALS) {
            var ore_block = BLOCKS.register(ore + "_ore", WorldSetup::createStandardOre);
            ORE_BLOCKS.add(ore_block);
            ORE_BLOCKS_ITEMS.add(
                    ITEMS.register(ore + "_ore", () -> new BlockItem(ore_block.get(), new Item.Properties())));

            var ds_ore_block = BLOCKS.register("deepslate_" + ore + "_ore", WorldSetup::createDeepslateBlock);
            DEEPSLATE_ORE_BLOCKS.add(ds_ore_block);
            DEEPSLATE_ORE_BLOCKS_ITEMS.add(ITEMS.register("deepslate_" + ore + "_ore",
                                                          () -> new BlockItem(ds_ore_block.get(),
                                                                              new Item.Properties())));

            var raw_ore_block = BLOCKS.register("raw_" + ore + "_block", WorldSetup::createStandardBlock);
            RAW_ORE_BLOCKS.add(raw_ore_block);
            RAW_ORE_BLOCKS_ITEMS.add(ITEMS.register("raw_" + ore + "_block",
                                                    () -> new BlockItem(raw_ore_block.get(), new Item.Properties())));

            RAW_ORE_ITEMS.add(ITEMS.register("raw_" + ore, WorldSetup::createStandardItem));
        }
    }


    private static final DeferredRegister<RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Allomancy.MODID);
    public static final Supplier<RecipeSerializer<InvestingRecipe>> INVESTING_RECIPE_SERIALIZER =
            RECIPES.register("investing", () -> new RecipeSerializer<>() {
                public InvestingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
                    Ingredient ingredient =
                            Ingredient.fromJson(GsonHelper.getAsJsonObject(pJson, "ingredient"), false);

                    String s1 = GsonHelper.getAsString(pJson, "result");
                    int i = GsonHelper.getAsInt(pJson, "count");
                    ItemStack itemstack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(s1)), i);
                    return new InvestingRecipe(pRecipeId, ingredient, itemstack);
                }

                public InvestingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
                    Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
                    ItemStack itemstack = pBuffer.readItem();
                    return new InvestingRecipe(pRecipeId, ingredient, itemstack);
                }

                public void toNetwork(FriendlyByteBuf pBuffer, InvestingRecipe pRecipe) {
                    pRecipe.getIngredient().toNetwork(pBuffer);
                    pBuffer.writeItem(pRecipe.getResult());
                }
            });

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Allomancy.MODID);
    public static final Supplier<RecipeType<InvestingRecipe>> INVESTING_RECIPE =
            RECIPE_TYPES.register("investing", () -> RecipeType.simple(Allomancy.rl("investing")));

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FLUID_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
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
        return new DropExperienceBlock(
                BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE).strength(4.5F, 3.0F));
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

            bootstrap.register(ore.getRegistryKey(Registries.CONFIGURED_FEATURE, "_ore_feature"),
                               new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(
                                       List.of(OreConfiguration.target(stone, ore_block.get().defaultBlockState()),
                                               OreConfiguration.target(deepslate, deepslate_ore_block
                                                       .get()
                                                       .defaultBlockState())), ore.size())));
        }
    }

    public static void bootstrapPlaced(BootstapContext<PlacedFeature> bootstrap) {

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

    public static void bootstrapBiomeModifier(BootstapContext<BiomeModifier> bootstrap) {
        var overworldTag = bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD);
        HolderGetter<PlacedFeature> placed = bootstrap.lookup(Registries.PLACED_FEATURE);

        List<Holder<PlacedFeature>> ores = new ArrayList<>();
        for (OreConfig ore : ORE_METALS) {
            ores.add(placed.getOrThrow(ore.getRegistryKey(Registries.PLACED_FEATURE, "_ore")));
        }
        bootstrap.register(ADD_ALLOMANCY_ORES,
                           new ForgeBiomeModifiers.AddFeaturesBiomeModifier(overworldTag, HolderSet.direct(ores),
                                                                            GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    public static void bootstrapStructures(BootstapContext<Structure> BootstapContext) {
        BootstapContext.register(WELL, new JigsawStructure(

                new Structure.StructureSettings(
                        BootstapContext.lookup(Registries.BIOME).getOrThrow(AllomancyTags.SPAWNS_WELLS),
                        Map.of(MobCategory.AMBIENT,

                               new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                                          WeightedRandomList.create()), MobCategory.MONSTER,
                               new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                                          WeightedRandomList.create())),
                        GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE),
                BootstapContext.lookup(Registries.TEMPLATE_POOL).getOrThrow(WELL_POOL), Optional.empty(), 1,
                ConstantHeight.of(VerticalAnchor.absolute(-16)), false, Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                3));
    }

    public static void bootstrapTemplatePools(BootstapContext<StructureTemplatePool> BootstapContext) {
        BootstapContext.register(WELL_POOL, new StructureTemplatePool(
                BootstapContext.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY),
                List.of(Pair.of(StructurePoolElement.single(Allomancy.rl("mountain_well").toString()), 1)),
                StructureTemplatePool.Projection.RIGID));
    }

    public static void bootstrapStructureSets(BootstapContext<StructureSet> BootstapContext) {
        BootstapContext.register(WELLS,
                                 new StructureSet(BootstapContext.lookup(Registries.STRUCTURE).getOrThrow(WELL),
                                                  new RandomSpreadStructurePlacement(Vec3i.ZERO,
                                                                                     StructurePlacement.FrequencyReductionMethod.DEFAULT,
                                                                                     1, 161616, Optional.empty(), 16,
                                                                                     8, RandomSpreadType.LINEAR)));
    }


}

