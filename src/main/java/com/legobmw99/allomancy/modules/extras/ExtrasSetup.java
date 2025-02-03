package com.legobmw99.allomancy.modules.extras;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.block.IAllomanticallyUsable;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.extras.advancement.AllomanticallyActivatedBlockTrigger;
import com.legobmw99.allomancy.modules.extras.advancement.MetalUsedOnEntityTrigger;
import com.legobmw99.allomancy.modules.extras.advancement.MetalUsedOnPlayerTrigger;
import com.legobmw99.allomancy.modules.extras.block.IronButtonBlock;
import com.legobmw99.allomancy.modules.extras.block.IronLeverBlock;
import com.legobmw99.allomancy.modules.extras.block.LerasiumFluid;
import com.legobmw99.allomancy.modules.extras.block.LiquidLerasiumBlock;
import com.legobmw99.allomancy.modules.extras.command.AllomancyPowerCommand;
import com.legobmw99.allomancy.modules.extras.command.AllomancyPowerType;
import com.legobmw99.allomancy.modules.extras.item.BronzeEarringItem;
import com.legobmw99.allomancy.util.ItemDisplay;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
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
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
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

public final class ExtrasSetup {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allomancy.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Allomancy.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Allomancy.MODID);

    public static final DeferredItem<BronzeEarringItem> BRONZE_EARRING =
            ITEMS.registerItem("bronze_earring", BronzeEarringItem::new, new Item.Properties()
                    .stacksTo(1)
                    .attributes(BronzeEarringItem.createAttributes())
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(ItemDisplay.addColorToText("item.allomancy.bronze_earring.lore",
                                                               ChatFormatting.GRAY)))));

    public static final TagKey<EntityType<?>> HEMALURGIC_CHARGERS =
            TagKey.create(Registries.ENTITY_TYPE, Allomancy.rl("killing_charges_earring"));

    public static final ResourceKey<EquipmentAsset> BRONZE =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Allomancy.rl("bronze_jewelry"));

    public static final DeferredItem<BronzeEarringItem> CHARGED_BRONZE_EARRING =
            ITEMS.registerItem("charged_bronze_earring", BronzeEarringItem::new, new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.EQUIPPABLE,
                               Equippable.builder(EquipmentSlot.HEAD).setAsset(BRONZE).build())
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(ItemDisplay.addColorToText("item.allomancy.charged_bronze_earring.lore",
                                                               ChatFormatting.BLUE)))));

    public static final BlockCapability<IAllomanticallyUsable, Void> ALLOMANTICALLY_USABLE_BLOCK =
            BlockCapability.createVoid(
                    // Provide a name to uniquely identify the capability.
                    Allomancy.rl("allomantically_usable_block"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    IAllomanticallyUsable.class);


    private static final BlockBehaviour.Properties IRON_REDSTONE_PROPS =
            Block.Properties.of().noCollission().strength(1.0F);

    private static final Item.Properties IRON_REDSTONE_LORE = new Item.Properties().component(DataComponents.LORE,
                                                                                              new ItemLore(
                                                                                                      List.of(ItemDisplay.addColorToText(
                                                                                                              "block.allomancy.iron_activation.lore",
                                                                                                              ChatFormatting.GRAY))));
    public static final DeferredBlock<IronButtonBlock> IRON_BUTTON =
            BLOCKS.registerBlock("iron_button", (props) -> new IronButtonBlock(true, props), IRON_REDSTONE_PROPS);
    public static final DeferredItem<BlockItem> IRON_BUTTON_ITEM =
            ITEMS.registerSimpleBlockItem(IRON_BUTTON, IRON_REDSTONE_LORE);

    public static final DeferredBlock<IronButtonBlock> INVERTED_IRON_BUTTON =
            BLOCKS.registerBlock("inverted_iron_button", (props) -> new IronButtonBlock(false, props),
                                 IRON_REDSTONE_PROPS);
    public static final DeferredItem<BlockItem> INVERTED_IRON_BUTTON_ITEM =
            ITEMS.registerSimpleBlockItem(INVERTED_IRON_BUTTON, IRON_REDSTONE_LORE);

    public static final DeferredBlock<IronLeverBlock> IRON_LEVER =
            BLOCKS.registerBlock("iron_lever", IronLeverBlock::new, IRON_REDSTONE_PROPS);
    public static final DeferredItem<BlockItem> IRON_LEVER_ITEM =
            ITEMS.registerSimpleBlockItem(IRON_LEVER, IRON_REDSTONE_LORE);

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
                    .pathType(PathType.LAVA)));

    private static BaseFlowingFluid.Properties makeProps() {
        return new BaseFlowingFluid.Properties(LERAS_TYPE, LERASIUM_FLUID, LERASIUM_FLUID)
                .block(LIQUID_LERASIUM)
                .explosionResistance(100)
                .levelDecreasePerBlock(8);
    }

    public static final Supplier<FlowingFluid> LERASIUM_FLUID =
            FLUIDS.register("lerasium", () -> new LerasiumFluid(makeProps()));

    public static final TagKey<Item> LERASIUM_CONVERSION = ItemTags.create(Allomancy.rl("converts_to_lerasium"));

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

    public static final List<ResourceKey<BannerPattern>> PATTERNS = new ArrayList<>();
    public static final List<TagKey<BannerPattern>> PATTERN_KEYS = new ArrayList<>();

    public static final List<DeferredItem<BannerPatternItem>> PATTERN_ITEMS = new ArrayList<>();
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES =
            DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Allomancy.MODID);
    private static final Supplier<SingletonArgumentInfo<AllomancyPowerType>> CONTAINER_CLASS =
            COMMAND_ARGUMENT_TYPES.register("allomancy_power",
                                            () -> ArgumentTypeInfos.registerByClass(AllomancyPowerType.class,
                                                                                    SingletonArgumentInfo.contextFree(
                                                                                            AllomancyPowerType::allomancyPowerType)));


    static {
        for (Metal mt : Metal.values()) {

            String name = mt.getName();
            var pattern = ResourceKey.create(Registries.BANNER_PATTERN, Allomancy.rl(name));
            PATTERNS.add(pattern);

            var pattern_key = TagKey.create(Registries.BANNER_PATTERN, Allomancy.rl(name));
            PATTERN_KEYS.add(pattern_key);

            var pattern_item = ITEMS.registerItem(name + "_pattern",
                                                  (props) -> new BannerPatternItem(pattern_key, props.stacksTo(1)));
            PATTERN_ITEMS.add(pattern_item);
        }
    }

    public static TagKey<Structure> SEEKABLE = TagKey.create(Registries.STRUCTURE, Allomancy.rl("seekable"));
    public static final TagKey<Biome> SPAWNS_WELLS =
            TagKey.create(Registries.BIOME, Allomancy.rl("has_structure/well"));
    public static final ResourceKey<Structure> WELL = ResourceKey.create(Registries.STRUCTURE, Allomancy.rl("well"));
    public static final ResourceKey<StructureTemplatePool> WELL_POOL =
            ResourceKey.create(Registries.TEMPLATE_POOL, Allomancy.rl("well_pool"));
    public static final ResourceKey<StructureSet> WELLS =
            ResourceKey.create(Registries.STRUCTURE_SET, Allomancy.rl("wells"));


    private static final DeferredRegister<CriterionTrigger<?>> CT =
            DeferredRegister.create(Registries.TRIGGER_TYPE, Allomancy.MODID);
    public static final Supplier<MetalUsedOnEntityTrigger> METAL_USED_ON_ENTITY_TRIGGER =
            CT.register("metal_used_on_entity", MetalUsedOnEntityTrigger::new);
    public static final Supplier<MetalUsedOnPlayerTrigger> METAL_USED_ON_PLAYER_TRIGGER =
            CT.register("metal_used_on_player", MetalUsedOnPlayerTrigger::new);
    public static final Supplier<AllomanticallyActivatedBlockTrigger> ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER =
            CT.register("activated_allomancy_block", AllomanticallyActivatedBlockTrigger::new);

    private ExtrasSetup() {}

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        CT.register(bus);
        COMMAND_ARGUMENT_TYPES.register(bus);
    }

    public static void bootstrapBanners(BootstrapContext<BannerPattern> bootstrapContext) {
        for (var banner : PATTERNS) {
            bootstrapContext.register(banner,
                                      new BannerPattern(banner.location(), banner.location().toShortLanguageKey()));
        }
    }


    public static void bootstrapStructures(BootstrapContext<Structure> bootstrapContext) {
        bootstrapContext.register(WELL, new JigsawStructure(new Structure.StructureSettings.Builder(
                bootstrapContext.lookup(Registries.BIOME).getOrThrow(SPAWNS_WELLS))
                                                                    .generationStep(
                                                                            GenerationStep.Decoration.UNDERGROUND_STRUCTURES)
                                                                    .terrainAdapation(TerrainAdjustment.NONE)
                                                                    .spawnOverrides(Map.of(MobCategory.AMBIENT,
                                                                                           new StructureSpawnOverride(
                                                                                                   StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                                                                                   WeightedRandomList.create()),
                                                                                           MobCategory.MONSTER,
                                                                                           new StructureSpawnOverride(
                                                                                                   StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                                                                                   WeightedRandomList.create())))
                                                                    .build(), bootstrapContext
                                                                    .lookup(Registries.TEMPLATE_POOL)
                                                                    .getOrThrow(WELL_POOL), Optional.empty(), 1,
                                                            ConstantHeight.of(VerticalAnchor.absolute(-16)), false,
                                                            Optional.of(Heightmap.Types.WORLD_SURFACE_WG), 3,
                                                            List.of(), DimensionPadding.ZERO,
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

    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlock(ALLOMANTICALLY_USABLE_BLOCK, new IronLeverBlock.AllomanticUseCapabilityProvider(),
                            IRON_LEVER.get());
        event.registerBlock(ALLOMANTICALLY_USABLE_BLOCK, new IronButtonBlock.AllomanticUseCapabilityProvider(),
                            IRON_BUTTON.get(), INVERTED_IRON_BUTTON.get());

        event.registerBlock(ALLOMANTICALLY_USABLE_BLOCK,
                            ((level, pos, state, blockEntity, context) -> ((player, isPush) -> {
                                if (player instanceof ServerPlayer sp) {
                                    ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER.get().trigger(sp, pos, isPush);
                                }
                                if (level.isClientSide()) {
                                    return true;
                                }
                                var direction = player.getNearestViewDirection();
                                if (isPush) {
                                    direction = direction.getOpposite();
                                }
                                return ((BellBlock) state.getBlock()).onHit(level, state,
                                                                            new BlockHitResult(Vec3.atCenterOf(pos),
                                                                                               direction, pos, false),
                                                                            player, true);

                            })), Blocks.BELL);

    }

    public static void registerCommands(final RegisterCommandsEvent e) {
        AllomancyPowerCommand.register(e.getDispatcher());
    }

}
