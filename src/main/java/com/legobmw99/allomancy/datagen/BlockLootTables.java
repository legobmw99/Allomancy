package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

class BlockLootTables extends BlockLootSubProvider {
    BlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            Allomancy.LOGGER.debug("Creating loot tables for ore: {}", MaterialsSetup.ORE_METALS[i].name());
            var ore = MaterialsSetup.ORE_BLOCKS.get(i).get();
            var ds = MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i).get();
            var raw = MaterialsSetup.RAW_ORE_ITEMS.get(i).get();
            var rawb = MaterialsSetup.RAW_ORE_BLOCKS.get(i).get();

            this.add(ore, block -> this.createOreDrop(block, raw));
            this.add(ds, block -> this.createOreDrop(block, raw));
            this.dropSelf(rawb);
        }

        this.dropSelf(ExtrasSetup.IRON_BUTTON.get());
        this.dropSelf(ExtrasSetup.INVERTED_IRON_BUTTON.get());
        this.dropSelf(ExtrasSetup.IRON_LEVER.get());


        for (Supplier<Block> rblock : MaterialsSetup.STORAGE_BLOCKS) {
            if (rblock != null) {
                this.dropSelf(rblock.get());
            }
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Stream.concat(MaterialsSetup.BLOCKS.getEntries().stream().map(DeferredHolder::get),
                             ExtrasSetup.BLOCKS.getEntries().stream().map(DeferredHolder::get))::iterator;
    }
}
