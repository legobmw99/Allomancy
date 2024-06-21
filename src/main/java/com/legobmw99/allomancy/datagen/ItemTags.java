package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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

            addCommonTag("nuggets", nugget);
            addCommonTag("nuggets/" + mt.getName(), nugget);
            addCommonTag("ingots", ingot);
            addCommonTag("ingots/" + mt.getName(), ingot);
            addCommonTag("storage_blocks", block);
            addCommonTag("storage_blocks/" + mt.getName(), block);


        }
        addCommonTag("nuggets", MaterialsSetup.NUGGETS.get(MaterialsSetup.LEAD).getKey());
        addCommonTag("nuggets/lead", MaterialsSetup.NUGGETS.get(MaterialsSetup.LEAD).getKey());
        addCommonTag("ingots", MaterialsSetup.INGOTS.get(MaterialsSetup.LEAD).getKey());
        addCommonTag("ingots/lead", MaterialsSetup.INGOTS.get(MaterialsSetup.LEAD).getKey());
        addCommonTag("storage_blocks", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.LEAD).getKey());
        addCommonTag("storage_blocks/lead", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.LEAD).getKey());

        addCommonTag("nuggets", MaterialsSetup.NUGGETS.get(MaterialsSetup.SILVER).getKey());
        addCommonTag("nuggets/silver", MaterialsSetup.NUGGETS.get(MaterialsSetup.SILVER).getKey());
        addCommonTag("ingots", MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).getKey());
        addCommonTag("ingots/silver", MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).getKey());
        addCommonTag("storage_blocks", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.SILVER).getKey());
        addCommonTag("storage_blocks/silver", MaterialsSetup.STORAGE_BLOCK_ITEMS.get(MaterialsSetup.SILVER).getKey());


        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            var ore = MaterialsSetup.ORE_BLOCKS_ITEMS.get(i).getKey();
            var ds_ore = MaterialsSetup.DEEPSLATE_ORE_BLOCKS_ITEMS.get(i).getKey();
            var raw_block = MaterialsSetup.RAW_ORE_BLOCKS_ITEMS.get(i).getKey();
            var raw = MaterialsSetup.RAW_ORE_ITEMS.get(i).getKey();

            addCommonTag("ores/" + MaterialsSetup.ORE_METALS[i], ore, ds_ore);
            addCommonTag("ores", ore, ds_ore);
            addCommonTag("ores_in_ground/stone", ore);
            addCommonTag("ores_in_ground/deepslate", ds_ore);
            addCommonTag("storage_blocks", raw_block);
            addCommonTag("storage_blocks/raw_" + MaterialsSetup.ORE_METALS[i], raw_block);
            addCommonTag("raw_materials", raw);
            addCommonTag("raw_materials/" + MaterialsSetup.ORE_METALS[i], raw);
        }

        tag(net.minecraft.tags.ItemTags.SWORDS)
                .replace(false)
                .add(CombatSetup.OBSIDIAN_DAGGER.get())
                .add(CombatSetup.KOLOSS_BLADE.get());
    }

    @SafeVarargs
    private void addCommonTag(String name, ResourceKey<Item>... items) {
        Allomancy.LOGGER.debug("Creating item tag for c:" + name);
        tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name)))
                .replace(false)
                .add(items);
    }


    @Override
    public String getName() {
        return "Allomancy Item Tags";
    }
}
