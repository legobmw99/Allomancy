package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GrinderItem extends Item {

    public GrinderItem() {
        super(Allomancy.createStandardItemProperties().setNoRepair().defaultDurability(256));
    }


    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack damaged = itemStack.copy();
        damaged.setDamageValue(damaged.getDamageValue() + 1);
        if (damaged.getDamageValue() >= damaged.getMaxDamage()) {
            damaged.setCount(0);
        }
        return damaged;
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }


    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }


}
