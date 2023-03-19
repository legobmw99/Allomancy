package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTags extends ItemTagsProvider {


    public ItemTags(PackOutput gen,
                    CompletableFuture<HolderLookup.Provider> lookupProvider,
                    CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider,
                    ExistingFileHelper exFileHelper) {
        super(gen, lookupProvider, blockTagProvider, Allomancy.MODID, exFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        for (Metal mt : Metal.values()) {
            if (mt.isVanilla()) {
                continue;
            }

            var nugget = MaterialsSetup.NUGGETS.get(mt.getIndex()).getKey();
            var ingot = MaterialsSetup.INGOTS.get(mt.getIndex()).getKey();
            var block = MaterialsSetup.STORAGE_BLOCK_ITEMS.get(mt.getIndex()).getKey();

            addForgeTag("nuggets", nugget);
            addForgeTag("nuggets/" + mt.getName(), nugget);
            addForgeTag("ingots", ingot);
            addForgeTag("ingots/" + mt.getName(), ingot);
            addForgeTag("storage_blocks", block);
            addForgeTag("storage_blocks/" + mt.getName(), block);


        }
        addForgeTag("nuggets", MaterialsSetup.NUGGETS.get(MaterialsSetup.LEAD).getKey());
        addForgeTag("nuggets/lead", MaterialsSetup.NUGGETS.get(MaterialsSetup.LEAD).getKey());
        addForgeTag("ingots", MaterialsSetup.INGOTS.get(MaterialsSetup.LEAD).getKey());
        addForgeTag("ingots/lead", MaterialsSetup.INGOTS.get(MaterialsSetup.LEAD).getKey());
        addForgeTag("storage_blocks", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.LEAD).getKey());
        addForgeTag("storage_blocks/lead", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.LEAD).getKey());

        addForgeTag("nuggets", MaterialsSetup.NUGGETS.get(MaterialsSetup.SILVER).getKey());
        addForgeTag("nuggets/silver", MaterialsSetup.NUGGETS.get(MaterialsSetup.SILVER).getKey());
        addForgeTag("ingots", MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).getKey());
        addForgeTag("ingots/silver", MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).getKey());
        addForgeTag("storage_blocks", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.SILVER).getKey());
        addForgeTag("storage_blocks/silver", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.SILVER).getKey());


        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            var ore = MaterialsSetup.ORE_BLOCKS_ITEMS.get(i).getKey();
            var ds_ore = MaterialsSetup.DEEPSLATE_ORE_BLOCKS_ITEMS.get(i).getKey();
            var raw_block = MaterialsSetup.RAW_ORE_BLOCKS_ITEMS.get(i).getKey();
            var raw = MaterialsSetup.RAW_ORE_ITEMS.get(i).getKey();

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

    private void addForgeTag(String name, ResourceKey<Item>... items) {
        // see ForgeItemTagsProvider
        Allomancy.LOGGER.debug("Creating item tag for forge:" + name);

        tag(net.minecraft.tags.ItemTags.create(new ResourceLocation("forge", name))).replace(false).add(items);
    }


    @Override
    public String getName() {
        return "Allomancy Item Tags";
    }
}
