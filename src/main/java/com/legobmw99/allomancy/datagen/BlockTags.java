package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class BlockTags extends BlockTagsProvider {
    public BlockTags(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        addForgeTag("ores/lead", MaterialsSetup.LEAD_ORE.get());
        addForgeTag("ores/tin", MaterialsSetup.TIN_ORE.get());
        addForgeTag("ores/copper", MaterialsSetup.COPPER_ORE.get());
        addForgeTag("ores/zinc", MaterialsSetup.ZINC_ORE.get());
    }

    private void addForgeTag(String name, Block... items) {
        Allomancy.LOGGER.debug("Creating block tag for forge:" + name);
        ResourceLocation loc = new ResourceLocation("forge", name);
        getBuilder(new Tag<Block>(loc)).replace(false).add(items).build(loc);
    }


    @Override
    public String getName() {
        return "Allomancy Block Tags";
    }
}
