package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;

public class ItemDisplay {
    public static CreativeModeTab allomancy_group;

    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
        allomancy_group = event.registerCreativeModeTab(new ResourceLocation(Allomancy.MODID, "main_tab"), builder -> builder
                .icon(() -> new ItemStack(CombatSetup.MISTCLOAK.get()))
                .title(Component.translatable("tabs.allomancy.main_tab"))
                .displayItems((featureFlags, output) -> {
                    output.accept(ConsumeSetup.LERASIUM_NUGGET.get());
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

                    for (Metal mt : Metal.values()) {
                        if (mt.isVanilla()) {
                            continue;
                        }
                        output.accept(MaterialsSetup.STORAGE_BLOCKS.get(mt.getIndex()).get());
                    }

                    for (Metal mt : Metal.values()) {
                        if (mt.isVanilla()) {
                            continue;
                        }
                        output.accept(MaterialsSetup.INGOTS.get(mt.getIndex()).get());
                    }
                    for (Metal mt : Metal.values()) {
                        if (mt.isVanilla()) {
                            continue;
                        }
                        output.accept(MaterialsSetup.NUGGETS.get(mt.getIndex()).get());
                    }
                    for (Metal mt : Metal.values()) {
                        output.accept(MaterialsSetup.FLAKES.get(mt.getIndex()).get());
                    }

                    for (Metal mt : Metal.values()) {
                        output.accept(ExtrasSetup.PATTERN_ITEMS.get(mt.getIndex()).get());
                    }

                    for (var ore : MaterialsSetup.ORE_BLOCKS) {
                        output.accept(ore.get());
                    }

                    for (var ore : MaterialsSetup.DEEPSLATE_ORE_BLOCKS) {
                        output.accept(ore.get());
                    }

                    for (var ore : MaterialsSetup.RAW_ORE_BLOCKS) {
                        output.accept(ore.get());
                    }

                    for (var ore : MaterialsSetup.RAW_ORE_ITEMS) {
                        output.accept(ore.get());
                    }

                }));
    }

    public static void addTabContents(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.COMBAT) {
            event.accept(CombatSetup.MISTCLOAK);
            event.accept(CombatSetup.OBSIDIAN_DAGGER);
            event.accept(CombatSetup.KOLOSS_BLADE);
        } else if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ExtrasSetup.IRON_LEVER);
            event.accept(ExtrasSetup.IRON_BUTTON);
        }
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
