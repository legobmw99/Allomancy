package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class KolossBladeItem extends SwordItem {
    public KolossBladeItem() {
        super(Tiers.STONE, 9, -2.6F, new Item.Properties());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (entityIn instanceof Player player) {
            if (isSelected && (player.getOffhandItem() != stack)) {
                if (!(player.hasEffect(MobEffects.DAMAGE_BOOST) && player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() >= 2) &&
                    player.getCapability(AllomancerCapability.PLAYER_CAP).filter(data -> data.isBurning(Metal.PEWTER)).isEmpty()) {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 10, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 10, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0, true, false));
                }
            }
        }
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return (attacker instanceof Player) && (attacker.getCapability(AllomancerCapability.PLAYER_CAP).filter(data -> data.isBurning(Metal.PEWTER)).isPresent());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        Component lore = ItemDisplay.addColorToText("item.allomancy.koloss_blade.lore", ChatFormatting.GRAY);
        tooltip.add(lore);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState blockIn) {
        return false;
    }
}
