package com.legobmw99.allomancy.modules.materials.world;

import com.google.common.collect.ImmutableList;
import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.ArrayList;

import static com.legobmw99.allomancy.modules.materials.MaterialsConfig.*;

public class OreGenerator {

    private static final ArrayList<OreData> ores = new ArrayList<>();

    static {
        int i = 0;
        ores.add(new OreData(aluminum_max_y, aluminum_min_y, aluminum_size, aluminum_density, MaterialsSetup.ORE_BLOCKS.get(i), MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i++),
                             generate_aluminum));
        ores.add(new OreData(cadmium_max_y, cadmium_min_y, cadmium_size, cadmium_density, MaterialsSetup.ORE_BLOCKS.get(i), MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i++),
                             generate_cadmium));
        ores.add(new OreData(chromium_max_y, chromium_min_y, chromium_size, cadmium_density, MaterialsSetup.ORE_BLOCKS.get(i), MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i++),
                             generate_chromium));
        ores.add(new OreData(lead_max_y, lead_min_y, lead_size, lead_density, MaterialsSetup.ORE_BLOCKS.get(i), MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i++), generate_lead));
        ores.add(new OreData(silver_max_y, silver_min_y, silver_size, silver_density, MaterialsSetup.ORE_BLOCKS.get(i), MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i++),
                             generate_silver));
        ores.add(new OreData(tin_max_y, tin_min_y, tin_size, tin_density, MaterialsSetup.ORE_BLOCKS.get(i), MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i++), generate_tin));
        ores.add(new OreData(zinc_max_y, zinc_min_y, zinc_size, zinc_density, MaterialsSetup.ORE_BLOCKS.get(i), MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i), generate_zinc));
    }

    public static void registerFeatures() {
        var registry = BuiltinRegistries.CONFIGURED_FEATURE;
        for (OreData ore : ores) {
            ResourceLocation block = ore.stone_ore_block.getRegistryName();
            Allomancy.LOGGER.info("Registering configured feature generation for block " + block.toString());
            ore.feature = featureFromData(ore);
            Registry.register(registry, block, ore.feature);
        }
    }

    public static void registerGeneration(BiomeLoadingEvent event) {
        var generation = event.getGeneration();
        for (OreData ore : ores) {
            if (ore.config_enabled) {
                generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ore.feature);
            }
        }
    }

    private static ConfiguredFeature<?, ?> featureFromData(OreData ore) {
        var targetList = ImmutableList.of(OreConfiguration.target(OreConfiguration.Predicates.STONE_ORE_REPLACEABLES, ore.stone_ore_block.defaultBlockState()),
                                          OreConfiguration.target(OreConfiguration.Predicates.DEEPSLATE_ORE_REPLACEABLES, ore.deepslate_ore_block.defaultBlockState()));
        return Feature.ORE
                .configured(new OreConfiguration(targetList, ore.vein_size))
                .rangeUniform(VerticalAnchor.absolute(ore.min_height), VerticalAnchor.absolute(ore.max_height))
                .squared()
                .count(ore.ores_per_chunk);
    }


    private static class OreData {
        public final int max_height;
        public final int min_height;
        public final int vein_size;
        public final int ores_per_chunk;
        public final Block stone_ore_block;
        public final Block deepslate_ore_block;
        public final boolean config_enabled;
        public ConfiguredFeature<?, ?> feature = null;

        /**
         * Construct an OreData with the given parameters
         *
         * @param max_height      the maximum height it can generate at
         * @param min_height      the minimum height it can generate at
         * @param vein_size       the vein size
         * @param ores_per_chunk  number of times it can generate per chunk
         * @param stone_ore_block the block to generate
         * @param config_enabled  whether or not it is enabled in the configuration file
         */
        protected OreData(ForgeConfigSpec.IntValue max_height,
                          ForgeConfigSpec.IntValue min_height,
                          ForgeConfigSpec.IntValue vein_size,
                          ForgeConfigSpec.IntValue ores_per_chunk,
                          RegistryObject<OreBlock> stone_ore_block,
                          RegistryObject<OreBlock> ds_ore_block,
                          ForgeConfigSpec.BooleanValue config_enabled) {
            this.max_height = max_height.get();
            this.min_height = min_height.get();
            this.vein_size = vein_size.get();
            this.ores_per_chunk = ores_per_chunk.get();
            this.stone_ore_block = stone_ore_block.get();
            this.deepslate_ore_block = ds_ore_block.get();
            this.config_enabled = config_enabled.get();
        }
    }
}