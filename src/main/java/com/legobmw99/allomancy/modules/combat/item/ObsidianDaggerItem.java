package com.legobmw99.allomancy.modules.combat.item;

import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ObsidianDaggerItem extends SwordItem {

    private static final int ATTACK_DAMAGE = 12;
    private static final float ATTACK_SPEED = 9.2F;

    private static final Tier tier = new ObsidianTier();

    public ObsidianDaggerItem() {
        super(tier, new Item.Properties()
                .attributes(createAttributes(tier, ATTACK_DAMAGE, ATTACK_SPEED))
                .rarity(Rarity.UNCOMMON));
    }

    // prevent dagger from mining
    @Override
    public boolean isCorrectToolForDrops(ItemStack pStack, BlockState pState) {
        return false;
    }

    // Disable mending on daggers
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        if (EnchantmentHelper
                .getEnchantmentsForCrafting(book)
                .keySet()
                .stream()
                .anyMatch(holder -> holder.value().effects().has(EnchantmentEffectComponents.REPAIR_WITH_XP) ||
                                    holder.value().effects().has(EnchantmentEffectComponents.ITEM_DAMAGE))) {
            return false;
        }
        return super.isBookEnchantable(stack, book);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.MENDING) || enchantment.is(Enchantments.UNBREAKING)) {
            return false;
        }
        return super.isPrimaryItemFor(stack, enchantment);
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return 0;
    }

    private static class ObsidianTier implements Tier {
        @Override
        public int getUses() {
            return 2;
        }

        @Override
        public float getSpeed() {
            return ATTACK_SPEED;
        }

        @Override
        public float getAttackDamageBonus() {
            return ATTACK_DAMAGE;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 1;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(Blocks.OBSIDIAN);
        }

    }
}
