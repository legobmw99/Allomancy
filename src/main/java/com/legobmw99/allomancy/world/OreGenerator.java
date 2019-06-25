package com.legobmw99.allomancy.world;

import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.ArrayList;

public class OreGenerator {

    private static class OreData {
        public int max_height;
        public int min_height;
        public int vein_size;
        public int ores_per_chunk;
        public Block ore_block;
        public boolean config_enabled;

        /**
         * Construct an OreData with the given parameters
         *
         * @param max_height     the maximum height it can generate at
         * @param min_height     the minumum height it can generate at
         * @param vein_size      the vien size
         * @param ores_per_chunk number of times it can generate per chunk
         * @param ore_block      the block to generate
         * @param config_enabled whether or not it is enabled in the configuration file
         */
        protected OreData(int max_height, int min_height, int vein_size, int ores_per_chunk, Block ore_block,
                          boolean config_enabled) {
            this.max_height = max_height;
            this.min_height = min_height;
            this.vein_size = vein_size;
            this.ores_per_chunk = ores_per_chunk;
            this.ore_block = ore_block;
            this.config_enabled = config_enabled;
        }
    }

    private static ArrayList<OreData> ores = new ArrayList<>();

    static {
        ores.add(new OreData(AllomancyConfig.copper_max_y, AllomancyConfig.copper_min_y, 9, AllomancyConfig.copper_density,
                Registry.copper_ore, AllomancyConfig.generate_copper));
        ores.add(new OreData(AllomancyConfig.tin_max_y, AllomancyConfig.tin_min_y, 9, AllomancyConfig.tin_density,
                Registry.tin_ore, AllomancyConfig.generate_tin));
        ores.add(new OreData(AllomancyConfig.lead_max_y, AllomancyConfig.lead_min_y, 9, AllomancyConfig.lead_density,
                Registry.lead_ore, AllomancyConfig.generate_lead));
        ores.add(new OreData(AllomancyConfig.zinc_max_y, AllomancyConfig.zinc_min_y, 9, AllomancyConfig.zinc_density,
                Registry.zinc_ore, AllomancyConfig.generate_zinc));
    }


    public static void generationSetup() {
        for (Biome biome : Biome.BIOMES) {
            if (biome.getRegistryName().toString().matches(".*end.*|.*nether.*")) {
                continue;
            }
            for (OreData ore : ores) {
                if (ore.config_enabled) {
                    biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                            Biome.createDecoratedFeature(Feature.ORE,
                                    new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, ore.ore_block.getDefaultState(), ore.vein_size),
                                    Placement.COUNT_RANGE,
                                    new CountRangeConfig(ore.ores_per_chunk, ore.max_height, 1, ore.max_height)));
                }
            }
        }
    }
}