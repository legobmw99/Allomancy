package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.VialItem;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;
import java.util.function.Supplier;


public final class ItemDisplay {

    private static final DeferredRegister<CreativeModeTab> CREATIVETABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Allomancy.MODID);

    private static final Supplier<CreativeModeTab> ALLOMANCY_CREATIVE_TAB =

            CREATIVETABS.register("main_tab", () -> CreativeModeTab
                    .builder()
                    .icon(CombatSetup.MISTCLOAK::toStack)
                    .title(Component.translatable("tabs" + ".allomancy.main_tab"))
                    .displayItems((featureFlags, output) -> {
                        output.accept(ConsumeSetup.LERASIUM_NUGGET);

                        output.accept(CombatSetup.ALUMINUM_HELMET);
                        output.accept(CombatSetup.MISTCLOAK);
                        output.accept(CombatSetup.COIN_BAG);
                        output.accept(ConsumeSetup.ALLOMANTIC_GRINDER);
                        output.accept(ConsumeSetup.VIAL);

                        ItemStack fullVial = ConsumeSetup.VIAL.toStack();
                        FlakeStorage.Mutable storage = new FlakeStorage.Mutable();
                        for (Metal mt : Metal.values()) {
                            storage.add(mt);
                        }
                        VialItem.fillVial(fullVial, storage.toImmutable());

                        output.accept(fullVial);

                        output.accept(CombatSetup.KOLOSS_BLADE);
                        output.accept(CombatSetup.OBSIDIAN_DAGGER);

                        output.accept(ExtrasSetup.BRONZE_EARRING);
                        output.accept(ExtrasSetup.CHARGED_BRONZE_EARRING);

                        output.accept(ExtrasSetup.IRON_LEVER);
                        output.accept(ExtrasSetup.IRON_BUTTON);
                        output.accept(ExtrasSetup.INVERTED_IRON_BUTTON);

                        WorldSetup.STORAGE_BLOCKS.stream().filter(Objects::nonNull).forEach(output::accept);
                        WorldSetup.INGOTS.stream().filter(Objects::nonNull).forEach(output::accept);
                        WorldSetup.NUGGETS.stream().filter(Objects::nonNull).forEach(output::accept);
                        WorldSetup.FLAKES.stream().filter(Objects::nonNull).forEach(output::accept);
                        for (Metal mt : Metal.values()) {
                            output.accept(ExtrasSetup.PATTERN_ITEMS.get(mt.getIndex()));
                        }
                        WorldSetup.ORE_BLOCKS.forEach(output::accept);
                        WorldSetup.DEEPSLATE_ORE_BLOCKS.forEach(output::accept);
                        WorldSetup.RAW_ORE_BLOCKS.forEach(output::accept);
                        WorldSetup.RAW_ORE_ITEMS.stream().filter(Objects::nonNull).forEach(output::accept);
                    })
                    .build());

    private ItemDisplay() {}

    private static void addTabContents(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(CombatSetup.ALUMINUM_HELMET);
            event.accept(CombatSetup.MISTCLOAK);
            event.accept(CombatSetup.OBSIDIAN_DAGGER);
            event.accept(CombatSetup.KOLOSS_BLADE);
        } else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ExtrasSetup.IRON_LEVER);
            event.accept(ExtrasSetup.IRON_BUTTON);
            event.accept(ExtrasSetup.INVERTED_IRON_BUTTON);
        }
    }

    public static void register(IEventBus bus) {
        CREATIVETABS.register(bus);

        bus.addListener(ItemDisplay::addTabContents);
    }

    public static MutableComponent addColorToText(String translationKey, ChatFormatting color) {
        MutableComponent lore = Component.translatable(translationKey);
        return addColor(lore, color);
    }

    public static MutableComponent addColorToText(String translationKey, ChatFormatting color, Object... fmting) {
        MutableComponent lore = Component.translatable(translationKey, fmting);
        return addColor(lore, color);
    }

    private static MutableComponent addColor(MutableComponent text, ChatFormatting color) {
        text.setStyle(text.getStyle().withColor(color));
        return text;
    }

}
