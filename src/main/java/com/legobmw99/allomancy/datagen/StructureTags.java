package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;

import java.util.concurrent.CompletableFuture;

public class StructureTags extends StructureTagsProvider {
    public StructureTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, Allomancy.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ExtrasSetup.SEEKABLE).replace(false).add(ExtrasSetup.WELL);
    }
}
