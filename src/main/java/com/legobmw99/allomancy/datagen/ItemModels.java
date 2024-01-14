package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;


public class ItemModels extends ItemModelProvider {
    public ItemModels(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, Allomancy.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        parentedBlock(ExtrasSetup.IRON_BUTTON.get(), "block/iron_button_inventory");
        parentedBlock(ExtrasSetup.INVERTED_IRON_BUTTON.get(), "block/iron_button_inventory");
        itemGenerated(ExtrasSetup.IRON_LEVER_ITEM.get(), "block/iron_lever");

        itemGenerated(CombatSetup.MISTCLOAK.get());
        itemGenerated(CombatSetup.ALUMINUM_HELMET.get());
        itemGenerated(CombatSetup.COIN_BAG.get());
        itemHandheld(CombatSetup.OBSIDIAN_DAGGER.get());
        largeItemHandheld(CombatSetup.KOLOSS_BLADE.get());

        itemGenerated(ConsumeSetup.ALLOMANTIC_GRINDER.get());
        itemGenerated(ConsumeSetup.LERASIUM_NUGGET.get());

        for (var rblock : MaterialsSetup.ORE_BLOCKS) {
            parentedBlock(rblock.get());
        }
        for (var rblock : MaterialsSetup.DEEPSLATE_ORE_BLOCKS) {
            parentedBlock(rblock.get());
        }
        for (var rblock : MaterialsSetup.RAW_ORE_BLOCKS) {
            parentedBlock(rblock.get());
        }

        for (var ritem : MaterialsSetup.RAW_ORE_ITEMS) {
            itemGenerated(ritem.get());
        }

        for (int i = 0; i < MaterialsSetup.METAL_ITEM_LEN; i++) {

            Item flake = MaterialsSetup.FLAKES.get(i).get();
            itemGenerated(flake);
            if (i <= Metal.BENDALLOY.getIndex()) {
                Item pattern_item = ExtrasSetup.PATTERN_ITEMS.get(i).get();
                itemGenerated(pattern_item);

                if (Metal.getMetal(i).isVanilla()) {
                    continue;
                }
            }


            Item nugget = MaterialsSetup.NUGGETS.get(i).get();
            itemGenerated(nugget);

            Item ingot = MaterialsSetup.INGOTS.get(i).get();
            itemGenerated(ingot);

            Block block = MaterialsSetup.STORAGE_BLOCKS.get(i).get();
            parentedBlock(block);

        }

        Allomancy.LOGGER.debug("Creating Item Model for allomancy:vial (filled)");
        var mf = getBuilder("vial_filled").parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", modLoc("item/full_vial"));
        Allomancy.LOGGER.debug("Creating Item Model for allomancy:vial");
        getBuilder("vial")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/vial"))
                .override()
                .predicate(mcLoc("custom_model_data"), 1)
                .model(mf)
                .end();

    }

    public void parentedBlock(Block block) {
        parentedBlock(block, "block/" + BuiltInRegistries.BLOCK.getKey(block).getPath());
    }

    public void parentedBlock(Block block, String model) {
        Allomancy.LOGGER.debug("Creating Item Model for " + BuiltInRegistries.BLOCK.getKey(block));
        getBuilder(BuiltInRegistries.BLOCK.getKey(block).getPath()).parent(new ModelFile.UncheckedModelFile(modLoc(model)));
    }

    public void itemGenerated(Item item) {
        itemGenerated(item, "item/" + BuiltInRegistries.ITEM.getKey(item).getPath());
    }

    public void itemGenerated(Item item, String model) {
        Allomancy.LOGGER.debug("Creating Item Model for " + BuiltInRegistries.ITEM.getKey(item));
        getBuilder(BuiltInRegistries.ITEM.getKey(item).getPath()).parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", modLoc(model));
    }

    public void itemHandheld(Item item) {
        Allomancy.LOGGER.debug("Creating Item Model for " + BuiltInRegistries.ITEM.getKey(item));
        getBuilder(BuiltInRegistries.ITEM.getKey(item).getPath())
                .parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", modLoc("item/" + BuiltInRegistries.ITEM.getKey(item).getPath()));
    }

    public void largeItemHandheld(Item item) {
        Allomancy.LOGGER.debug("Creating Large Item Model for " + BuiltInRegistries.ITEM.getKey(item));
        getBuilder(BuiltInRegistries.ITEM.getKey(item).getPath())
                .parent(getExistingFile(modLoc("item/handheld_large")))
                .texture("layer0", modLoc("item/" + BuiltInRegistries.ITEM.getKey(item).getPath()));
    }

    @Override
    public String getName() {
        return "Allomancy Item Models";
    }
}
