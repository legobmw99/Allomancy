package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ItemDisplay {

    private static final DeferredRegister<CreativeModeTab> CREATIVETABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Allomancy.MODID);

    public static Supplier<CreativeModeTab> allomancy_group = CREATIVETABS.register("main_tab", () -> CreativeModeTab
            .builder()
            .icon(() -> new ItemStack(CombatSetup.MISTCLOAK.get()))
            .title(Component.translatable("tabs.allomancy.main_tab"))
            .displayItems((featureFlags, output) -> {
                output.accept(ConsumeSetup.LERASIUM_NUGGET.get());
                output.accept(ExtrasSetup.BRONZE_EARRING.get());
                output.accept(ExtrasSetup.CHARGED_BRONZE_EARRING.get());

                output.accept(CombatSetup.ALUMINUM_HELMET.get());
                output.accept(CombatSetup.MISTCLOAK.get());
                output.accept(CombatSetup.COIN_BAG.get());
                output.accept(ConsumeSetup.ALLOMANTIC_GRINDER.get());
                output.accept(ConsumeSetup.VIAL.get());

                ItemStack fullVial = new ItemStack(ConsumeSetup.VIAL.get(), 1);
                CompoundTag nbt = new CompoundTag();
                for (Metal mt : Metal.values()) {
                    nbt.putBoolean(mt.getName(), true);
                }
                nbt.putInt("CustomModelData", 1);
                fullVial.setTag(nbt);
                output.accept(fullVial);

                output.accept(CombatSetup.KOLOSS_BLADE.get());
                output.accept(CombatSetup.OBSIDIAN_DAGGER.get());

                output.accept(ExtrasSetup.IRON_LEVER.get());
                output.accept(ExtrasSetup.IRON_BUTTON.get());
                output.accept(ExtrasSetup.INVERTED_IRON_BUTTON.get());


                for (var block : WorldSetup.STORAGE_BLOCKS) {
                    if (block == null) {
                        continue;
                    }
                    output.accept(block.get());
                }

                for (var ingot : WorldSetup.INGOTS) {
                    if (ingot == null) {
                        continue;
                    }
                    output.accept(ingot.get());
                }

                for (var nug : WorldSetup.NUGGETS) {
                    if (nug == null) {
                        continue;
                    }
                    output.accept(nug.get());
                }

                for (var flake : WorldSetup.FLAKES) {
                    if (flake == null) {
                        continue;
                    }
                    output.accept(flake.get());
                }

                for (Metal mt : Metal.values()) {
                    output.accept(ExtrasSetup.PATTERN_ITEMS.get(mt.getIndex()).get());
                }

                for (var ore : WorldSetup.ORE_BLOCKS) {
                    output.accept(ore.get());
                }

                for (var ore : WorldSetup.DEEPSLATE_ORE_BLOCKS) {
                    output.accept(ore.get());
                }

                for (var ore : WorldSetup.RAW_ORE_BLOCKS) {
                    output.accept(ore.get());
                }

                for (var ore : WorldSetup.RAW_ORE_ITEMS) {
                    output.accept(ore.get());
                }

            })
            .build());

    public static void addTabContents(BuildCreativeModeTabContentsEvent event) {
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

    public static void register() {
        CREATIVETABS.register(FMLJavaModLoadingContext.get().getModEventBus());
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
        text.setStyle(text.getStyle().withColor(TextColor.fromLegacyFormat(color)));
        return text;
    }

}