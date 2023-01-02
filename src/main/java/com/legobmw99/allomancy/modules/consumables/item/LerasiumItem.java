package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class LerasiumItem extends Item {
    private static final FoodProperties lerasium = new FoodProperties.Builder().fast().alwaysEat().saturationMod(0).nutrition(0).build();

    public LerasiumItem() {
        super(ItemDisplay.createStandardItemProperties().rarity(Rarity.EPIC).stacksTo(1).food(lerasium));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStackIn = player.getItemInHand(hand);
        if (player.getCapability(AllomancerCapability.PLAYER_CAP).filter(data -> !data.isMistborn()).isPresent()) {
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStackIn);
        }

        return new InteractionResultHolder<>(InteractionResult.FAIL, itemStackIn);

    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity livingEntity) {

        livingEntity.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(IAllomancerData::setMistborn);
        //Fancy-shmancy effects
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, world);
        lightning.setVisualOnly(true);
        lightning.moveTo(livingEntity.position().add(0, 3, 0));
        world.addFreshEntity(lightning);
        livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 0, true, false));

        return super.finishUsingItem(stack, world, livingEntity);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        Component lore = ItemDisplay.addColorToText("item.allomancy.lerasium_nugget.lore", ChatFormatting.LIGHT_PURPLE);
        tooltip.add(lore);

    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 4;
    }

    @Override
    public boolean isFoil(ItemStack par1ItemStack) {
        //Add enchantment glint
        return true;
    }
}