package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class KolossBladeItem extends SwordItem {

    private static final int ATTACK_DAMAGE = 9;
    private static final float ATTACK_SPEED = -2.6F;

    private static final Tier SLOW_STONE = new Tier() {
        @Override
        public int getUses() {
            return Tiers.STONE.getUses();
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
            return Tiers.STONE.getIncorrectBlocksForDrops();
        }

        @Override
        public int getEnchantmentValue() {
            return Tiers.STONE.getEnchantmentValue();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Tiers.STONE.getRepairIngredient();
        }

    };

    public KolossBladeItem() {
        super(SLOW_STONE,
              new Item.Properties().attributes(SwordItem.createAttributes(SLOW_STONE, ATTACK_DAMAGE, ATTACK_SPEED)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (entityIn instanceof Player player) {
            if (isSelected && (player.getOffhandItem() != stack)) {
                if (!(player.hasEffect(MobEffects.DAMAGE_BOOST) &&
                      player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() >= 2) &&
                    !player.getData(AllomancerAttachment.ALLOMANCY_DATA).isBurning(Metal.PEWTER)) {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 10, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 10, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0, true, false));
                }
            }
        }
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return (attacker instanceof Player) &&
               (attacker.getData(AllomancerAttachment.ALLOMANCY_DATA).isBurning(Metal.PEWTER));
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                Item.TooltipContext ctx,
                                List<Component> tooltip,
                                TooltipFlag flagIn) {
        super.appendHoverText(stack, ctx, tooltip, flagIn);
        Component lore = ItemDisplay.addColorToText("item.allomancy.koloss_blade.lore", ChatFormatting.GRAY);
        tooltip.add(lore);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack pStack, BlockState pState) {
        return false;
    }
}
