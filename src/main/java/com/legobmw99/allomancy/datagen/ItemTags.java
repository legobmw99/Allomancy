package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ItemTags extends ItemTagsProvider {


    public ItemTags(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        addForgeTag("ingots/brass", MaterialsSetup.BRASS_INGOT.get());
        addForgeTag("ingots/tin", MaterialsSetup.TIN_INGOT.get());
        addForgeTag("ingots/zinc", MaterialsSetup.ZINC_INGOT.get());
        addForgeTag("ingots/copper", MaterialsSetup.COPPER_INGOT.get());
        addForgeTag("ingots/bronze", MaterialsSetup.BRONZE_INGOT.get());
        addForgeTag("ingots/lead", MaterialsSetup.LEAD_INGOT.get());
        addForgeTag("ingots/steel");
        addForgeTag("ingots/pewter");

        addForgeTag("ores/lead", MaterialsSetup.LEAD_ORE_ITEM.get());
        addForgeTag("ores/tin", MaterialsSetup.TIN_ORE_ITEM.get());
        addForgeTag("ores/copper", MaterialsSetup.COPPER_ORE_ITEM.get());
        addForgeTag("ores/zinc", MaterialsSetup.ZINC_ORE_ITEM.get());

    }

    private void addForgeTag(String name, Item... items) {
        Allomancy.LOGGER.debug("Creating item tag for forge:" + name);
        ResourceLocation loc = new ResourceLocation("forge", name);
        getBuilder(new Tag<Item>(loc)).replace(false).add(items).build(loc);
    }


    @Override
    public String getName() {
        return "Allomancy Item Tags";
    }
}
