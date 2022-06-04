package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;


public class BlockTags extends BlockTagsProvider {

    public BlockTags(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void addTags() {

        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            var block = MaterialsSetup.ORE_BLOCKS.get(i).get();
            var ds = MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i).get();
            var raw = MaterialsSetup.RAW_ORE_BLOCKS.get(i).get();

            addForgeTag("ores/" + MaterialsSetup.ORE_METALS[i], block, ds);
            addForgeTag("ores", block, ds);
            addForgeTag("ores_in_ground/stone", block);
            addForgeTag("ores_in_ground/deepslate", ds);

            addForgeTag("storage_blocks", raw);
            addForgeTag("storage_blocks/raw_" + MaterialsSetup.ORE_METALS[i], raw);

            makePickaxeMineable(block, ds, raw);
        }

        for (Metal mt : Metal.values()) {
            if (mt.isVanilla()) {
                continue;
            }
            var block = MaterialsSetup.STORAGE_BLOCKS.get(mt.getIndex()).get();
            addForgeTag("storage_blocks/" + mt.getName(), block);
            addForgeTag("storage_blocks", block);
            makePickaxeMineable(block);
            if (mt != Metal.ALUMINUM) {
                addTag("beacon_base_blocks", block);
            }

        }

        var lead = MaterialsSetup.STORAGE_BLOCKS.get(MaterialsSetup.LEAD).get();
        addForgeTag("storage_blocks/lead", lead);
        var silver = MaterialsSetup.STORAGE_BLOCKS.get(MaterialsSetup.SILVER).get();
        addForgeTag("storage_blocks/silver", silver);
        addTag("beacon_base_blocks", silver);

        makePickaxeMineable(lead, silver);
        addForgeTag("storage_blocks", lead, silver);

    }

    private void addForgeTag(String name, Block... items) {
        Allomancy.LOGGER.debug("Creating block tag for forge:" + name);
        tag(net.minecraft.tags.BlockTags.create(new ResourceLocation("forge", name))).replace(false).add(items);
    }

    private void makePickaxeMineable(Block... items) {
        addTag("mineable/pickaxe", items);
        addTag("needs_stone_tool", items);

    }

    private void addTag(String name, Block... items) {
        Allomancy.LOGGER.debug("Creating block tag for minecraft:" + name);
        tag(net.minecraft.tags.BlockTags.create(new ResourceLocation("minecraft", name))).replace(false).add(items);
    }

    @Override
    public String getName() {
        return "Allomancy Block Tags";
    }
}
