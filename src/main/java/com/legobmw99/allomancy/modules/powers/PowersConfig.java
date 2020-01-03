package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.setup.AllomancyConfig;
import com.legobmw99.allomancy.setup.AllomancySetup;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PowersConfig {

    public enum SCREEN_LOC {
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT
    }

    public static ForgeConfigSpec.IntValue max_metal_detection;
    public static ForgeConfigSpec.BooleanValue animate_selection;
    public static ForgeConfigSpec.EnumValue<SCREEN_LOC> overlay_position;
    public static ForgeConfigSpec.BooleanValue random_mistings;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> cfg_whitelist;

    public static Set<String> whitelist = new HashSet<>();

    public static void init(ForgeConfigSpec.Builder common_builder, ForgeConfigSpec.Builder client_builder) {

        common_builder.comment("Settings for the gameplay elements of the mod").push("Gameplay");
        random_mistings = common_builder.comment("Spawn players as a random Misting").define("random_mistings", true);
        cfg_whitelist = common_builder.comment("List of registry names of items and blocks that are counted as 'metal").defineList("whitelist", default_whitelist(), o -> o instanceof String);
        common_builder.pop();

        client_builder.push("Graphics");
        max_metal_detection = client_builder.comment("Maximum iron/steelsight distance").defineInRange("max_metal_distance", 15, 3, 30);
        animate_selection = client_builder.comment("Animate the selection wheel").define("animate_selection", true);
        overlay_position = client_builder.comment("Screen Overlay Position").defineEnum("overlay_position", SCREEN_LOC.TOP_RIGHT);
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
        ArrayList<String> defaultList = new ArrayList<>();
        defaultList.add(Items.IRON_AXE.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_AXE.getRegistryName().toString());
        defaultList.add(Items.CHAINMAIL_BOOTS.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_BOOTS.getRegistryName().toString());
        defaultList.add(Items.IRON_BOOTS.getRegistryName().toString());
        defaultList.add(Items.BUCKET.getRegistryName().toString());
        defaultList.add(Items.LAVA_BUCKET.getRegistryName().toString());
        defaultList.add(Items.MILK_BUCKET.getRegistryName().toString());
        defaultList.add(Items.WATER_BUCKET.getRegistryName().toString());
        defaultList.add(Items.CAULDRON.getRegistryName().toString());
        defaultList.add(Items.COMPASS.getRegistryName().toString());
        defaultList.add(Items.FLINT_AND_STEEL.getRegistryName().toString());
        defaultList.add(Items.CHAINMAIL_HELMET.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_HELMET.getRegistryName().toString());
        defaultList.add(Items.IRON_HELMET.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_HOE.getRegistryName().toString());
        defaultList.add(Items.IRON_HOE.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_HORSE_ARMOR.getRegistryName().toString());
        defaultList.add(Items.IRON_HORSE_ARMOR.getRegistryName().toString());
        defaultList.add(Items.CHAINMAIL_LEGGINGS.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_LEGGINGS.getRegistryName().toString());
        defaultList.add(Items.IRON_LEGGINGS.getRegistryName().toString());
        defaultList.add(Items.MINECART.getRegistryName().toString());
        defaultList.add(Items.CHEST_MINECART.getRegistryName().toString());
        defaultList.add(Items.HOPPER_MINECART.getRegistryName().toString());
        defaultList.add(Items.FURNACE_MINECART.getRegistryName().toString());
        defaultList.add(Items.TNT_MINECART.getRegistryName().toString());
        defaultList.add(Items.IRON_PICKAXE.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_PICKAXE.getRegistryName().toString());
        defaultList.add(Items.IRON_CHESTPLATE.getRegistryName().toString());
        defaultList.add(Items.CHAINMAIL_CHESTPLATE.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_CHESTPLATE.getRegistryName().toString());
        defaultList.add(Items.CLOCK.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_SHOVEL.getRegistryName().toString());
        defaultList.add(Items.IRON_SHOVEL.getRegistryName().toString());
        defaultList.add(Items.SHEARS.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_APPLE.getRegistryName().toString());
        defaultList.add(Items.ENCHANTED_GOLDEN_APPLE.getRegistryName().toString());
        defaultList.add(Items.GOLDEN_CARROT.getRegistryName().toString());
        defaultList.add(Items.IRON_SWORD.getRegistryName().toString());
        defaultList.add(Items.IRON_NUGGET.getRegistryName().toString());
        defaultList.add(Items.IRON_INGOT.getRegistryName().toString());
        defaultList.add(Items.GOLD_NUGGET.getRegistryName().toString());
        defaultList.add(Items.GOLD_INGOT.getRegistryName().toString());

        defaultList.add(Blocks.ANVIL.getRegistryName().toString());
        defaultList.add(Blocks.IRON_TRAPDOOR.getRegistryName().toString());
        defaultList.add(Blocks.IRON_DOOR.getRegistryName().toString());
        defaultList.add(Blocks.CAULDRON.getRegistryName().toString());
        defaultList.add(Blocks.IRON_BARS.getRegistryName().toString());
        defaultList.add(Blocks.HOPPER.getRegistryName().toString());
        defaultList.add(Blocks.PISTON_HEAD.getRegistryName().toString());
        defaultList.add(Blocks.MOVING_PISTON.getRegistryName().toString());
        defaultList.add(Blocks.STICKY_PISTON.getRegistryName().toString());
        defaultList.add(Blocks.BELL.getRegistryName().toString());
        defaultList.add(Blocks.PISTON.getRegistryName().toString());
        defaultList.add(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getRegistryName().toString());
        defaultList.add(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getRegistryName().toString());
        defaultList.add(Blocks.RAIL.getRegistryName().toString());
        defaultList.add(Blocks.ACTIVATOR_RAIL.getRegistryName().toString());
        defaultList.add(Blocks.DETECTOR_RAIL.getRegistryName().toString());
        defaultList.add(Blocks.POWERED_RAIL.getRegistryName().toString());
        defaultList.add(Blocks.IRON_BLOCK.getRegistryName().toString());
        defaultList.add(Blocks.IRON_ORE.getRegistryName().toString());
        defaultList.add(Blocks.GOLD_BLOCK.getRegistryName().toString());
        defaultList.add(Blocks.GOLD_ORE.getRegistryName().toString());

        defaultList.add("allomancy:vial");
        defaultList.add("allomancy:iron_lever");
        defaultList.add("allomancy:iron_button");
        defaultList.add("allomancy:lerasium_nugget");
        defaultList.add("allomancy:allomantic_grinder");
        defaultList.add("allomancy:coin_bag");
        defaultList.add("allomancy:copper_ore");
        defaultList.add("allomancy:tin_ore");
        defaultList.add("allomancy:lead_ore");
        defaultList.add("allomancy:zinc_ore");
        defaultList.add("allomancy:copper_ingot");
        defaultList.add("allomancy:tin_ingot");
        defaultList.add("allomancy:lead_ingot");
        defaultList.add("allomancy:zinc_ingot");
        defaultList.add("allomancy:bronze_ingot");


        for (int i = 0; i < AllomancySetup.flake_metals.length; i++) {
            defaultList.add("allomancy:" + AllomancySetup.flake_metals[i] + "_flakes");
        }

        defaultList.sort(String::compareTo);

        return defaultList;
    }

}
