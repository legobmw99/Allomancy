package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.concurrent.CompletableFuture;

public class BannerTag extends TagsProvider<BannerPattern> {

    public BannerTag(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> registries, net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        super(pGenerator, Registries.BANNER_PATTERN, registries, Allomancy.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        for (Metal mt : Metal.values()) {
            Allomancy.LOGGER.debug("Creating banner tag for " + mt.getName());

            this.tag(ExtrasSetup.PATTERN_KEYS.get(mt.getIndex())).add(ExtrasSetup.PATTERNS.get(mt.getIndex()).getKey());
        }
    }
}
