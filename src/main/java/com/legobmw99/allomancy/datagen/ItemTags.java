package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemTags extends ItemTagsProvider {


    public ItemTags(DataGenerator gen, BlockTagsProvider blockTagProvider) {
        super(gen, blockTagProvider);
    }

    @Override
    protected void registerTags() {
        addForgeTag("ingots/brass", MaterialsSetup.BRASS_INGOT.get());
        addForgeTag("ingots/tin", MaterialsSetup.TIN_INGOT.get());
        addForgeTag("ingots/zinc", MaterialsSetup.ZINC_INGOT.get());
        addForgeTag("ingots/copper", MaterialsSetup.COPPER_INGOT.get());
        addForgeTag("ingots/bronze", MaterialsSetup.BRONZE_INGOT.get());
        addForgeTag("ingots/lead", MaterialsSetup.LEAD_INGOT.get());
        addForgeTag("ingots/aluminum", MaterialsSetup.ALUMINUM_INGOT.get());
        addForgeTag("ingots/chromium", MaterialsSetup.CHROMIUM_INGOT.get());
        addForgeTag("ingots/cadmium", MaterialsSetup.CADMIUM_INGOT.get());
        addForgeTag("ingots/silver", MaterialsSetup.SILVER_INGOT.get());
        addForgeTag("ingots/pewter");
        addForgeTag("ingots/steel");
        addForgeTag("ingots/duralumin");
        addForgeTag("ingots/nicrosil");
        addForgeTag("ingots/electrum");
        addForgeTag("ingots/bendalloy");

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
        ResourceLocation loc = new ResourceLocation("forge", name);

        getOrCreateBuilder(net.minecraft.tags.ItemTags.makeWrapperTag("forge" + name)).replace(false).add(items);
    }


    @Override
    public String getName() {
        return "Allomancy Item Tags";
    }
}
