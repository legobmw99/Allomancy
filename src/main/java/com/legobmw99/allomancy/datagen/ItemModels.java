package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        parentedBlock(ExtrasSetup.IRON_BUTTON.get(), "block/iron_button_inventory");
        itemGenerated(ExtrasSetup.IRON_LEVER_ITEM.get(), "block/iron_lever");

        itemGenerated(CombatSetup.MISTCLOAK.get(), "item/mistcloak");
        itemGenerated(CombatSetup.COIN_BAG.get(), "item/coin_bag");
        itemHandheld(CombatSetup.OBSIDIAN_DAGGER.get(), "item/obsidian_dagger");
        largeItemHandheld(CombatSetup.KOLOSS_BLADE.get(), "item/koloss_blade");

        itemGenerated(ConsumeSetup.ALLOMANTIC_GRINDER.get(), "item/allomantic_grinder");
        itemGenerated(ConsumeSetup.LERASIUM_NUGGET.get(), "item/lerasium_nugget");

        for (var rblock : MaterialsSetup.ORE_BLOCKS) {
            Block block = rblock.get();
            String path = block.getRegistryName().getPath();
            parentedBlock(block, "block/" + path);
        }
        for (var rblock : MaterialsSetup.DEEPSLATE_ORE_BLOCKS) {
            Block block = rblock.get();
            String path = block.getRegistryName().getPath();
            parentedBlock(block, "block/" + path);
        }
        for (var rblock : MaterialsSetup.RAW_ORE_BLOCKS) {
            Block block = rblock.get();
            String path = block.getRegistryName().getPath();
            parentedBlock(block, "block/" + path);
        }

        for (int i = 0; i < MaterialsSetup.METAL_ITEM_LEN; i++) {

            Item flake = MaterialsSetup.FLAKES.get(i).get();
            itemGenerated(flake, "item/" + flake.getRegistryName().getPath());

            if (i <= Metal.BENDALLOY.getIndex()) {
                Item pattern_item = ExtrasSetup.PATTERN_ITEMS.get(i).get();
                itemGenerated(pattern_item, "item/" + pattern_item.getRegistryName().getPath());

                if (Metal.getMetal(i).isVanilla()) {
                    continue;
                }
            }


            Item nugget = MaterialsSetup.NUGGETS.get(i).get();
            itemGenerated(nugget, "item/" + nugget.getRegistryName().getPath());

            Item ingot = MaterialsSetup.INGOTS.get(i).get();
            itemGenerated(ingot, "item/" + ingot.getRegistryName().getPath());

            Block block = MaterialsSetup.STORAGE_BLOCKS.get(i).get();
            parentedBlock(block, "block/" + block.getRegistryName().getPath());

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

    public void parentedBlock(Block block, String model) {
        Allomancy.LOGGER.debug("Creating Item Model for " + block.getRegistryName());
        getBuilder(block.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(modLoc(model)));
    }

    public void itemGenerated(Item item, String texture) {
        Allomancy.LOGGER.debug("Creating Item Model for " + item.getRegistryName());
        getBuilder(item.getRegistryName().getPath()).parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", modLoc(texture));
    }

    public void itemHandheld(Item item, String texture) {
        Allomancy.LOGGER.debug("Creating Item Model for " + item.getRegistryName());
        getBuilder(item.getRegistryName().getPath()).parent(getExistingFile(mcLoc("item/handheld"))).texture("layer0", modLoc(texture));
    }

    public void largeItemHandheld(Item item, String texture) {
        Allomancy.LOGGER.debug("Creating Large Item Model for " + item.getRegistryName());
        getBuilder(item.getRegistryName().getPath()).parent(getExistingFile(modLoc("item/handheld_large"))).texture("layer0", modLoc(texture));
    }

    @Override
    public String getName() {
        return "Allomancy Item Models";
    }
}
