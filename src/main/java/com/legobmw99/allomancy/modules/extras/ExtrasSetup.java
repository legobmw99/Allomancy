package com.legobmw99.allomancy.modules.extras;

import com.legobmw99.allomancy.Allomancy;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ExtrasSetup {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allomancy.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);
    private static final DeferredRegister<BannerPattern> BP = DeferredRegister.create(Registries.BANNER_PATTERN, Allomancy.MODID);


    public static final DeferredBlock<IronButtonBlock> IRON_BUTTON = BLOCKS.register("iron_button", () -> new IronButtonBlock(true));

    public static final DeferredItem<Item> IRON_BUTTON_ITEM = ITEMS.register("iron_button", () -> new BlockItem(IRON_BUTTON.get(), new Item.Properties()));
    public static final DeferredBlock<IronButtonBlock> INVERTED_IRON_BUTTON = BLOCKS.register("inverted_iron_button", () -> new IronButtonBlock(false));

    public static final DeferredItem<Item> INVERTED_IRON_BUTTON_ITEM = ITEMS.register("inverted_iron_button",
                                                                                      () -> new BlockItem(INVERTED_IRON_BUTTON.get(), new Item.Properties()));
    public static final DeferredBlock<IronLeverBlock> IRON_LEVER = BLOCKS.register("iron_lever", IronLeverBlock::new);
    public static final DeferredItem<Item> IRON_LEVER_ITEM = ITEMS.register("iron_lever", () -> new BlockItem(IRON_LEVER.get(), new Item.Properties()));

    public static final List<DeferredHolder<BannerPattern, BannerPattern>> PATTERNS = new ArrayList<>();
    public static final List<TagKey<BannerPattern>> PATTERN_KEYS = new ArrayList<>();

    public static final List<DeferredItem<BannerPatternItem>> PATTERN_ITEMS = new ArrayList<>();
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Allomancy.MODID);
    private static final Supplier<SingletonArgumentInfo<AllomancyPowerType>> CONTAINER_CLASS = COMMAND_ARGUMENT_TYPES.register("allomancy_power",
                                                                                                                               () -> ArgumentTypeInfos.registerByClass(
                                                                                                                                       AllomancyPowerType.class,
                                                                                                                                       SingletonArgumentInfo.contextFree(
                                                                                                                                               AllomancyPowerType::allomancyPowerType)));


    static {
        for (Metal mt : Metal.values()) {

            String name = mt.getName();
            var pattern = BP.register(mt.getName(), () -> new BannerPattern("ALLOMANCY" + mt.name()));
            PATTERNS.add(pattern);
            var pattern_key = TagKey.create(Registries.BANNER_PATTERN, new ResourceLocation(Allomancy.MODID, name));
            PATTERN_KEYS.add(pattern_key);

            var pattern_item = ITEMS.register(name + "_pattern", () -> new BannerPatternItem(pattern_key, new Item.Properties().stacksTo(1)));
            PATTERN_ITEMS.add(pattern_item);
        }
    }

    private static final DeferredRegister<CriterionTrigger<?>> CT = DeferredRegister.create(Registries.TRIGGER_TYPE, Allomancy.MODID);
    public static final Supplier<MetalUsedOnEntityTrigger> METAL_USED_ON_ENTITY_TRIGGER = CT.register("metal_used_on_entity", MetalUsedOnEntityTrigger::new);
    public static final Supplier<MetalUsedOnPlayerTrigger> METAL_USED_ON_PLAYER_TRIGGER = CT.register("metal_used_on_player", MetalUsedOnPlayerTrigger::new);
    public static final Supplier<AllomanticallyActivatedBlockTrigger> ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER = CT.register("activated_allomancy_block",
                                                                                                                           AllomanticallyActivatedBlockTrigger::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BP.register(bus);
        CT.register(bus);
        COMMAND_ARGUMENT_TYPES.register(bus);
    }

    public static void registerCommands(final RegisterCommandsEvent e) {
        AllomancyPowerCommand.register(e.getDispatcher());
    }

}
