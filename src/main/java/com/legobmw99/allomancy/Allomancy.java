package com.legobmw99.allomancy;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.materials.world.OreGenerator;
import com.legobmw99.allomancy.modules.powers.PowersSetup;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.util.AllomancyConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Allomancy.MODID)
public class Allomancy {

    public static final String MODID = "allomancy";
    public static final ItemGroup allomancy_group = new ItemGroup(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(CombatSetup.MISTCLOAK.get());
        }
    };

    public static final Logger LOGGER = LogManager.getLogger();

    public static Allomancy instance;

    public Allomancy() {
        instance = this;
        // Register our setup events on the necessary buses
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Allomancy::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Allomancy::clientInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AllomancyConfig::onLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AllomancyConfig::onReload);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PowersClientSetup::registerParticle);
        MinecraftForge.EVENT_BUS.addListener(Allomancy::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, OreGenerator::registerGeneration);

        // Register all Registries
        PowersSetup.register();
        CombatSetup.register();
        ConsumeSetup.register();
        MaterialsSetup.register();
        ExtrasSetup.register();

        // Config init
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AllomancyConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AllomancyConfig.CLIENT_CONFIG);

    }

    public static Item.Properties createStandardItemProperties() {
        return new Item.Properties().tab(allomancy_group).stacksTo(64);
    }

    public static Block.Properties createStandardBlockProperties() {
        return Block.Properties.of(Material.STONE).strength(2.1F).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops();
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
        text.setStyle(text.getStyle().withColor(Color.fromLegacyFormat(color)));
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
