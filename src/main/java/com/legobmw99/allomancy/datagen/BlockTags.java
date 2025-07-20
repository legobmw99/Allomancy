package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;


public class BlockTags extends BlockTagsProvider {

    public BlockTags(PackOutput gen, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper exFileHelper) {
        super(gen, lookupProvider, Allomancy.MODID, exFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        for (int i = 0; i < WorldSetup.ORE_METALS.length; i++) {
            var block = WorldSetup.ORE_BLOCKS.get(i).getKey();
            var ds = WorldSetup.DEEPSLATE_ORE_BLOCKS.get(i).getKey();
            var raw = WorldSetup.RAW_ORE_BLOCKS.get(i).getKey();

            addForgeTag("ores/" + WorldSetup.ORE_METALS[i], block, ds);
            addForgeTag("ores", block, ds);
            addForgeTag("ores_in_ground/stone", block);
            addForgeTag("ores_in_ground/deepslate", ds);

            addForgeTag("storage_blocks", raw);
            addForgeTag("storage_blocks/raw_" + WorldSetup.ORE_METALS[i], raw);

            makePickaxeMineable(block, ds, raw);
        }

        for (Metal mt : Metal.values()) {
            if (mt.isVanilla()) {
                continue;
            }
            var block = WorldSetup.STORAGE_BLOCKS.get(mt.getIndex()).getKey();
            addForgeTag("storage_blocks/" + mt.getName(), block);
            addForgeTag("storage_blocks", block);
            makePickaxeMineable(block);
            if (mt != Metal.ALUMINUM) {
                addBeacon(block);
            }

        }

        var lead = WorldSetup.STORAGE_BLOCKS.get(WorldSetup.LEAD).getKey();
        addForgeTag("storage_blocks/lead", lead);
        var silver = WorldSetup.STORAGE_BLOCKS.get(WorldSetup.SILVER).getKey();
        addForgeTag("storage_blocks/silver", silver);
        addBeacon(silver);

        makePickaxeMineable(lead, silver);
        addForgeTag("storage_blocks", lead, silver);

    }

    private void addForgeTag(String name, ResourceKey<Block>... items) {
        Allomancy.LOGGER.debug("Creating block tag for forge:" + name);
        tag(net.minecraft.tags.BlockTags.create(new ResourceLocation("forge", name))).replace(false).add(items);
    }

    private void makePickaxeMineable(ResourceKey<Block>... items) {
        this.tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).replace(false).add(items);
        this.tag(net.minecraft.tags.BlockTags.NEEDS_STONE_TOOL).replace(false).add(items);

    }

    private void addBeacon(ResourceKey<Block>... items) {
        this.tag(net.minecraft.tags.BlockTags.BEACON_BASE_BLOCKS).replace(false).add(items);
    }

    @Override
    public String getName() {
        return "Allomancy Block Tags";
    }

}
