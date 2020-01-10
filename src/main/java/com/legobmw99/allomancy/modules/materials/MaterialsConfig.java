package com.legobmw99.allomancy.modules.materials;

import net.minecraftforge.common.ForgeConfigSpec;

public class MaterialsConfig {
    public static ForgeConfigSpec.BooleanValue generate_lerasium;
    public static ForgeConfigSpec.BooleanValue generate_unbreakable_daggers;

    public static ForgeConfigSpec.BooleanValue generate_aluminum;
    public static ForgeConfigSpec.BooleanValue generate_cadmium;
    public static ForgeConfigSpec.BooleanValue generate_chromium;
    public static ForgeConfigSpec.BooleanValue generate_copper;
    public static ForgeConfigSpec.BooleanValue generate_lead;
    public static ForgeConfigSpec.BooleanValue generate_tin;
    public static ForgeConfigSpec.BooleanValue generate_silver;
    public static ForgeConfigSpec.BooleanValue generate_zinc;

    public static ForgeConfigSpec.IntValue aluminum_density;
    public static ForgeConfigSpec.IntValue cadmium_density;
    public static ForgeConfigSpec.IntValue chromium_density;
    public static ForgeConfigSpec.IntValue copper_density;
    public static ForgeConfigSpec.IntValue lead_density;
    public static ForgeConfigSpec.IntValue tin_density;
    public static ForgeConfigSpec.IntValue silver_density;
    public static ForgeConfigSpec.IntValue zinc_density;

    public static ForgeConfigSpec.IntValue aluminum_min_y;
    public static ForgeConfigSpec.IntValue aluminum_max_y;
    public static ForgeConfigSpec.IntValue cadmium_min_y;
    public static ForgeConfigSpec.IntValue cadmium_max_y;
    public static ForgeConfigSpec.IntValue chromium_min_y;
    public static ForgeConfigSpec.IntValue chromium_max_y;
    public static ForgeConfigSpec.IntValue copper_min_y;
    public static ForgeConfigSpec.IntValue copper_max_y;
    public static ForgeConfigSpec.IntValue lead_min_y;
    public static ForgeConfigSpec.IntValue lead_max_y;
    public static ForgeConfigSpec.IntValue silver_min_y;
    public static ForgeConfigSpec.IntValue silver_max_y;
    public static ForgeConfigSpec.IntValue tin_min_y;
    public static ForgeConfigSpec.IntValue tin_max_y;
    public static ForgeConfigSpec.IntValue zinc_min_y;
    public static ForgeConfigSpec.IntValue zinc_max_y;

    public static void init(ForgeConfigSpec.Builder common_builder, ForgeConfigSpec.Builder client_builder) {
        common_builder.comment("Settings for the mod's added generation").push("WorldGen");

        common_builder.push("Aluminum");
        generate_aluminum = common_builder.comment("Generate Aluminum Ore").define("generate_aluminum", true);
        aluminum_density = common_builder.comment("Density of Aluminum Ore").defineInRange("aluminum_density", 15, 1, 40);
        aluminum_min_y = common_builder.comment("Minimum Y Level to Generate Aluminum").defineInRange("aluminum_min_y", 25, 1, 128);
        aluminum_max_y = common_builder.comment("Maximum Y Level to Generate Aluminum").defineInRange("aluminum_max_y", 55, 1, 128);
        common_builder.pop();

        common_builder.push("Cadmium");
        generate_cadmium = common_builder.comment("Generate Cadmium Ore").define("generate_cadmium", true);
        cadmium_density = common_builder.comment("Density of Cadmium Ore").defineInRange("cadmium_density", 5, 1, 40);
        cadmium_min_y = common_builder.comment("Minimum Y Level to Cadmium Aluminum").defineInRange("cadmium_min_y", 30, 1, 128);
        cadmium_max_y = common_builder.comment("Maximum Y Level to Cadmium Aluminum").defineInRange("cadmium_max_y", 50, 1, 128);
        common_builder.pop();

        common_builder.push("Chromium");
        generate_chromium = common_builder.comment("Generate Chromium Ore").define("generate_chromium", true);
        chromium_density = common_builder.comment("Density of Chromium Ore").defineInRange("chromium_density", 8, 1, 40);
        chromium_min_y = common_builder.comment("Minimum Y Level to Generate Chromium").defineInRange("chromium_min_y", 20, 1, 128);
        chromium_max_y = common_builder.comment("Maximum Y Level to Generate Chromium").defineInRange("chromium_max_y", 45, 1, 128);
        common_builder.pop();

        common_builder.push("Copper");
        generate_copper = common_builder.comment("Generate Copper Ore").define("generate_copper", true);
        copper_density = common_builder.comment("Density of Copper Ore").defineInRange("copper_density", 15, 1, 40);
        copper_min_y = common_builder.comment("Minimum Y Level to Generate Copper").defineInRange("copper_min_y", 30, 1, 128);
        copper_max_y = common_builder.comment("Maximum Y Level to Generate Copper").defineInRange("copper_max_y", 50, 1, 128);
        common_builder.pop();

        common_builder.push("Lead");
        generate_lead = common_builder.comment("Generate Lead Ore").define("generate_lead", true);
        lead_density = common_builder.comment("Density of Lead Ore").defineInRange("lead_density", 15, 1, 40);
        lead_min_y = common_builder.comment("Minimum Y Level to Generate Lead").defineInRange("lead_min_y", 20, 1, 128);
        lead_max_y = common_builder.comment("Maximum Y Level to Generate Lead").defineInRange("lead_max_y", 40, 1, 128);
        common_builder.pop();

        common_builder.push("Silver");
        generate_silver = common_builder.comment("Generate Silver Ore").define("generate_silver", true);
        silver_density = common_builder.comment("Density of Silver Ore").defineInRange("silver_density", 11, 1, 40);
        silver_min_y = common_builder.comment("Minimum Y Level to Generate Silver").defineInRange("silver_min_y", 16, 1, 128);
        silver_max_y = common_builder.comment("Maximum Y Level to Generate Silver").defineInRange("silver_max_y", 46, 1, 128);
        common_builder.pop();

        common_builder.push("Tin");
        generate_tin = common_builder.comment("Generate Tin Ore").define("generate_tin", true);
        tin_density = common_builder.comment("Density of Tin Ore").defineInRange("tin_density", 15, 1, 40);
        tin_min_y = common_builder.comment("Minimum Y Level to Generate Tin").defineInRange("tin_min_y", 40, 1, 128);
        tin_max_y = common_builder.comment("Maximum Y Level to Generate Tin").defineInRange("tin_max_y", 64, 1, 128);
        common_builder.pop();


        common_builder.push("Zinc");
        generate_zinc = common_builder.comment("Generate Zinc Ore").define("generate_zinc", true);
        zinc_density = common_builder.comment("Density of Zinc Ore").defineInRange("zinc_density", 12, 1, 40);
        zinc_min_y = common_builder.comment("Minimum Y Level to Generate Zinc").defineInRange("zinc_min_y", 20, 1, 128);
        zinc_max_y = common_builder.comment("Maximum Y Level to Generate Zinc").defineInRange("zinc_max_y", 40, 1, 128);
        common_builder.pop();

        common_builder.push("Loot Injection");
        generate_lerasium = common_builder.comment("Add Lerasium to dungeon and other loot tables").define("generate_lerasium", true);
        generate_unbreakable_daggers = common_builder.comment("Add Unbreakable Obsidian Daggers to end city and other loot tables").define("generate_unbreakable_daggers", true);
        common_builder.pop();

        common_builder.pop();
    }
}
