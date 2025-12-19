package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.UseEffects;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;

public class HorseshoeItem extends Item {

    private int ticks = 0;

    public HorseshoeItem(Properties properties) {
        super(properties.durability(256).component(DataComponents.USE_EFFECTS, new UseEffects(true, true, 1.0f)));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        var data = AllomancerAttachment.get(player);
        if (data.isBurning(Metal.STEEL) && data.isBurning(Metal.IRON) &&
            player.getAvailableSpaceBelow(13.0) <= 12.0) {

            stack.setDamageValue(stack.getDamageValue() + 1);
            player.startUsingItem(hand);
            grantFlight(player);

            return InteractionResult.SUCCESS;
        }

        revokeFlight(player);
        return InteractionResult.FAIL;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 40;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof Player player) {
            var data = AllomancerAttachment.get(player);
            if (data.isBurning(Metal.STEEL) && data.isBurning(Metal.IRON) &&
                player.getAvailableSpaceBelow(13.0) <= 12.0) {
                grantFlight(player);
                ticks++;
                if (ticks % 5 == 0) {
                    level.playSound(player, player.getX(), player.getY() - 10, player.getZ(),
                                    SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.4f, 0.9F);

                }
            } else {
                revokeFlight(player);
            }
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity livingEntity, int count) {
        super.onStopUsing(stack, livingEntity, count);
        if (livingEntity instanceof Player player) {
            revokeFlight(player);
        }
    }


    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BRUSH;
    }

    private static final Identifier USING_MODIFIER = Allomancy.id("using_horseshoe");

    private static void grantFlight(Player player) {
        player
                .getAttribute(NeoForgeMod.CREATIVE_FLIGHT)
                .addOrUpdateTransientModifier(
                        new AttributeModifier(USING_MODIFIER, 1, AttributeModifier.Operation.ADD_VALUE));
        player.getAbilities().flying = true;
    }

    private static void revokeFlight(Player player) {
        player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT).removeModifier(USING_MODIFIER);
        player.getAbilities().flying = false;
    }

}
