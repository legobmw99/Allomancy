package com.legobmw99.allomancy.setup;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AllomancyConfig {

    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }


    public static boolean generate_copper;
    public static boolean generate_tin;
    public static boolean generate_lead;
    public static boolean generate_zinc;
    public static int copper_density;
    public static int tin_density;
    public static int lead_density;
    public static int zinc_density;
    public static int copper_min_y;
    public static int copper_max_y;
    public static int tin_min_y;
    public static int tin_max_y;
    public static int lead_min_y;
    public static int lead_max_y;
    public static int zinc_min_y;
    public static int zinc_max_y;
    public static boolean random_mistings;
    public static final Set<String> whitelist = new HashSet<String>();

    public static int max_metal_detection;
    public static boolean animate_selection;
    public static SCREEN_LOC overlay_position;

    public static class CommonConfig {
        public final ForgeConfigSpec.BooleanValue random_mistings;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public final ForgeConfigSpec.BooleanValue generate_copper;
        public final ForgeConfigSpec.BooleanValue generate_tin;
        public final ForgeConfigSpec.BooleanValue generate_lead;
        public final ForgeConfigSpec.BooleanValue generate_zinc;
        public final ForgeConfigSpec.IntValue copper_density;
        public final ForgeConfigSpec.IntValue tin_density;
        public final ForgeConfigSpec.IntValue lead_density;
        public final ForgeConfigSpec.IntValue zinc_density;
        public final ForgeConfigSpec.IntValue copper_min_y;
        public final ForgeConfigSpec.IntValue copper_max_y;
        public final ForgeConfigSpec.IntValue tin_min_y;
        public final ForgeConfigSpec.IntValue tin_max_y;
        public final ForgeConfigSpec.IntValue lead_min_y;
        public final ForgeConfigSpec.IntValue lead_max_y;
        public final ForgeConfigSpec.IntValue zinc_min_y;
        public final ForgeConfigSpec.IntValue zinc_max_y;

        CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Settings for the mod's added ore generation").push("WorldGen");
            builder.push("Copper");
            generate_copper = builder.comment("Generate Copper Ore").define("generate_copper", true);
            copper_density = builder.comment("Density of Copper Ore").defineInRange("copper_density", 15, 1, 40);
            copper_min_y = builder.comment("Minimum Y Level to Generate Copper").defineInRange("copper_min_y", 30, 1, 128);
            copper_max_y = builder.comment("Maximum Y Level to Generate Copper").defineInRange("copper_max_y", 50, 1, 128);
            builder.pop();

            builder.push("Tin");
            generate_tin = builder.comment("Generate Tin Ore").define("generate_tin", true);
            tin_density = builder.comment("Density of Tin Ore").defineInRange("tin_density", 15, 1, 40);
            tin_min_y = builder.comment("Minimum Y Level to Generate Tin").defineInRange("tin_min_y", 40, 1, 128);
            tin_max_y = builder.comment("Maximum Y Level to Generate Tin").defineInRange("tin_max_y", 64, 1, 128);
            builder.pop();

            builder.push("Lead");
            generate_lead = builder.comment("Generate Lead Ore").define("generate_lead", true);
            lead_density = builder.comment("Density of Lead Ore").defineInRange("lead_density", 15, 1, 40);
            lead_min_y = builder.comment("Minimum Y Level to Generate Lead").defineInRange("lead_min_y", 20, 1, 128);
            lead_max_y = builder.comment("Maximum Y Level to Generate Lead").defineInRange("lead_max_y", 40, 1, 128);
            builder.pop();

            builder.push("Zinc");
            generate_zinc = builder.comment("Generate Zinc Ore").define("generate_zinc", true);
            zinc_density = builder.comment("Density of Zinc Ore").defineInRange("zinc_density", 12, 1, 40);
            zinc_min_y = builder.comment("Minimum Y Level to Generate Zinc").defineInRange("zinc_min_y", 20, 1, 128);
            zinc_max_y = builder.comment("Maximum Y Level to Generate Zinc").defineInRange("zinc_max_y", 40, 1, 128);
            builder.pop();

            builder.pop();

            builder.comment("Settings for the gameplay elements of the mod").push("Gameplay");
            random_mistings = builder.comment("Spawn players as a random Misting").define("random_mistings", true);
            whitelist = builder.comment("List of registry names of items and blocks that are counted as 'metal").defineList("whitelist", default_whitelist(), o -> o instanceof String);
            builder.pop();
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


            for (int i = 0; i < Registry.flake_metals.length; i++) {
                defaultList.add("allomancy:" + Registry.flake_metals[i] + "_flakes");
            }

            /* todo investigate
            TagCollection<Item> tc = ItemTags.getCollection(); //get everything from item tags, in theory
            Stream<ResourceLocation> rs =  tc.getRegisteredTags().stream().filter(p -> isMetalName(p.toString()));
            rs.forEach(r -> tc.get(r).getAllElements().stream().map(i -> i.getRegistryName().toString()).forEach(defaultList::add));*/

            defaultList.sort(String::compareTo);

            return defaultList;
        }

        /*private static boolean isMetalName(String s){
            return s.contains("copper") || s.contains("tin") || s.contains("gold") || s.contains("iron")
                    || s.contains("steel") || s.contains("lead") || s.contains("silver") || s.contains("brass")
                    || s.contains("bronze") || s.contains("aluminum") || s.contains("zinc");
        }*/
    }

    public static void refreshCommon() {
        random_mistings = COMMON.random_mistings.get();
        generate_copper = COMMON.generate_copper.get();
        generate_tin = COMMON.generate_tin.get();
        generate_lead = COMMON.generate_lead.get();
        generate_zinc = COMMON.generate_zinc.get();
        copper_density = COMMON.copper_density.get();
        tin_density = COMMON.tin_density.get();
        lead_density = COMMON.lead_density.get();
        zinc_density = COMMON.zinc_density.get();
        copper_min_y = COMMON.copper_min_y.get();
        copper_max_y = COMMON.copper_max_y.get();
        tin_min_y = COMMON.tin_min_y.get();
        tin_max_y = COMMON.tin_max_y.get();
        lead_min_y = COMMON.lead_min_y.get();
        lead_max_y = COMMON.lead_max_y.get();
        zinc_min_y = COMMON.zinc_min_y.get();
        zinc_max_y = COMMON.zinc_max_y.get();
        whitelist.clear();
        whitelist.addAll(COMMON.whitelist.get());
    }


    public static class ClientConfig {
        public final ForgeConfigSpec.IntValue max_metal_detection;
        public final ForgeConfigSpec.BooleanValue animate_selection;
        public final ForgeConfigSpec.EnumValue<SCREEN_LOC> overlay_position;

        ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("Graphics");
            max_metal_detection = builder.comment("Maximum iron/steelsight distance").defineInRange("max_metal_distance", 15, 3, 30);
            animate_selection = builder.comment("Animate the selection wheel").define("animate_selection", true);
            overlay_position = builder.comment("Screen Overlay Position").comment("Options: TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT").defineEnum("overlay_position", SCREEN_LOC.TOP_RIGHT);
            builder.pop();
        }
    }

    public static void refreshClient() {
        max_metal_detection = CLIENT.max_metal_detection.get();
        animate_selection = CLIENT.animate_selection.get();
        overlay_position = CLIENT.overlay_position.get();
    }

    public enum SCREEN_LOC {
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT
    }
}
