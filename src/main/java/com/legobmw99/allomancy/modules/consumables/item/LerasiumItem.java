package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

import java.util.List;

public class LerasiumItem extends Item {

    private static final Consumable lerasium_consumable = Consumable
            .builder()
            .consumeSeconds(0.2f)
            .animation(ItemUseAnimation.EAT)
            .sound(SoundEvents.GENERIC_EAT)
            .hasConsumeParticles(false)
            .build();

    public LerasiumItem(Item.Properties props) {
        super(props
                      .rarity(Rarity.EPIC)
                      .stacksTo(1)
                      .component(DataComponents.CONSUMABLE, lerasium_consumable)
                      .component(DataComponents.LORE, new ItemLore(
                              List.of(ItemDisplay.addColorToText("item.allomancy.lerasium_nugget.lore",
                                                                 ChatFormatting.LIGHT_PURPLE)))));
    }


    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (world instanceof ServerLevel level && player instanceof ServerPlayer sp) {
            if (player.getData(AllomancerAttachment.ALLOMANCY_DATA).isMistborn()) {
                return InteractionResult.FAIL;
            }

            var entangled = level.getServer().getAdvancements().get(Allomancy.rl("main/dna_entangled"));
            if (entangled != null && sp.getAdvancements().getOrStartProgress(entangled).isDone()) {
                return InteractionResult.FAIL;
            }

            player.startUsingItem(hand);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity livingEntity) {

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
    public boolean isFoil(ItemStack stack) {
        //Add enchantment glint
        return true;
    }
}