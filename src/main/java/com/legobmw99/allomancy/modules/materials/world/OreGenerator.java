package com.legobmw99.allomancy.modules.materials.world;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;

import static com.legobmw99.allomancy.modules.materials.MaterialsConfig.*;

public class OreGenerator {

    private static final ArrayList<OreData> ores = new ArrayList<>();

    static {
        ores.add(new OreData(aluminum_max_y, aluminum_min_y, aluminum_size, aluminum_density, MaterialsSetup.ALUMINUM_ORE, generate_aluminum));
        ores.add(new OreData(cadmium_max_y, cadmium_min_y, cadmium_size, cadmium_density, MaterialsSetup.CADMIUM_ORE, generate_cadmium));
        ores.add(new OreData(chromium_max_y, chromium_min_y, chromium_size, cadmium_density, MaterialsSetup.CHROMIUM_ORE, generate_chromium));
        ores.add(new OreData(copper_max_y, copper_min_y, copper_size, copper_density, MaterialsSetup.COPPER_ORE, generate_copper));
        ores.add(new OreData(lead_max_y, lead_min_y, lead_size, lead_density, MaterialsSetup.LEAD_ORE, generate_lead));
        ores.add(new OreData(silver_max_y, silver_min_y, silver_size, silver_density, MaterialsSetup.SILVER_ORE, generate_silver));
        ores.add(new OreData(tin_max_y, tin_min_y, tin_size, tin_density, MaterialsSetup.TIN_ORE, generate_tin));
        ores.add(new OreData(zinc_max_y, zinc_min_y, zinc_size, zinc_density, MaterialsSetup.ZINC_ORE, generate_zinc));
    }

    public static void registerFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;
        for (OreData ore : ores) {
            ResourceLocation block = ore.ore_block.getRegistryName();
            Allomancy.LOGGER.info("Registering configured feature generation for block " + block.toString());
            ore.feature = featureFromData(ore);
            Registry.register(registry, block, ore.feature);
        }
    }

    public static void registerGeneration(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder generation = event.getGeneration();
        for (OreData ore : ores) {
            if (ore.config_enabled) {
                generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore.feature);
            }
        }
    }

    private static ConfiguredFeature<?, ?> featureFromData(OreData ore) {
        return Feature.ORE
                .withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, ore.ore_block.getDefaultState(), ore.vein_size))
                .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(ore.min_height, ore.min_height, ore.max_height)))
                .square()
                .func_242731_b(ore.ores_per_chunk);
    }


    private static class OreData {
        public final int max_height;
        public final int min_height;
        public final int vein_size;
        public final int ores_per_chunk;
        public final Block ore_block;
        public final boolean config_enabled;
        public ConfiguredFeature<?, ?> feature = null;

        /**
         * Construct an OreData with the given parameters
         *
         * @param max_height     the maximum height it can generate at
         * @param min_height     the minimum height it can generate at
         * @param vein_size      the vein size
         * @param ores_per_chunk number of times it can generate per chunk
         * @param ore_block      the block to generate
         * @param config_enabled whether or not it is enabled in the configuration file
         */
        protected OreData(ForgeConfigSpec.IntValue max_height,
                          ForgeConfigSpec.IntValue min_height,
                          ForgeConfigSpec.IntValue vein_size,
                          ForgeConfigSpec.IntValue ores_per_chunk,
                          RegistryObject<Block> ore_block,
                          ForgeConfigSpec.BooleanValue config_enabled) {
            this.max_height = max_height.get();
            this.min_height = min_height.get();
            this.vein_size = vein_size.get();
            this.ores_per_chunk = ores_per_chunk.get();
            this.ore_block = ore_block.get();
            this.config_enabled = config_enabled.get();
        }
    }
}