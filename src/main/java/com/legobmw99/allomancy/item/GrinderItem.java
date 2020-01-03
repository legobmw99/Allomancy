package com.legobmw99.allomancy.item;

import com.legobmw99.allomancy.setup.Registry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GrinderItem extends Item {

    public GrinderItem() {
        super(new Item.Properties().group(Registry.allomancy_group).maxStackSize(1));
    }

    @Override
    public boolean hasContainerItem() {
        return true;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }
}
