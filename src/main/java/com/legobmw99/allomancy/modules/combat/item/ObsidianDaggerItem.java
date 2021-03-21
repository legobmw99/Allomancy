package com.legobmw99.allomancy.modules.combat.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;

public class ObsidianDaggerItem extends SwordItem {

    private static final int ATTACK_DAMAGE = 12;
    private static final float ATTACK_SPEED = 9.2F;

    private static final IItemTier tier = new IItemTier() {
        @Override
        public int getUses() {
            return 8;
        }

        @Override
        public float getSpeed() {
            return 0;
        }

        @Override
        public float getAttackDamageBonus() {
            return ATTACK_DAMAGE;
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 1;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(Blocks.OBSIDIAN);
        }
    };

    public ObsidianDaggerItem() {
        super(tier, ATTACK_DAMAGE, ATTACK_SPEED, new Item.Properties().tab(ItemGroup.TAB_COMBAT));
    }

    // prevent dagger from mining
    @Override
    public boolean isCorrectToolForDrops(BlockState blockIn) {
        return false;
    }

    // Disable mending on daggers
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        if (EnchantmentHelper.getEnchantments(book).containsKey(Enchantments.MENDING)) {
            return false;
        }
        return super.isBookEnchantable(stack, book);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment.equals(Enchantments.MENDING)) {
            return false;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return 0;
    }

}
