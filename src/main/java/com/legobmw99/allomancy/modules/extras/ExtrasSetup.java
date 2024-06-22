package com.legobmw99.allomancy.modules.extras;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.block.IAllomanticallyUsable;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.extras.advancement.AllomanticallyActivatedBlockTrigger;
import com.legobmw99.allomancy.modules.extras.advancement.MetalUsedOnEntityTrigger;
import com.legobmw99.allomancy.modules.extras.advancement.MetalUsedOnPlayerTrigger;
import com.legobmw99.allomancy.modules.extras.block.IronButtonBlock;
import com.legobmw99.allomancy.modules.extras.block.IronLeverBlock;
import com.legobmw99.allomancy.modules.extras.command.AllomancyPowerCommand;
import com.legobmw99.allomancy.modules.extras.command.AllomancyPowerType;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ExtrasSetup {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allomancy.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);

    public static final BlockCapability<IAllomanticallyUsable, Void> ALLOMANTICALLY_USABLE_BLOCK =
            BlockCapability.createVoid(
                    // Provide a name to uniquely identify the capability.
                    ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, "allomantically_usable_block"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    IAllomanticallyUsable.class);

    public static final DeferredBlock<IronButtonBlock> IRON_BUTTON =
            BLOCKS.register("iron_button", () -> new IronButtonBlock(true));

    public static final DeferredItem<Item> IRON_BUTTON_ITEM =
            ITEMS.register("iron_button", () -> new BlockItem(IRON_BUTTON.get(), new Item.Properties()));
    public static final DeferredBlock<IronButtonBlock> INVERTED_IRON_BUTTON =
            BLOCKS.register("inverted_iron_button", () -> new IronButtonBlock(false));

    public static final DeferredItem<Item> INVERTED_IRON_BUTTON_ITEM = ITEMS.register("inverted_iron_button",
                                                                                      () -> new BlockItem(
                                                                                              INVERTED_IRON_BUTTON.get(),
                                                                                              new Item.Properties()));
    public static final DeferredBlock<IronLeverBlock> IRON_LEVER = BLOCKS.register("iron_lever", IronLeverBlock::new);
    public static final DeferredItem<Item> IRON_LEVER_ITEM =
            ITEMS.register("iron_lever", () -> new BlockItem(IRON_LEVER.get(), new Item.Properties()));

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
            var pattern = ResourceKey.create(Registries.BANNER_PATTERN,
                                             ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, name));
            PATTERNS.add(pattern);

            var pattern_key = TagKey.create(Registries.BANNER_PATTERN,
                                            ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, name));
            PATTERN_KEYS.add(pattern_key);

            var pattern_item = ITEMS.register(name + "_pattern", () -> new BannerPatternItem(pattern_key,
                                                                                             new Item.Properties().stacksTo(
                                                                                                     1)));
            PATTERN_ITEMS.add(pattern_item);
        }
    }

    private static final DeferredRegister<CriterionTrigger<?>> CT =
            DeferredRegister.create(Registries.TRIGGER_TYPE, Allomancy.MODID);
    public static final Supplier<MetalUsedOnEntityTrigger> METAL_USED_ON_ENTITY_TRIGGER =
            CT.register("metal_used_on_entity", MetalUsedOnEntityTrigger::new);
    public static final Supplier<MetalUsedOnPlayerTrigger> METAL_USED_ON_PLAYER_TRIGGER =
            CT.register("metal_used_on_player", MetalUsedOnPlayerTrigger::new);
    public static final Supplier<AllomanticallyActivatedBlockTrigger> ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER =
            CT.register("activated_allomancy_block", AllomanticallyActivatedBlockTrigger::new);

    public static void register(IEventBus bus) {
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

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(ALLOMANTICALLY_USABLE_BLOCK, new IronLeverBlock.AllomanticUseCapabilityProvider(),
                            IRON_LEVER.get());
        event.registerBlock(ALLOMANTICALLY_USABLE_BLOCK, new IronButtonBlock.AllomanticUseCapabilityProvider(),
                            IRON_BUTTON.get(), INVERTED_IRON_BUTTON.get());

        event.registerBlock(ALLOMANTICALLY_USABLE_BLOCK,
                            ((level, pos, state, blockEntity, context) -> ((player, isPush) -> {
                                if (player instanceof ServerPlayer sp) {
                                    ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER.get().trigger(sp, pos, isPush);
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
