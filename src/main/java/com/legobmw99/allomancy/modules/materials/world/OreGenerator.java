package com.legobmw99.allomancy.modules.materials.world;

import com.google.common.collect.ImmutableList;
import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsConfig;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.ArrayList;
import java.util.List;


public class OreGenerator {

    private static final ArrayList<Holder<PlacedFeature>> ores = new ArrayList<>();


    public static void registerFeatures() {
        for (MaterialsConfig.OreConfig ore_config : MaterialsConfig.ores) {
            Allomancy.LOGGER.info("Registering feature generation for " + ore_config.name + " ore!");
            var feature = featureFromConfig(ore_config);
            ores.add(feature);
        }
    }

    public static void registerGeneration(BiomeLoadingEvent event) {
        if (event.getCategory() == Biome.BiomeCategory.NETHER || event.getCategory() == Biome.BiomeCategory.THEEND) {
            return;
        }

        var generation = event.getGeneration();
        for (MaterialsConfig.OreConfig ore_config : MaterialsConfig.ores) {
            if (ore_config.generate.get()) {
                generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ores.get(ore_config.index));
            }
        }
    }


    private static Holder<PlacedFeature> featureFromConfig(MaterialsConfig.OreConfig cfg) {
        var stone_ore = MaterialsSetup.ORE_BLOCKS.get(cfg.index).get();
        var deepslate_ore = MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(cfg.index).get();
        var targetList = ImmutableList.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, stone_ore.defaultBlockState()),
                                          OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, deepslate_ore.defaultBlockState()));
        var oreconfig = FeatureUtils.register(cfg.name + "_ore_feature", Feature.ORE, new OreConfiguration(targetList, cfg.vein_size.get()));
        return PlacementUtils.register(cfg.name + "_ore_feature", oreconfig, commonOrePlacement(cfg.per_chunk.get(), cfg.min_y.get(), cfg.max_y.get(), cfg.placement_type.get()));
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, int min, int max, MaterialsConfig.PlacementType type) {
        return switch (type) {
            case UNIFORM -> orePlacement(CountPlacement.of(count), HeightRangePlacement.uniform(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max)));
            case TRIANGLE -> orePlacement(CountPlacement.of(count), HeightRangePlacement.triangle(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max)));
        };
    }

}