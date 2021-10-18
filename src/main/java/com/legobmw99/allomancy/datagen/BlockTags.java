package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
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

            String path = block.getRegistryName().getPath();
            addForgeTag("ores/" + path, block, ds);
            makePickaxeMineable(block, ds, raw);
        }

        for (Metal mt : Metal.values()) {
            if (!mt.isVanilla()) {
                var block = MaterialsSetup.STORAGE_BLOCKS.get(mt.getIndex()).get();
                addForgeTag("storage_blocks/" + mt.getName(), block);
                makePickaxeMineable(block);
                if (mt != Metal.ALUMINUM){
                    addTag("beacon_base_blocks", block);
                }
            }
        }

        var lead = MaterialsSetup.STORAGE_BLOCKS.get(MaterialsSetup.LEAD).get();
        addForgeTag("storage_blocks/lead", lead);
        var silver = MaterialsSetup.STORAGE_BLOCKS.get(MaterialsSetup.SILVER).get();
        addForgeTag("storage_blocks/silver", silver);
        addTag("beacon_base_blocks", silver);

        makePickaxeMineable(lead, silver);



    }

    private void addForgeTag(String name, Block... items) {
        Allomancy.LOGGER.debug("Creating block tag for forge:" + name);
        tag(net.minecraft.tags.BlockTags.bind("forge:" + name)).replace(false).add(items);
    }

    private void makePickaxeMineable(Block... items) {
        addTag("mineable/pickaxe", items);
        addTag("needs_stone_tool", items);

    }

    private void addTag(String name, Block... items) {
        Allomancy.LOGGER.debug("Creating block tag for minecraft:" + name);
        tag(net.minecraft.tags.BlockTags.bind("minecraft:" + name)).replace(false).add(items);
    }

    @Override
    public String getName() {
        return "Allomancy Block Tags";
    }
}
