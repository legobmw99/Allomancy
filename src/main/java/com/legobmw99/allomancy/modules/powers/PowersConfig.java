package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.setup.AllomancyConfig;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PowersConfig {

    public static ForgeConfigSpec.IntValue max_metal_detection;
    public static ForgeConfigSpec.BooleanValue animate_selection;
    public static ForgeConfigSpec.BooleanValue enable_overlay;
    public static ForgeConfigSpec.EnumValue<SCREEN_LOC> overlay_position;
    public static ForgeConfigSpec.BooleanValue random_mistings;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> cfg_whitelist;
    public static Set<String> whitelist = new HashSet<>();
    private static ArrayList<String> defaultList;

    public static void init(ForgeConfigSpec.Builder common_builder, ForgeConfigSpec.Builder client_builder) {

        common_builder.comment("Settings for the gameplay elements of the mod").push("Gameplay");
        random_mistings = common_builder.comment("Spawn players as a random Misting").define("random_mistings", true);
        cfg_whitelist = common_builder.comment("List of registry names of items and blocks that are counted as 'metal").defineList("whitelist", default_whitelist(), o -> o instanceof String);
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

    private static List<String> default_whitelist() {
        defaultList = new ArrayList<>();

        add(Items.IRON_AXE);
        add(Items.GOLDEN_AXE);
        add(Items.CHAINMAIL_BOOTS);
        add(Items.GOLDEN_BOOTS);
        add(Items.IRON_BOOTS);
        add(Items.BUCKET);
        add(Items.LAVA_BUCKET);
        add(Items.MILK_BUCKET);
        add(Items.WATER_BUCKET);
        add(Items.CAULDRON);
        add(Items.COMPASS);
        add(Items.FLINT_AND_STEEL);
        add(Items.CHAINMAIL_HELMET);
        add(Items.GOLDEN_HELMET);
        add(Items.IRON_HELMET);
        add(Items.GOLDEN_HOE);
        add(Items.IRON_HOE);
        add(Items.GOLDEN_HORSE_ARMOR);
        add(Items.IRON_HORSE_ARMOR);
        add(Items.CHAINMAIL_LEGGINGS);
        add(Items.GOLDEN_LEGGINGS);
        add(Items.IRON_LEGGINGS);
        add(Items.MINECART);
        add(Items.CHEST_MINECART);
        add(Items.HOPPER_MINECART);
        add(Items.FURNACE_MINECART);
        add(Items.TNT_MINECART);
        add(Items.IRON_PICKAXE);
        add(Items.GOLDEN_PICKAXE);
        add(Items.IRON_CHESTPLATE);
        add(Items.CHAINMAIL_CHESTPLATE);
        add(Items.GOLDEN_CHESTPLATE);
        add(Items.CLOCK);
        add(Items.GOLDEN_SHOVEL);
        add(Items.IRON_SHOVEL);
        add(Items.SHEARS);
        add(Items.GOLDEN_APPLE);
        add(Items.ENCHANTED_GOLDEN_APPLE);
        add(Items.GOLDEN_CARROT);
        add(Items.IRON_SWORD);
        add(Items.IRON_INGOT);
        add(Items.IRON_NUGGET);
        add(Items.GOLD_INGOT);
        add(Items.GOLD_NUGGET);
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
        add(Blocks.IRON_TRAPDOOR);
        add(Blocks.IRON_DOOR);
        add(Blocks.CAULDRON);
        add(Blocks.IRON_BARS);
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
        add(Blocks.IRON_BLOCK);
        add(Blocks.IRON_ORE);
        add(Blocks.GOLD_BLOCK);
        add(Blocks.GOLD_ORE);
        add(Blocks.LANTERN);
        add(Blocks.TRAPPED_CHEST);
        add(Blocks.TRIPWIRE);
        add(Blocks.NETHER_GOLD_ORE);
        add(Blocks.SOUL_LANTERN);
        add(Blocks.NETHERITE_BLOCK);
        add(Blocks.ANCIENT_DEBRIS); // OP? TODO: consider if this should not be here, alongside scrap
        add(Blocks.LODESTONE);
        add(Blocks.GILDED_BLACKSTONE);

        add("allomancy:vial");
        add("allomancy:iron_lever");
        add("allomancy:iron_button");
        add("allomancy:lerasium_nugget");
        add("allomancy:allomantic_grinder");
        add("allomancy:coin_bag");
        add("allomancy:cadmium_ore");
        add("allomancy:chromium_ore");
        add("allomancy:copper_ore");
        add("allomancy:silver_ore");
        add("allomancy:tin_ore");
        add("allomancy:lead_ore");
        add("allomancy:zinc_ore");
        add("allomancy:cadmium_ingot");
        add("allomancy:chromium_ingot");
        add("allomancy:copper_ingot");
        add("allomancy:silver_ingot");
        add("allomancy:tin_ingot");
        add("allomancy:lead_ingot");
        add("allomancy:zinc_ingot");
        add("allomancy:bronze_ingot");
        add("allomancy:brass_ingot");


        for (Metal mt : Metal.values()) {
            if (mt != Metal.ALUMINUM)
                add("allomancy:" + mt.getName() + "_flakes");
        }
        add("allomancy:lead_flakes");
        add("allomancy:silver_flakes");

        defaultList.sort(String::compareTo);

        return defaultList;

    }

    private static void add(String s) {
        Allomancy.LOGGER.debug("Adding " + s + " to the default whitelist!");
        defaultList.add(s);
    }

    private static void add(Item i) {
        add(i.getRegistryName().toString());
    }

    private static void add(Block b) {
        add(b.getRegistryName().toString());
    }

    public enum SCREEN_LOC {
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT
    }

}
