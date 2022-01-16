package com.legobmw99.allomancy.modules.materials;

import com.legobmw99.allomancy.api.enums.Metal;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class MaterialsConfig {
    public static final List<OreConfig> ores = new ArrayList<>();
    public static ForgeConfigSpec.BooleanValue generate_lerasium;
    public static ForgeConfigSpec.BooleanValue generate_unbreakable_daggers;
    private static int i = 0;

    public static void init(ForgeConfigSpec.Builder common_builder) {
        common_builder.comment("Settings for the mod's added generation").push("world");

        common_builder.push("loot");
        generate_lerasium = common_builder.comment("Add Lerasium to dungeon and other loot tables").define("generate_lerasium", true);
        generate_unbreakable_daggers = common_builder.comment("Add Unbreakable Obsidian Daggers to end city and other loot tables").define("generate_unbreakable_daggers", true);
        common_builder.pop();

        ores.add(new OreConfig(Metal.ALUMINUM, common_builder, 14, 9, 40, 120));
        ores.add(new OreConfig(Metal.CADMIUM, common_builder, 5, 7, -60, 0));
        ores.add(new OreConfig(Metal.CHROMIUM, common_builder, 8, 6, -30, 30));
        ores.add(new OreConfig("lead", common_builder, 15, 9, -40, 30));
        ores.add(new OreConfig("silver", common_builder, 11, 7, -40, 30));
        ores.add(new OreConfig(Metal.TIN, common_builder, 15, 11, 30, 112));
        ores.add(new OreConfig(Metal.ZINC, common_builder, 12, 8, 40, 80));


        common_builder.pop();
    }

    public enum PlacementType {
        TRIANGLE,
        UNIFORM;
    }

    public static class OreConfig {
        public final String name;
        public final int index;
        public ForgeConfigSpec.BooleanValue generate;
        public ForgeConfigSpec.IntValue per_chunk;
        public ForgeConfigSpec.IntValue vein_size;
        public ForgeConfigSpec.EnumValue<PlacementType> placement_type;
        public ForgeConfigSpec.IntValue min_y;
        public ForgeConfigSpec.IntValue max_y;

        public OreConfig(Metal mt, ForgeConfigSpec.Builder common_builder, int defaultDensity, int defaultSize, int defaultMinY, int defaultMaxY) {
            this(mt.getName(), common_builder, defaultDensity, defaultSize, defaultMinY, defaultMaxY);
        }

        public OreConfig(String name, ForgeConfigSpec.Builder common_builder, int defaultDensity, int defaultSize, int defaultMinY, int defaultMaxY) {
            this.name = name;
            this.index = i++;
            common_builder.comment("Generation Settings for " + name + " ore.").push(name);
            this.generate = common_builder.comment("Enable " + name + " generation").define("generate", true);
            this.per_chunk = common_builder.comment("Density of " + name + " Ore").defineInRange("per_chunk", defaultDensity, 1, 40);
            this.vein_size = common_builder.comment("Vein size of " + name + " Ore").defineInRange("size", defaultSize, 1, 60);
            this.placement_type = common_builder.comment("Whether " + name + " is placed in a uniform block or triangular").defineEnum("placement_type", PlacementType.TRIANGLE);
            this.min_y = common_builder.comment("Minimum Y Level to Generate " + name).defineInRange("min_y", defaultMinY, -128, 320);
            this.max_y = common_builder.comment("Maximum Y Level to Generate" + name).defineInRange("max_y", defaultMaxY, -64, 512);

            common_builder.pop();
        }

    }
}
