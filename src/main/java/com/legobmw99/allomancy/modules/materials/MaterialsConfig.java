package com.legobmw99.allomancy.modules.materials;

import net.minecraftforge.common.ForgeConfigSpec;

public class MaterialsConfig {
    public static ForgeConfigSpec.BooleanValue generate_copper;
    public static ForgeConfigSpec.BooleanValue generate_tin;
    public static ForgeConfigSpec.BooleanValue generate_lead;
    public static ForgeConfigSpec.BooleanValue generate_zinc;
    public static ForgeConfigSpec.IntValue copper_density;
    public static ForgeConfigSpec.IntValue tin_density;
    public static ForgeConfigSpec.IntValue lead_density;
    public static ForgeConfigSpec.IntValue zinc_density;
    public static ForgeConfigSpec.IntValue copper_min_y;
    public static ForgeConfigSpec.IntValue copper_max_y;
    public static ForgeConfigSpec.IntValue tin_min_y;
    public static ForgeConfigSpec.IntValue tin_max_y;
    public static ForgeConfigSpec.IntValue lead_min_y;
    public static ForgeConfigSpec.IntValue lead_max_y;
    public static ForgeConfigSpec.IntValue zinc_min_y;
    public static ForgeConfigSpec.IntValue zinc_max_y;

    public static void init(ForgeConfigSpec.Builder common_builder, ForgeConfigSpec.Builder client_builder) {
        common_builder.comment("Settings for the mod's added ore generation").push("WorldGen");
        common_builder.push("Copper");
        generate_copper = common_builder.comment("Generate Copper Ore").define("generate_copper", true);
        copper_density = common_builder.comment("Density of Copper Ore").defineInRange("copper_density", 15, 1, 40);
        copper_min_y = common_builder.comment("Minimum Y Level to Generate Copper").defineInRange("copper_min_y", 30, 1, 128);
        copper_max_y = common_builder.comment("Maximum Y Level to Generate Copper").defineInRange("copper_max_y", 50, 1, 128);
        common_builder.pop();

        common_builder.push("Tin");
        generate_tin = common_builder.comment("Generate Tin Ore").define("generate_tin", true);
        tin_density = common_builder.comment("Density of Tin Ore").defineInRange("tin_density", 15, 1, 40);
        tin_min_y = common_builder.comment("Minimum Y Level to Generate Tin").defineInRange("tin_min_y", 40, 1, 128);
        tin_max_y = common_builder.comment("Maximum Y Level to Generate Tin").defineInRange("tin_max_y", 64, 1, 128);
        common_builder.pop();

        common_builder.push("Lead");
        generate_lead = common_builder.comment("Generate Lead Ore").define("generate_lead", true);
        lead_density = common_builder.comment("Density of Lead Ore").defineInRange("lead_density", 15, 1, 40);
        lead_min_y = common_builder.comment("Minimum Y Level to Generate Lead").defineInRange("lead_min_y", 20, 1, 128);
        lead_max_y = common_builder.comment("Maximum Y Level to Generate Lead").defineInRange("lead_max_y", 40, 1, 128);
        common_builder.pop();

        common_builder.push("Zinc");
        generate_zinc = common_builder.comment("Generate Zinc Ore").define("generate_zinc", true);
        zinc_density = common_builder.comment("Density of Zinc Ore").defineInRange("zinc_density", 12, 1, 40);
        zinc_min_y = common_builder.comment("Minimum Y Level to Generate Zinc").defineInRange("zinc_min_y", 20, 1, 128);
        zinc_max_y = common_builder.comment("Maximum Y Level to Generate Zinc").defineInRange("zinc_max_y", 40, 1, 128);
        common_builder.pop();

        common_builder.pop();
    }
}
