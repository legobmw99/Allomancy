package com.legobmw99.allomancy.modules.extras;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.extras.advancement.MetalUsedOnEntityTrigger;
import com.legobmw99.allomancy.modules.extras.block.IronButtonBlock;
import com.legobmw99.allomancy.modules.extras.block.IronLeverBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ExtrasSetup {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Allomancy.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);
    public static final DeferredRegister<BannerPattern> BP = DeferredRegister.create(Registries.BANNER_PATTERN, Allomancy.MODID);


    public static final DeferredBlock<IronButtonBlock> IRON_BUTTON = BLOCKS.register("iron_button", IronButtonBlock::new);
    public static final DeferredItem<Item> IRON_BUTTON_ITEM = ITEMS.register("iron_button", () -> new BlockItem(IRON_BUTTON.get(), new Item.Properties()));
    public static final DeferredBlock<IronLeverBlock> IRON_LEVER = BLOCKS.register("iron_lever", IronLeverBlock::new);
    public static final DeferredItem<Item> IRON_LEVER_ITEM = ITEMS.register("iron_lever", () -> new BlockItem(IRON_LEVER.get(), new Item.Properties()));

    public static final List<DeferredHolder<BannerPattern, BannerPattern>> PATTERNS = new ArrayList<>();
    public static final List<TagKey<BannerPattern>> PATTERN_KEYS = new ArrayList<>();

    public static final List<DeferredItem<BannerPatternItem>> PATTERN_ITEMS = new ArrayList<>();


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

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BP.register(bus);
    }


    public static final MetalUsedOnEntityTrigger METAL_USED_ON_ENTITY_TRIGGER = new MetalUsedOnEntityTrigger();

    public static void init(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            CriteriaTriggers.register("allomancy:metal_used", METAL_USED_ON_ENTITY_TRIGGER);
        });
    }
}
