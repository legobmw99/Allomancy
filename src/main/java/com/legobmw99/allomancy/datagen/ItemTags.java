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

            var nugget = MaterialsSetup.NUGGETS.get(mt.getIndex()).get();
            var ingot = MaterialsSetup.INGOTS.get(mt.getIndex()).get();
            var block = MaterialsSetup.STORAGE_BLOCK_ITEMS.get(mt.getIndex()).get();

            addForgeTag("nuggets", nugget);
            addForgeTag("nuggets/" + mt.getName(), nugget);
            addForgeTag("ingots", ingot);
            addForgeTag("ingots/" + mt.getName(), ingot);
            addForgeTag("storage_blocks", block);
            addForgeTag("storage_blocks/" + mt.getName(), block);


        }
        addForgeTag("nuggets", MaterialsSetup.NUGGETS.get(MaterialsSetup.LEAD).get());
        addForgeTag("nuggets/lead", MaterialsSetup.NUGGETS.get(MaterialsSetup.LEAD).get());
        addForgeTag("ingots", MaterialsSetup.INGOTS.get(MaterialsSetup.LEAD).get());
        addForgeTag("ingots/lead", MaterialsSetup.INGOTS.get(MaterialsSetup.LEAD).get());
        addForgeTag("storage_blocks", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.LEAD).get());
        addForgeTag("storage_blocks/lead", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.LEAD).get());

        addForgeTag("nuggets", MaterialsSetup.NUGGETS.get(MaterialsSetup.SILVER).get());
        addForgeTag("nuggets/silver", MaterialsSetup.NUGGETS.get(MaterialsSetup.SILVER).get());
        addForgeTag("ingots", MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).get());
        addForgeTag("ingots/silver", MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).get());
        addForgeTag("storage_blocks", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.SILVER).get());
        addForgeTag("storage_blocks/silver", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.SILVER).get());


        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            var ore = MaterialsSetup.ORE_BLOCKS_ITEMS.get(i).get();
            var ds_ore = MaterialsSetup.DEEPSLATE_ORE_BLOCKS_ITEMS.get(i).get();
            var raw_block = MaterialsSetup.RAW_ORE_BLOCKS_ITEMS.get(i).get();
            var raw = MaterialsSetup.RAW_ORE_ITEMS.get(i).get();

            addForgeTag("ores/" + MaterialsSetup.ORE_METALS[i], ore, ds_ore);
            addForgeTag("ores", ore, ds_ore);
            addForgeTag("ores_in_ground/stone", ore);
            addForgeTag("ores_in_ground/deepslate", ds_ore);
            addForgeTag("storage_blocks", raw_block);
            addForgeTag("storage_blocks/raw_" + MaterialsSetup.ORE_METALS[i], raw_block);
            addForgeTag("raw_materials", raw);
            addForgeTag("raw_materials/" + MaterialsSetup.ORE_METALS[i], raw);
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
