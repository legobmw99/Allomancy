package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class VialItem extends Item {

    public VialItem() {
        super(new Item.Properties().stacksTo(32));
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity livingEntity) {

        if (!stack.hasTag() || !livingEntity.hasData(AllomancerAttachment.ALLOMANCY_DATA)) {
            return stack;
        }


        var data = livingEntity.getData(AllomancerAttachment.ALLOMANCY_DATA);
        for (Metal mt : Metal.values()) {
            if (stack.getTag().contains(mt.getName()) && stack.getTag().getBoolean(mt.getName())) {
                if (data.getAmount(mt) < 10) {
                    data.setAmount(mt, data.getAmount(mt) + 1);
                }
            }
        }

        if (!((Player) (livingEntity)).getAbilities().instabuild) {
            stack.shrink(1);

            if (!((Player) livingEntity).getInventory().add(new ItemStack(ConsumeSetup.VIAL.get(), 1))) {
                world.addFreshEntity(new ItemEntity(world, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), new ItemStack(ConsumeSetup.VIAL.get(), 1)));
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 6;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);

        if (playerIn.hasData(AllomancerAttachment.ALLOMANCY_DATA)) {
            var data = playerIn.getData(AllomancerAttachment.ALLOMANCY_DATA);
            //If all the ones being filled are full, don't allow
            int filling = 0, full = 0;
            if (itemStackIn.hasTag()) {
                for (Metal mt : Metal.values()) {
                    if (itemStackIn.getTag().contains(mt.getName()) && itemStackIn.getTag().getBoolean(mt.getName())) {
                        filling++;
                        if (data.getAmount(mt) >= 10) {
                            full++;
                        }
                    }
                }

                if (filling != full) {
                    playerIn.startUsingItem(hand);
                    return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStackIn);
                }
            }
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemStackIn);
        } else {
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemStackIn);
        }


    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag()) {
            boolean full_display = Screen.hasShiftDown();
            int count = 0;
            for (Metal mt : Metal.values()) {
                if (stack.getTag().getBoolean(mt.getName())) {
                    count++;
                    if (full_display) {
                        MutableComponent metal = ItemDisplay.addColorToText("metals." + mt.getName(), ChatFormatting.GRAY);
                        tooltip.add(metal);
                    }
                }
            }
            if (!full_display) {
                MutableComponent lcount = ItemDisplay.addColorToText("item.allomancy.vial.lore_count", ChatFormatting.GRAY, count);
                tooltip.add(lcount);
                MutableComponent linst = ItemDisplay.addColorToText("item.allomancy.vial.lore_inst", ChatFormatting.GRAY);
                tooltip.add(linst);

            }
        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return stack.hasTag() ? Rarity.UNCOMMON : Rarity.COMMON;
    }

}
