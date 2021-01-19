package com.legobmw99.allomancy.setup;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.PowersSetup;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class AllomancySetup {

    public static final ItemGroup allomancy_group = new ItemGroup(Allomancy.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(CombatSetup.MISTCLOAK.get());
        }
    };

    public static Item.Properties createStandardItemProperties() {
        return new Item.Properties().group(allomancy_group).maxStackSize(64);
    }

    public static Block.Properties createStandardBlockProperties() {
        return Block.Properties.create(Material.ROCK).hardnessAndResistance(2.1F).harvestTool(ToolType.PICKAXE).harvestLevel(2).setRequiresTool();
    }

    public static Item createStandardItem() {
        return new Item(createStandardItemProperties());
    }

    public static Block createStandardBlock() {
        return new Block(createStandardBlockProperties());
    }

    public static IFormattableTextComponent addColorToText(String translationKey, TextFormatting color) {
        IFormattableTextComponent lore = new TranslationTextComponent(translationKey);
        return addColor(lore, color);
    }

    public static IFormattableTextComponent addColorToText(String translationKey, TextFormatting color, Object... fmting) {
        IFormattableTextComponent lore = new TranslationTextComponent(translationKey, fmting);
        return addColor(lore, color);
    }

    private static IFormattableTextComponent addColor(IFormattableTextComponent text, TextFormatting color) {
        text.setStyle(text.getStyle().setColor(Color.fromTextFormatting(color)));
        return text;
    }

    public static void clientInit(final FMLClientSetupEvent e) {
        CombatSetup.clientInit(e);
        PowersSetup.clientInit(e);
    }

    public static void registerCommands(final RegisterCommandsEvent e) {
        PowersSetup.registerCommands(e);
    }

    public static void init(final FMLCommonSetupEvent e) {
        PowersSetup.init(e);
        MaterialsSetup.init(e);
        Network.registerPackets();
    }
}
