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
import com.legobmw99.allomancy.modules.extras.item.BronzeEarringItem;
import com.legobmw99.allomancy.util.AllomancyTags;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ExtrasSetup {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allomancy.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);


    public static final DeferredItem<BronzeEarringItem> BRONZE_EARRING =
            ITEMS.registerItem("bronze_earring", BronzeEarringItem::new, (p) -> p
                    .stacksTo(1)
                    .attributes(BronzeEarringItem.createAttributes())
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(ItemDisplay.addColorToText("item.allomancy.bronze_earring.lore",
                                                               ChatFormatting.GRAY)))));

    public static final ResourceKey<EquipmentAsset> BRONZE =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Allomancy.id("bronze_jewelry"));

    public static final DeferredItem<BronzeEarringItem> CHARGED_BRONZE_EARRING =
            ITEMS.registerItem("charged_bronze_earring", BronzeEarringItem::new, (p) -> p
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
                    Allomancy.id("allomantically_usable_block"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    IAllomanticallyUsable.class);


    private static final BlockBehaviour.Properties IRON_REDSTONE_PROPS =
            Block.Properties.of().noCollision().strength(1.0F);

    private static Item.Properties ironRedstoneLore(Item.Properties props) {
        return props.component(DataComponents.LORE, new ItemLore(
                List.of(ItemDisplay.addColorToText("block.allomancy.iron_activation.lore", ChatFormatting.GRAY))));
    }

    public static final DeferredBlock<IronButtonBlock> IRON_BUTTON =
            BLOCKS.registerBlock("iron_button", (props) -> new IronButtonBlock(true, props),
                                 () -> IRON_REDSTONE_PROPS);
    public static final DeferredItem<BlockItem> IRON_BUTTON_ITEM =
            ITEMS.registerSimpleBlockItem(IRON_BUTTON, ExtrasSetup::ironRedstoneLore);

    public static final DeferredBlock<IronButtonBlock> INVERTED_IRON_BUTTON =
            BLOCKS.registerBlock("inverted_iron_button", (props) -> new IronButtonBlock(false, props),
                                 () -> IRON_REDSTONE_PROPS);
    public static final DeferredItem<BlockItem> INVERTED_IRON_BUTTON_ITEM =
            ITEMS.registerSimpleBlockItem(INVERTED_IRON_BUTTON, ExtrasSetup::ironRedstoneLore);

    public static final DeferredBlock<IronLeverBlock> IRON_LEVER =
            BLOCKS.registerBlock("iron_lever", IronLeverBlock::new, () -> IRON_REDSTONE_PROPS);
    public static final DeferredItem<BlockItem> IRON_LEVER_ITEM =
            ITEMS.registerSimpleBlockItem(IRON_LEVER, ExtrasSetup::ironRedstoneLore);

    public static final List<ResourceKey<BannerPattern>> PATTERNS = new ArrayList<>();

    public static final List<DeferredItem<Item>> PATTERN_ITEMS = new ArrayList<>();
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES =
            DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Allomancy.MODID);


    static {
        for (Metal mt : Metal.values()) {

            String name = mt.getName();
            var pattern = ResourceKey.create(Registries.BANNER_PATTERN, Allomancy.id(name));
            PATTERNS.add(pattern);

            var pattern_key = TagKey.create(Registries.BANNER_PATTERN, Allomancy.id(name));
            AllomancyTags.PATTERN_TAGS.add(pattern_key);

            var pattern_item = ITEMS.registerItem(name + "_pattern", (props) -> new Item(
                    props.stacksTo(1).component(DataComponents.PROVIDES_BANNER_PATTERNS, pattern_key)));
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

    private static final Supplier<SingletonArgumentInfo<AllomancyPowerType>> CONTAINER_CLASS =
            COMMAND_ARGUMENT_TYPES.register("allomancy_power",
                                            () -> ArgumentTypeInfos.registerByClass(AllomancyPowerType.class,
                                                                                    SingletonArgumentInfo.contextFree(
                                                                                            AllomancyPowerType::allomancyPowerType)));

    private ExtrasSetup() {}

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        CT.register(bus);
        COMMAND_ARGUMENT_TYPES.register(bus);

        bus.addListener(ExtrasSetup::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(AllomancyPowerCommand::register);
    }

    private static void registerCapabilities(final RegisterCapabilitiesEvent event) {
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

                                if (level.getBlockEntity(pos) instanceof BellBlockEntity bell && bell.ticks != 0 &&
                                    bell.ticks < 5) {
                                    // too soon
                                    return false;
                                }

                                return ((BellBlock) state.getBlock()).onHit(level, state,
                                                                            new BlockHitResult(Vec3.atCenterOf(pos),
                                                                                               direction, pos, false),
                                                                            player, true);

                            })), Blocks.BELL);

    }

    public static void bootstrapBanners(BootstrapContext<BannerPattern> bootstrapContext) {
        for (var banner : PATTERNS) {
            bootstrapContext.register(banner, new BannerPattern(banner.identifier(),
                                                                banner.identifier().toShortLanguageKey()));
        }
    }
}
