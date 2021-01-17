package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTags extends ItemTagsProvider {


    public ItemTags(DataGenerator gen, BlockTagsProvider blockTagProvider, String modid, ExistingFileHelper exFileHelper) {
        super(gen, blockTagProvider, modid, exFileHelper);
    }

    @Override
    protected void registerTags() {

        for (Metal mt : Metal.values()) {
            if (mt == Metal.GOLD || mt == Metal.IRON)
                continue;

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

        addForgeTag("ores/lead", MaterialsSetup.LEAD_ORE_ITEM.get());
        addForgeTag("ores/tin", MaterialsSetup.TIN_ORE_ITEM.get());
        addForgeTag("ores/copper", MaterialsSetup.COPPER_ORE_ITEM.get());
        addForgeTag("ores/zinc", MaterialsSetup.ZINC_ORE_ITEM.get());
        addForgeTag("ores/aluminum", MaterialsSetup.ALUMINUM_ORE_ITEM.get());
        addForgeTag("ores/cadmium", MaterialsSetup.CADMIUM_ORE_ITEM.get());
        addForgeTag("ores/chromium", MaterialsSetup.CHROMIUM_ORE_ITEM.get());
        addForgeTag("ores/silver", MaterialsSetup.SILVER_ORE_ITEM.get());

    }

    private void addForgeTag(String name, Item... items) {
        // see ForgeItemTagsProvider
        Allomancy.LOGGER.debug("Creating item tag for forge:" + name);

        getOrCreateBuilder(net.minecraft.tags.ItemTags.makeWrapperTag("forge:" + name)).replace(false).add(items);
    }


    @Override
    public String getName() {
        return "Allomancy Item Tags";
    }
}
