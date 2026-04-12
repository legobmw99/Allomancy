package com.legobmw99.allomancy.modules.consumables.item;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;

public class GrinderItem extends Item {

    public static final int MAX_DAMAGE = 256;

    public GrinderItem(Item.Properties props) {
        super(props.durability(MAX_DAMAGE));
    }


    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemInstance item) {
        int damage = item.getOrDefault(DataComponents.DAMAGE, 0) + 1;
        if (damage >= item.getOrDefault(DataComponents.MAX_DAMAGE, MAX_DAMAGE)) {
            return null; // empty
        }
        return new ItemStackTemplate(item.typeHolder(),
                                     DataComponentPatch.builder().set(DataComponents.DAMAGE, damage).build());
    }


    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }


}
