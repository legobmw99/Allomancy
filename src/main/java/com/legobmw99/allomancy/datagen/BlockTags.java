package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.util.Metal;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;


public class BlockTags extends BlockTagsProvider {

    public BlockTags(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void addTags() {
        addForgeTag("ores/aluminum", MaterialsSetup.ALUMINUM_ORE.get());
        addForgeTag("ores/cadmium", MaterialsSetup.CADMIUM_ORE.get());
        addForgeTag("ores/chromium", MaterialsSetup.CHROMIUM_ORE.get());
        addForgeTag("ores/copper", MaterialsSetup.COPPER_ORE.get());
        addForgeTag("ores/lead", MaterialsSetup.LEAD_ORE.get());
        addForgeTag("ores/silver", MaterialsSetup.SILVER_ORE.get());
        addForgeTag("ores/tin", MaterialsSetup.TIN_ORE.get());
        addForgeTag("ores/zinc", MaterialsSetup.ZINC_ORE.get());

        for (Metal mt : Metal.values()) {
            if (mt != Metal.GOLD && mt != Metal.IRON) {
                addForgeTag("storage_blocks/" + mt.getName(), MaterialsSetup.STORAGE_BLOCKS.get(mt.getIndex()).get());
            }
        }

        addForgeTag("storage_blocks/lead", MaterialsSetup.STORAGE_BLOCKS.get(MaterialsSetup.LEAD).get());

        addForgeTag("storage_blocks/silver", MaterialsSetup.STORAGE_BLOCKS.get(MaterialsSetup.SILVER).get());


    }

    private void addForgeTag(String name, Block... items) {
        Allomancy.LOGGER.debug("Creating block tag for forge:" + name);
        tag(net.minecraft.tags.BlockTags.bind("forge:" + name)).replace(false).add(items);
    }


    @Override
    public String getName() {
        return "Allomancy Block Tags";
    }
}
