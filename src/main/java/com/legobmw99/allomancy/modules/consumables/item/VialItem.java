package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.setup.AllomancySetup;
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

        for (int i = 0; i < AllomancySetup.allomanctic_metals.length; i++) {
            if (stack.getTag().contains(AllomancySetup.allomanctic_metals[i]) && stack.getTag().getBoolean(AllomancySetup.allomanctic_metals[i])) {
                if (cap.getMetalAmounts(i) < 10) {
                    cap.setMetalAmounts(i, cap.getMetalAmounts(i) + 1);
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
            for (int i = 0; i < AllomancySetup.allomanctic_metals.length; i++) {
                if (itemStackIn.getTag().contains(AllomancySetup.allomanctic_metals[i]) && itemStackIn.getTag().getBoolean(AllomancySetup.allomanctic_metals[i])) {
                    filling++;
                    if (cap.getMetalAmounts(i) >= 10) {
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


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag()) {
            for (int i = 0; i < AllomancySetup.allomanctic_metals.length; i++) {
                if (stack.getTag().getBoolean(AllomancySetup.allomanctic_metals[i])) {
                    ITextComponent metal = new TranslationTextComponent("metals." + AllomancySetup.allomanctic_metals[i]);
                    metal.setStyle(metal.getStyle().setColor(TextFormatting.GRAY));
                    tooltip.add(metal);
                }
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
            for (int i = 0; i < AllomancySetup.allomanctic_metals.length; i++) {
                nbt.putBoolean(AllomancySetup.allomanctic_metals[i], true);
            }
            resultItem.setTag(nbt);
            items.add(resultItem);
        }
    }

}
