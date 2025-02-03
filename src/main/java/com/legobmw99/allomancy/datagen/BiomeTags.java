package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class BiomeTags extends BiomeTagsProvider {
    public BiomeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, Allomancy.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(ExtrasSetup.SPAWNS_WELLS).addTag(Tags.Biomes.IS_MOUNTAIN_PEAK);
    }
}
