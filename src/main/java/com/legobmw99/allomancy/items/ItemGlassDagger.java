package com.legobmw99.allomancy.items;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemGlassDagger extends Item {
    public ItemGlassDagger() {
        super();
        this.setCreativeTab(Registry.tabsAllomancy);
        this.maxStackSize = 1;
        this.setUnlocalizedName("glassdagger");
        this.setRegistryName(new ResourceLocation(Allomancy.MODID, "glassdagger"));
        this.setContainerItem(this);
    }

    public float getDamageVsEntity(Entity entity) {
        return 6;
    }

    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 3500;
    }
}