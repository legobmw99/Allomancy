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
        itemHandheld(ExtrasSetup.IRON_LEVER_ITEM.get(), "block/iron_lever");

        itemHandheld(CombatSetup.MISTCLOAK.get(), "item/mistcloak");
        itemHandheld(CombatSetup.COIN_BAG.get(), "item/coin_bag");
        itemHandheld(CombatSetup.OBSIDIAN_DAGGER.get(), "item/obsidian_dagger");


        itemHandheld(ConsumeSetup.ALLOMANTIC_GRINDER.get(), "item/allomantic_grinder");
        itemHandheld(ConsumeSetup.VIAL.get(), "item/vial");
        itemHandheld(ConsumeSetup.LERASIUM_NUGGET.get(), "item/lerasium_nugget");

        parentedBlock(MaterialsSetup.COPPER_ORE.get(), "block/copper_ore");
        parentedBlock(MaterialsSetup.TIN_ORE.get(), "block/tin_ore");
        parentedBlock(MaterialsSetup.ZINC_ORE.get(), "block/zinc_ore");
        parentedBlock(MaterialsSetup.LEAD_ORE.get(), "block/lead_ore");
        itemHandheld(MaterialsSetup.BRASS_INGOT.get(), "item/brass_ingot");
        itemHandheld(MaterialsSetup.TIN_INGOT.get(), "item/tin_ingot");
        itemHandheld(MaterialsSetup.COPPER_INGOT.get(), "item/copper_ingot");
        itemHandheld(MaterialsSetup.LEAD_INGOT.get(), "item/lead_ingot");
        itemHandheld(MaterialsSetup.BRONZE_INGOT.get(), "item/bronze_ingot");
        itemHandheld(MaterialsSetup.ZINC_INGOT.get(), "item/zinc_ingot");

        for (RegistryObject<Item> flake_reg : MaterialsSetup.FLAKES) {
            Item flake = flake_reg.get();
            itemHandheld(flake, "item/" + flake.getRegistryName().getPath());
        }

    }

    public void parentedBlock(Block block, String model) {
        Allomancy.LOGGER.debug("Creating Item Model for " + block.getRegistryName());
        getBuilder(block.getRegistryName().getPath())
                .parent(new ModelFile.UncheckedModelFile(modLoc(model)));
    }

    public void itemHandheld(Item item, String texture) {
        Allomancy.LOGGER.debug("Creating Item Model for " + item.getRegistryName());
        getBuilder(item.getRegistryName().getPath()).parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", modLoc(texture));
    }

    @Override
    public String getName() {
        return "Allomancy Item Models";
    }
}
