package com.legobmw99.allomancy.modules.materials;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MaterialsConfig {
    public static ModConfigSpec.BooleanValue generate_lerasium;
    public static ModConfigSpec.BooleanValue generate_unbreakable_daggers;

    public static void init(ModConfigSpec.Builder common_builder) {
        common_builder.comment("Settings for the mod's added generation").push("world");

        common_builder.push("loot");
        generate_lerasium = common_builder.comment("Add Lerasium to dungeon and other loot tables").define("generate_lerasium", true);
        generate_unbreakable_daggers = common_builder.comment("Add Unbreakable Obsidian Daggers to end city and other loot tables").define("generate_unbreakable_daggers", true);
        common_builder.pop();
        common_builder.pop();
    }

}
