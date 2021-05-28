package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancyCapability;
import com.legobmw99.allomancy.util.Metal;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class VialItem extends Item {

    public VialItem() {
        super(Allomancy.createStandardItemProperties().stacksTo(32));
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity livingEntity) {

        if (!stack.hasTag()) {
            return stack;
        }

        livingEntity.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
            for (Metal mt : Metal.values()) {
                if (stack.getTag().contains(mt.getName()) && stack.getTag().getBoolean(mt.getName())) {
                    if (data.getAmount(mt) < 10) {
                        data.setAmount(mt, data.getAmount(mt) + 1);
                    }
                }
            }
        });

        if (!((PlayerEntity) (livingEntity)).abilities.instabuild) {
            stack.shrink(1);

            if (!((PlayerEntity) livingEntity).inventory.add(new ItemStack(ConsumeSetup.VIAL.get(), 1))) {
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
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }


    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);
        ActionResult<ItemStack> res = playerIn.getCapability(AllomancyCapability.PLAYER_CAP).map(data -> {
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

                if (filling == full) {
                    return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
                }

                playerIn.startUsingItem(hand);
                return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);
            }
            return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
        }).orElse(new ActionResult<>(ActionResultType.FAIL, itemStackIn));
        return res;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag()) {
            boolean full_display = Screen.hasShiftDown();
            int count = 0;
            for (Metal mt : Metal.values()) {
                if (stack.getTag().getBoolean(mt.getName())) {
                    count++;
                    if (full_display) {
                        IFormattableTextComponent metal = Allomancy.addColorToText("metals." + mt.getName(), TextFormatting.GRAY);
                        tooltip.add(metal);
                    }
                }
            }
            if (!full_display) {
                IFormattableTextComponent lcount = Allomancy.addColorToText("item.allomancy.vial.lore_count", TextFormatting.GRAY, count);
                tooltip.add(lcount);
                IFormattableTextComponent linst = Allomancy.addColorToText("item.allomancy.vial.lore_inst", TextFormatting.GRAY);
                tooltip.add(linst);

            }
        }
    }


    @Override
    public Rarity getRarity(ItemStack stack) {
        return stack.hasTag() ? Rarity.UNCOMMON : Rarity.COMMON;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (group == Allomancy.allomancy_group) {
            items.add(new ItemStack(this, 1));

            ItemStack resultItem = new ItemStack(ConsumeSetup.VIAL.get(), 1);
            CompoundNBT nbt = new CompoundNBT();
            for (Metal mt : Metal.values()) {
                nbt.putBoolean(mt.getName(), true);
            }
            nbt.putInt("CustomModelData", 1);
            resultItem.setTag(nbt);
            items.add(resultItem);
        }
    }

}
