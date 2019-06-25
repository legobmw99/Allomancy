package com.legobmw99.allomancy.util;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;


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


    public static boolean random_mistings;
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


    public static int max_metal_detection;
    public static boolean animate_selection;
    public static SCREEN_LOC overlay_position;

    public static class CommonConfig {
        public final ForgeConfigSpec.BooleanValue random_mistings;
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
            builder.push("Gameplay");
            random_mistings = builder.comment("Spawn players as a random Misting").define("random_mistings", true);
            //todo investigate whitelist here
            builder.pop();
            builder.push("World Gen");
            generate_copper = builder.comment("Generate Copper Ore").define("generate_copper", true);
            generate_tin = builder.comment("Generate Tin Ore").define("generate_tin", true);
            generate_lead = builder.comment("Generate Lead Ore").define("generate_lead", true);
            generate_zinc = builder.comment("Generate Zinc Ore").define("generate_zinc", true);
            copper_density = builder.comment("Density of Copper Ore").defineInRange("copper_density", 5, 1, 20);
            tin_density = builder.comment("Density of Copper Ore").defineInRange("copper_density", 5, 1, 20);
            lead_density = builder.comment("Density of Copper Ore").defineInRange("copper_density", 5, 1, 20);
            zinc_density = builder.comment("Density of Copper Ore").defineInRange("copper_density", 5, 1, 20);
            copper_min_y = builder.comment("Minimum Y Level to Generate Copper").defineInRange("copper_min_y", 30, 1, 128);
            copper_max_y = builder.comment("Maximum Y Level to Generate Copper").defineInRange("copper_max_y", 50, 1, 128);
            tin_min_y = builder.comment("Minimum Y Level to Generate Tin").defineInRange("tin_min_y", 40, 1, 128);
            tin_max_y = builder.comment("Maximum Y Level to Generate Tin").defineInRange("tin_max_y", 64, 1, 128);
            lead_min_y = builder.comment("Minimum Y Level to Generate Lead").defineInRange("lead_min_y", 20, 1, 128);
            lead_max_y = builder.comment("Maximum Y Level to Generate Lead").defineInRange("lead_max_y", 40, 1, 128);
            zinc_min_y = builder.comment("Minimum Y Level to Generate Zinc").defineInRange("zinc_min_y", 20, 1, 128);
            zinc_max_y = builder.comment("Maximum Y Level to Generate Zinc").defineInRange("zinc_max_y", 40, 1, 128);
            builder.pop();
        }
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
    }

    public static class ClientConfig {
        public final ForgeConfigSpec.IntValue max_metal_detection;
        public final ForgeConfigSpec.BooleanValue animate_selection;
        public final ForgeConfigSpec.EnumValue<SCREEN_LOC> overlay_position;

        ClientConfig(ForgeConfigSpec.Builder builder){
            builder.push("Graphics");
            max_metal_detection = builder.comment("Maximum iron/steelsight distance").defineInRange("max_metal_distance",12,3,30);
            animate_selection = builder.comment("Animate the selection wheel").define("animate_selection", true);
            overlay_position = builder.comment("Screen Overlay Position").defineEnum("overlay_position", SCREEN_LOC.TOP_LEFT);
            builder.pop();
        }
    }

    public static void refreshClient(){
        max_metal_detection = CLIENT.max_metal_detection.get();
        animate_selection = CLIENT.animate_selection.get();
        overlay_position = CLIENT.overlay_position.get();
    }

    public enum SCREEN_LOC{
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT
    }
}
