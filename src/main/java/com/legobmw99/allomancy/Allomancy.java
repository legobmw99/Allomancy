package com.legobmw99.allomancy;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.combat.client.CombatClientSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.materials.world.OreGenerator;
import com.legobmw99.allomancy.modules.powers.PowersSetup;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.util.AllomancyConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
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
    public static final CreativeModeTab allomancy_group = new CreativeModeTab(MODID) {
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CombatClientSetup::registerEntityRenders);

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

    public static Item createStandardItem() {
        return new Item(createStandardItemProperties());
    }


    public static MutableComponent addColorToText(String translationKey, ChatFormatting color) {
        MutableComponent lore = new TranslatableComponent(translationKey);
        return addColor(lore, color);
    }

    public static MutableComponent addColorToText(String translationKey, ChatFormatting color, Object... fmting) {
        MutableComponent lore = new TranslatableComponent(translationKey, fmting);
        return addColor(lore, color);
    }

    private static MutableComponent addColor(MutableComponent text, ChatFormatting color) {
        text.setStyle(text.getStyle().withColor(TextColor.fromLegacyFormat(color)));
        return text;
    }

    public static void clientInit(final FMLClientSetupEvent e) {
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
