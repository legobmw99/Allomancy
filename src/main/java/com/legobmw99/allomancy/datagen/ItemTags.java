package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTags extends ItemTagsProvider {


    public ItemTags(DataGenerator gen, BlockTagsProvider blockTagProvider, String modid, ExistingFileHelper exFileHelper) {
        super(gen, blockTagProvider, modid, exFileHelper);
    }

    @Override
    protected void addTags() {

        for (Metal mt : Metal.values()) {
            if (mt.isVanilla()) {
                continue;
            }

            addForgeTag("nuggets/" + mt.getName(), MaterialsSetup.NUGGETS.get(mt.getIndex()).get());
            addForgeTag("ingots/" + mt.getName(), MaterialsSetup.INGOTS.get(mt.getIndex()).get());
            addForgeTag("blocks/" + mt.getName(), MaterialsSetup.STORAGE_BLOCK_ITEMS.get(mt.getIndex()).get());
            addForgeTag("storage_blocks/" + mt.getName(), MaterialsSetup.STORAGE_BLOCK_ITEMS.get(mt.getIndex()).get());

        }
        addForgeTag("nuggets/lead", MaterialsSetup.NUGGETS.get(MaterialsSetup.LEAD).get());
        addForgeTag("ingots/lead", MaterialsSetup.INGOTS.get(MaterialsSetup.LEAD).get());
        addForgeTag("blocks/lead", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.LEAD).get());
        addForgeTag("storage_blocks/lead", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.LEAD).get());

        addForgeTag("nuggets/silver", MaterialsSetup.NUGGETS.get(MaterialsSetup.SILVER).get());
        addForgeTag("ingots/silver", MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).get());
        addForgeTag("blocks/silver", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.SILVER).get());
        addForgeTag("storage_blocks/silver", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.SILVER).get());

        for (var rblock : MaterialsSetup.ORE_BLOCKS_ITEMS) {
            var block = rblock.get();
            String path = block.getRegistryName().getPath();
            addForgeTag("ores/" + path, block);
        }

        for (var rblock : MaterialsSetup.DEEPSLATE_ORE_BLOCKS_ITEMS) {
            var block = rblock.get();
            String path = block.getRegistryName().getPath();
            addForgeTag("ores/" + path, block);
        }

    }

    private void addForgeTag(String name, Item... items) {
        // see ForgeItemTagsProvider
        Allomancy.LOGGER.debug("Creating item tag for forge:" + name);

        tag(net.minecraft.tags.ItemTags.create(new ResourceLocation("forge", name))).replace(false).add(items);
    }


    @Override
    public String getName() {
        return "Allomancy Item Tags";
    }
}
