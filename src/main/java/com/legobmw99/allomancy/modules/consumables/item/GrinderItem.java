package com.legobmw99.allomancy.modules.consumables.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GrinderItem extends Item {

    public GrinderItem(Item.Properties props) {
        super(props.durability(256));
    }

    @Override
    public ItemStack getCraftingRemainder(ItemStack itemStack) {
        ItemStack damaged = itemStack.copy();
        damaged.setDamageValue(damaged.getDamageValue() + 1);
        if (damaged.getDamageValue() >= damaged.getMaxDamage()) {
            damaged.setCount(0);
        }
        return damaged;
    }


    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }


}
