package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.powers.util.PowerUtils;
import com.legobmw99.allomancy.setup.AllomancyConfig;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.*;

public class PowersConfig {

    public static ForgeConfigSpec.IntValue max_metal_detection;
    public static ForgeConfigSpec.BooleanValue animate_selection;
    public static ForgeConfigSpec.BooleanValue enable_overlay;
    public static ForgeConfigSpec.EnumValue<SCREEN_LOC> overlay_position;
    public static ForgeConfigSpec.BooleanValue random_mistings;
    public static ForgeConfigSpec.BooleanValue generate_whitelist;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> cfg_whitelist;
    public static Set<String> whitelist = new HashSet<>();

    private static HashSet<String> defaultList;

    public static void init(ForgeConfigSpec.Builder common_builder, ForgeConfigSpec.Builder client_builder) {

        common_builder.comment("Settings for the gameplay elements of the mod").push("Gameplay");
        random_mistings = common_builder.comment("Spawn players as a random Misting").define("random_mistings", true);
        generate_whitelist = common_builder.comment("Regenerate the metal whitelist").define("regenerate_whitelist", true);
        cfg_whitelist = common_builder.comment("List of registry names of items and blocks that are counted as 'metal").defineList("whitelist", new ArrayList<>(), o -> o instanceof String);
        common_builder.pop();

        client_builder.push("Graphics");
        max_metal_detection = client_builder.comment("Maximum iron/steelsight distance").defineInRange("max_metal_distance", 15, 3, 30);
        animate_selection = client_builder.comment("Animate the selection wheel").define("animate_selection", true);
        enable_overlay = client_builder.comment("Enable the screen overlay").define("overlay_enabled", true);
        overlay_position = client_builder.comment("Screen Overlay Position").defineEnum("overlay_position", SCREEN_LOC.TOP_LEFT);
        client_builder.pop();
    }

    public static void refresh(final ModConfig.ModConfigEvent e) {
        ModConfig cfg = e.getConfig();
        if (cfg.getSpec() == AllomancyConfig.COMMON_CONFIG) {
            refresh_whitelist();
        }
    }

    private static void refresh_whitelist() {
        whitelist.clear();
        whitelist.addAll(cfg_whitelist.get());
    }

    public static void load_whitelist(final ModConfig.Loading e) {
        ModConfig cfg = e.getConfig();
        if (cfg.getSpec() == AllomancyConfig.COMMON_CONFIG) {
            if (generate_whitelist.get()) {
                ArrayList<String> list = new ArrayList<>(default_whitelist());
                list.sort(String::compareTo);
                cfg_whitelist.set(list);
                generate_whitelist.set(false);
            }
            refresh_whitelist();
        }
    }

    private static Set<String> default_whitelist() {
        defaultList = new HashSet<>();

        add(Items.CHAINMAIL_BOOTS);
        add(Items.BUCKET);
        add(Items.LAVA_BUCKET);
        add(Items.MILK_BUCKET);
        add(Items.WATER_BUCKET);
        add(Items.CAULDRON);
        add(Items.COMPASS);
        add(Items.CHAINMAIL_HELMET);
        add(Items.CHAINMAIL_LEGGINGS);
        add(Items.MINECART);
        add(Items.CHEST_MINECART);
        add(Items.HOPPER_MINECART);
        add(Items.FURNACE_MINECART);
        add(Items.TNT_MINECART);
        add(Items.CHAINMAIL_CHESTPLATE);
        add(Items.CLOCK);
        add(Items.SHEARS);
        add(Items.NETHERITE_INGOT);
        add(Items.NETHERITE_SCRAP); // Op?
        add(Items.NETHERITE_HELMET);
        add(Items.NETHERITE_CHESTPLATE);
        add(Items.NETHERITE_LEGGINGS);
        add(Items.NETHERITE_BOOTS);
        add(Items.NETHERITE_HOE);
        add(Items.NETHERITE_PICKAXE);
        add(Items.NETHERITE_SHOVEL);
        add(Items.NETHERITE_SWORD);
        add(Items.NETHERITE_AXE);

        add(Blocks.ANVIL);
        add(Blocks.CAULDRON);
        add(Blocks.HOPPER);
        add(Blocks.PISTON_HEAD);
        add(Blocks.MOVING_PISTON);
        add(Blocks.STICKY_PISTON);
        add(Blocks.BELL);
        add(Blocks.PISTON);
        add(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        add(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        add(Blocks.RAIL);
        add(Blocks.ACTIVATOR_RAIL);
        add(Blocks.DETECTOR_RAIL);
        add(Blocks.POWERED_RAIL);
        add(Blocks.LANTERN);
        add(Blocks.TRAPPED_CHEST);
        add(Blocks.TRIPWIRE);
        add(Blocks.SOUL_LANTERN);
        add(Blocks.NETHERITE_BLOCK);
        add(Blocks.ANCIENT_DEBRIS); // OP? TODO: consider if this should not be here, alongside scrap
        add(Blocks.LODESTONE);
        add(Blocks.GILDED_BLACKSTONE);

        add(ConsumeSetup.VIAL.get());
        add(ConsumeSetup.LERASIUM_NUGGET.get());
        add(ConsumeSetup.ALLOMANTIC_GRINDER.get());
        add(CombatSetup.COIN_BAG.get());


        ForgeRegistries.ITEMS.getValues()
                .stream()
                .map(ForgeRegistryEntry::getRegistryName)
                .filter(Objects::nonNull)
                .filter(PowerUtils::resourceContainsMetal)
                .forEach(PowersConfig::add);

        ForgeRegistries.BLOCKS.getValues()
                .stream()
                .map(ForgeRegistryEntry::getRegistryName)
                .filter(Objects::nonNull)
                .filter(PowerUtils::resourceContainsMetal)
                .forEach(PowersConfig::add);

        return defaultList;

    }


    private static void add(String s) {
        Allomancy.LOGGER.debug("Adding " + s + " to the default whitelist!");
        defaultList.add(s);
    }

    private static void add(ResourceLocation r) {
        add(r.toString());
    }

    private static void add(ForgeRegistryEntry<?> i) {
        add(i.getRegistryName());
    }


    public enum SCREEN_LOC {
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT
    }

}
