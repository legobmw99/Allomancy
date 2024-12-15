package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

import java.util.List;

public class LerasiumItem extends Item {
    private static final FoodProperties lerasium_food =
            new FoodProperties.Builder().alwaysEdible().saturationModifier(0).nutrition(0).build();

    private static final Consumable lerasium_consumable = Consumable
            .builder()
            .consumeSeconds(0.2f)
            .animation(ItemUseAnimation.EAT)
            .sound(SoundEvents.GENERIC_EAT)
            .hasConsumeParticles(false)
            .build();

    public LerasiumItem(Item.Properties props) {
        super(props.rarity(Rarity.EPIC).stacksTo(1).food(lerasium_food, lerasium_consumable));
    }


    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {

        if (!player.getData(AllomancerAttachment.ALLOMANCY_DATA).isMistborn()) {
            player.startUsingItem(hand);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;

    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }
        if (livingEntity instanceof Player) {
            livingEntity.getData(AllomancerAttachment.ALLOMANCY_DATA).setMistborn();
            //Fancy-shmancy effects
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, world);
            lightning.setVisualOnly(true);
            lightning.moveTo(livingEntity.position().add(0, 3, 0));
            world.addFreshEntity(lightning);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 0, true, false));
        }

        return super.finishUsingItem(stack, world, livingEntity);
    }


    @Override
    public void appendHoverText(ItemStack stack,
                                Item.TooltipContext ctx,
                                List<Component> tooltip,
                                TooltipFlag flagIn) {
        super.appendHoverText(stack, ctx, tooltip, flagIn);
        Component lore =
                ItemDisplay.addColorToText("item.allomancy.lerasium_nugget.lore", ChatFormatting.LIGHT_PURPLE);
        tooltip.add(lore);

    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return 4;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        //Add enchantment glint
        return true;
    }
}