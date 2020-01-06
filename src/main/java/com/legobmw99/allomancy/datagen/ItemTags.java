package com.legobmw99.allomancy.datagen;

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
        ResourceLocation brass = new ResourceLocation("forge", "ingots/brass");
        getBuilder(new Tag<Item>(brass)).replace(false).add(MaterialsSetup.BRASS_INGOT.get()).build(brass);
        ResourceLocation tin = new ResourceLocation("forge", "ingots/tin");
        getBuilder(new Tag<Item>(tin)).replace(false).add(MaterialsSetup.TIN_INGOT.get()).build(tin);
        ResourceLocation zinc = new ResourceLocation("forge", "ingots/zinc");
        getBuilder(new Tag<Item>(zinc)).replace(false).add(MaterialsSetup.ZINC_INGOT.get()).build(zinc);
        ResourceLocation copper = new ResourceLocation("forge", "ingots/copper");
        getBuilder(new Tag<Item>(copper)).replace(false).add(MaterialsSetup.COPPER_INGOT.get()).build(copper);
        ResourceLocation bronze = new ResourceLocation("forge", "ingots/bronze");
        getBuilder(new Tag<Item>(bronze)).replace(false).add(MaterialsSetup.BRONZE_INGOT.get()).build(bronze);
        ResourceLocation lead = new ResourceLocation("forge", "ingots/lead");
        getBuilder(new Tag<Item>(lead)).replace(false).add(MaterialsSetup.LEAD_INGOT.get()).build(lead);
        ResourceLocation steel = new ResourceLocation("forge", "ingots/steel");
        getBuilder(new Tag<Item>(steel)).replace(false).build(steel);
        ResourceLocation pewter = new ResourceLocation("forge", "ingots/pewter");
        getBuilder(new Tag<Item>(pewter)).replace(false).build(pewter);
    }

    @Override
    public String getName() {
        return "Allomancy Item Tags";
    }
}
