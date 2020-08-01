package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.fml.RegistryObject;

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

        parentedBlock(MaterialsSetup.ALUMINUM_ORE.get(), "block/aluminum_ore");
        parentedBlock(MaterialsSetup.CADMIUM_ORE.get(), "block/cadmium_ore");
        parentedBlock(MaterialsSetup.CHROMIUM_ORE.get(), "block/chromium_ore");
        parentedBlock(MaterialsSetup.COPPER_ORE.get(), "block/copper_ore");
        parentedBlock(MaterialsSetup.LEAD_ORE.get(), "block/lead_ore");
        parentedBlock(MaterialsSetup.SILVER_ORE.get(), "block/silver_ore");
        parentedBlock(MaterialsSetup.TIN_ORE.get(), "block/tin_ore");
        parentedBlock(MaterialsSetup.ZINC_ORE.get(), "block/zinc_ore");

        itemGenerated(MaterialsSetup.ALUMINUM_INGOT.get(), "item/aluminum_ingot");
        itemGenerated(MaterialsSetup.CADMIUM_INGOT.get(), "item/cadmium_ingot");
        itemGenerated(MaterialsSetup.CHROMIUM_INGOT.get(), "item/chromium_ingot");
        itemGenerated(MaterialsSetup.SILVER_INGOT.get(), "item/silver_ingot");
        itemGenerated(MaterialsSetup.BRASS_INGOT.get(), "item/brass_ingot");
        itemGenerated(MaterialsSetup.TIN_INGOT.get(), "item/tin_ingot");
        itemGenerated(MaterialsSetup.COPPER_INGOT.get(), "item/copper_ingot");
        itemGenerated(MaterialsSetup.LEAD_INGOT.get(), "item/lead_ingot");
        itemGenerated(MaterialsSetup.BRONZE_INGOT.get(), "item/bronze_ingot");
        itemGenerated(MaterialsSetup.ZINC_INGOT.get(), "item/zinc_ingot");

        for (RegistryObject<Item> flake_reg : MaterialsSetup.FLAKES) {
            Item flake = flake_reg.get();
            itemGenerated(flake, "item/" + flake.getRegistryName().getPath());
        }

        Allomancy.LOGGER.debug("Creating Item Model for allomancy:vial (filled)");
        ModelFile mf = getBuilder("vial_filled").parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/full_vial"));
        Allomancy.LOGGER.debug("Creating Item Model for allomancy:vial");
        getBuilder("vial").parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/vial"))
                .override().predicate(mcLoc("custom_model_data"), 1).model(mf).end();

    }

    public void parentedBlock(Block block, String model) {
        Allomancy.LOGGER.debug("Creating Item Model for " + block.getRegistryName());
        getBuilder(block.getRegistryName().getPath())
                .parent(new ModelFile.UncheckedModelFile(modLoc(model)));
    }

    public void itemGenerated(Item item, String texture) {
        Allomancy.LOGGER.debug("Creating Item Model for " + item.getRegistryName());
        getBuilder(item.getRegistryName().getPath()).parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc(texture));
    }

    public void itemHandheld(Item item, String texture) {
        Allomancy.LOGGER.debug("Creating Item Model for " + item.getRegistryName());
        getBuilder(item.getRegistryName().getPath()).parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", modLoc(texture));
    }

    public void largeItemHandheld(Item item, String texture) {
        Allomancy.LOGGER.debug("Creating Large Item Model for " + item.getRegistryName());
        getBuilder(item.getRegistryName().getPath()).parent(getExistingFile(modLoc("item/handheld_large")))
                .texture("layer0", modLoc(texture));
    }

    @Override
    public String getName() {
        return "Allomancy Item Models";
    }
}
