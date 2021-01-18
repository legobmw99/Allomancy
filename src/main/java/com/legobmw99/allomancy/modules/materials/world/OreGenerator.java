package com.legobmw99.allomancy.modules.materials.world;

import com.legobmw99.allomancy.modules.materials.MaterialsConfig;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.block.Block;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.ArrayList;

public class OreGenerator {

    private static final ArrayList<OreData> ores = new ArrayList<>();

    static {
        ores.add(new OreData(MaterialsConfig.aluminum_max_y.get(), MaterialsConfig.aluminum_min_y.get(), 9, MaterialsConfig.aluminum_density.get(),
                             MaterialsSetup.ALUMINUM_ORE.get(), MaterialsConfig.generate_aluminum.get()));
        ores.add(new OreData(MaterialsConfig.cadmium_max_y.get(), MaterialsConfig.cadmium_min_y.get(), 7, MaterialsConfig.cadmium_density.get(), MaterialsSetup.CADMIUM_ORE.get(),
                             MaterialsConfig.generate_cadmium.get()));
        ores.add(
                new OreData(MaterialsConfig.chromium_max_y.get(), MaterialsConfig.chromium_min_y.get(), 6, MaterialsConfig.cadmium_density.get(), MaterialsSetup.CHROMIUM_ORE.get(),
                            MaterialsConfig.generate_chromium.get()));
        ores.add(new OreData(MaterialsConfig.copper_max_y.get(), MaterialsConfig.copper_min_y.get(), 9, MaterialsConfig.copper_density.get(), MaterialsSetup.COPPER_ORE.get(),
                             MaterialsConfig.generate_copper.get()));
        ores.add(new OreData(MaterialsConfig.lead_max_y.get(), MaterialsConfig.lead_min_y.get(), 9, MaterialsConfig.lead_density.get(), MaterialsSetup.LEAD_ORE.get(),
                             MaterialsConfig.generate_lead.get()));
        ores.add(new OreData(MaterialsConfig.silver_max_y.get(), MaterialsConfig.silver_min_y.get(), 7, MaterialsConfig.silver_density.get(), MaterialsSetup.SILVER_ORE.get(),
                             MaterialsConfig.generate_silver.get()));
        ores.add(new OreData(MaterialsConfig.tin_max_y.get(), MaterialsConfig.tin_min_y.get(), 11, MaterialsConfig.tin_density.get(), MaterialsSetup.TIN_ORE.get(),
                             MaterialsConfig.generate_tin.get()));
        ores.add(new OreData(MaterialsConfig.zinc_max_y.get(), MaterialsConfig.zinc_min_y.get(), 8, MaterialsConfig.zinc_density.get(), MaterialsSetup.ZINC_ORE.get(),
                             MaterialsConfig.generate_zinc.get()));
    }

    public static void registerGeneration(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder generation = event.getGeneration();
        for (OreData ore : ores) {
            if (ore.config_enabled) {
                // TODO - investigate min/max
                generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(
                        new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, ore.ore_block.getDefaultState(), ore.vein_size))
                                                                                               .withPlacement(Placement.RANGE.configure(
                                                                                                       new TopSolidRangeConfig(0, ore.min_height, ore.max_height)))
                                                                                               .square()
                                                                                               .func_242731_b(ore.ores_per_chunk));
            }
        }
    }

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
        protected OreData(int max_height, int min_height, int vein_size, int ores_per_chunk, Block ore_block, boolean config_enabled) {
            this.max_height = max_height;
            this.min_height = min_height;
            this.vein_size = vein_size;
            this.ores_per_chunk = ores_per_chunk;
            this.ore_block = ore_block;
            this.config_enabled = config_enabled;
        }
    }
}