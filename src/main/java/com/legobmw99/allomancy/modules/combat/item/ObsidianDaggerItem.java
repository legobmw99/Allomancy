package com.legobmw99.allomancy.modules.combat.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.Ingredient;

public class ObsidianDaggerItem extends SwordItem {

    private static final int ATTACK_DAMAGE = 12;
    private static final float ATTACK_SPEED = 9.2F;

    private static final IItemTier tier = new IItemTier() {
        @Override
        public int getMaxUses() {
            return 8;
        }

        @Override
        public float getEfficiency() {
            return 0;
        }

        @Override
        public float getAttackDamage() {
            return ATTACK_DAMAGE;
        }

        @Override
        public int getHarvestLevel() {
            return 0;
        }

        @Override
        public int getEnchantability() {
            return 1;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return Ingredient.fromItems(Blocks.OBSIDIAN);
        }
    };

    public ObsidianDaggerItem() {
        super(tier, ATTACK_DAMAGE, ATTACK_SPEED, new Item.Properties().group(ItemGroup.COMBAT));
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
}
