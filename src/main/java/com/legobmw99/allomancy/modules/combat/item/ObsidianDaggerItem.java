package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

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
