package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.setup.AllomancySetup;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class VialItem extends Item {

    public VialItem() {
        super(AllomancySetup.createStandardItemProperties().maxStackSize(32));
    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity livingEntity) {

        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(livingEntity);


        if (!stack.hasTag()) {
            return stack;
        }

        for (Metal mt : Metal.values()) {
            if (stack.getTag().contains(mt.getName()) && stack.getTag().getBoolean(mt.getName())) {
                if (cap.getAmount(mt) < 10) {
                    cap.setAmount(mt, cap.getAmount(mt) + 1);
                }
            }
        }

        if (!((PlayerEntity) (livingEntity)).abilities.isCreativeMode) {
            stack.shrink(1);
            ((PlayerEntity) (livingEntity)).inventory.addItemStackToInventory(new ItemStack(ConsumeSetup.VIAL.get(), 1));
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 6;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(playerIn);
        //If all the ones being filled are full, don't allow
        int filling = 0, full = 0;
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (itemStackIn.hasTag()) {
            for (Metal mt : Metal.values()) {
                if (itemStackIn.getTag().contains(mt.getName()) && itemStackIn.getTag().getBoolean(mt.getName())) {
                    filling++;
                    if (cap.getAmount(mt) >= 10) {
                        full++;
                    }
                }
            }

            if (filling == full) {
                return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
            }

            playerIn.setActiveHand(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);
        }
        return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag()) {
            boolean full_display = Screen.hasShiftDown();
            int count = 0;
            for (Metal mt : Metal.values()) {
                if (stack.getTag().getBoolean(mt.getName())) {
                    count++;
                    if (full_display) {
                        ITextComponent metal = new TranslationTextComponent("metals." + mt.getName());
                        metal.setStyle(metal.getStyle().setColor(TextFormatting.GRAY));
                        tooltip.add(metal);
                    }
                }
            }
            if (!full_display) {
                ITextComponent lcount = new TranslationTextComponent("item.allomancy.vial.lore_count", count);
                lcount.setStyle(lcount.getStyle().setColor(TextFormatting.GRAY));
                tooltip.add(lcount);
                ITextComponent linst = new TranslationTextComponent("item.allomancy.vial.lore_inst");
                linst.setStyle(linst.getStyle().setColor(TextFormatting.GRAY));
                tooltip.add(linst);

            }
        }
    }


    @Override
    public Rarity getRarity(ItemStack stack) {
        return stack.hasTag() ? Rarity.UNCOMMON : Rarity.COMMON;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (group == AllomancySetup.allomancy_group) {
            items.add(new ItemStack(this, 1));

            ItemStack resultItem = new ItemStack(ConsumeSetup.VIAL.get(), 1);
            CompoundNBT nbt = new CompoundNBT();
            for (Metal mt : Metal.values()) {
                nbt.putBoolean(mt.getName(), true);
            }
            resultItem.setTag(nbt);
            items.add(resultItem);
        }
    }

}
