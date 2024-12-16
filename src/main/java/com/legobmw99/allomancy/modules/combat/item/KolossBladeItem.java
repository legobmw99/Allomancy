package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class KolossBladeItem extends SwordItem {

    private static final int ATTACK_DAMAGE = 17;
    private static final float ATTACK_SPEED = -2.6F;

    private static final ToolMaterial SLOW_STONE =
            new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, ATTACK_SPEED, 1.0F, ATTACK_DAMAGE,
                             ItemTags.STONE_TOOL_MATERIALS);

    public KolossBladeItem(Item.Properties props) {
        super(SLOW_STONE.applySwordProperties(props, ATTACK_DAMAGE, ATTACK_SPEED));
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
