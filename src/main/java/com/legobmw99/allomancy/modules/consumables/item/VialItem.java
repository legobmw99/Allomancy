package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class VialItem extends Item {

    public VialItem() {
        super(Allomancy.createStandardItemProperties().stacksTo(32));
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity livingEntity) {

        if (!stack.hasTag()) {
            return stack;
        }

        livingEntity.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
            for (Metal mt : Metal.values()) {
                if (stack.getTag().contains(mt.getName()) && stack.getTag().getBoolean(mt.getName())) {
                    if (data.getAmount(mt) < 10) {
                        data.setAmount(mt, data.getAmount(mt) + 1);
                    }
                }
            }
        });

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
        InteractionResultHolder<ItemStack> res = playerIn.getCapability(AllomancerCapability.PLAYER_CAP).map(data -> {
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
        }).orElse(new InteractionResultHolder<>(InteractionResult.FAIL, itemStackIn));
        return res;
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
                        MutableComponent metal = Allomancy.addColorToText("metals." + mt.getName(), ChatFormatting.GRAY);
                        tooltip.add(metal);
                    }
                }
            }
            if (!full_display) {
                MutableComponent lcount = Allomancy.addColorToText("item.allomancy.vial.lore_count", ChatFormatting.GRAY, count);
                tooltip.add(lcount);
                MutableComponent linst = Allomancy.addColorToText("item.allomancy.vial.lore_inst", ChatFormatting.GRAY);
                tooltip.add(linst);

            }
        }
    }


    @Override
    public Rarity getRarity(ItemStack stack) {
        return stack.hasTag() ? Rarity.UNCOMMON : Rarity.COMMON;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (group == Allomancy.allomancy_group) {
            items.add(new ItemStack(this, 1));

            ItemStack resultItem = new ItemStack(ConsumeSetup.VIAL.get(), 1);
            CompoundTag nbt = new CompoundTag();
            for (Metal mt : Metal.values()) {
                nbt.putBoolean(mt.getName(), true);
            }
            nbt.putInt("CustomModelData", 1);
            resultItem.setTag(nbt);
            items.add(resultItem);
        }
    }

}
