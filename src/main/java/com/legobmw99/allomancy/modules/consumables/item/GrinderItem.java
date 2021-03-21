package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.setup.AllomancySetup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GrinderItem extends Item {

    public GrinderItem() {
        super(AllomancySetup.createStandardItemProperties().stacksTo(1));
    }

    @Override
    public boolean hasCraftingRemainingItem() {
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
