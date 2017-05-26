package com.legobmw99.allomancy.items;

import com.legobmw99.allomancy.util.AllomancyCapabilities;
import com.legobmw99.allomancy.util.Registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemVial extends Item {
    public static String[] unlocalName = { "emptyvial", "ironelixer", "steelelixer", "tinelixer", "pewterelixer", "zincelixer", "brasselixer", "copperelixer", "bronzeelixer", "ultimateelixer" };

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        AllomancyCapabilities cap;
        cap = AllomancyCapabilities.forPlayer(entityLiving);

        if (!((EntityPlayer) (entityLiving)).capabilities.isCreativeMode) {
            stack.shrink(1);
            ((EntityPlayer) (entityLiving)).inventory.addItemStackToInventory(new ItemStack(Registry.itemVial, 1, 0));
        }
        if (cap == null) {
            return stack;
        }

        if (stack.getItemDamage() == 0) {
            return stack;
        }

        if (stack.getItemDamage() < 9) {
            cap.setMetalAmounts(stack.getItemDamage() - 1, cap.getMetalAmounts(stack.getItemDamage() - 1) + 1);
        } else if (stack.getItemDamage() < 10) {
            for (int i = 0; i < 8; i++) {
                if (cap.getMetalAmounts(i) < 10) {
                    cap.setMetalAmounts(i, cap.getMetalAmounts(i) + 1);
                }
            }
        }
        return stack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 6;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        AllomancyCapabilities cap;
        cap = AllomancyCapabilities.forPlayer(playerIn);
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        // Checks both the metal amount (we only want to fill up to 10) and the item damage (can't drink empty vials)
        if (itemStackIn.getItemDamage() > 0) {
            if ((itemStackIn.getItemDamage() == 9 && !checkFullCapacity(cap)) // Check to see if it is the ultimate vial and that there is space for at least one metal
                    || (itemStackIn.getItemDamage() < 9 && cap.getMetalAmounts(itemStackIn.getItemDamage() - 1) < 10)) { // If it is just a normal vial, make sure that there is space for that one specific metal
                playerIn.setActiveHand(hand);
                return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
            } else {
                return new ActionResult(EnumActionResult.FAIL, itemStackIn);
            }
        } else {
            return new ActionResult(EnumActionResult.FAIL, itemStackIn);
        }
    }

    /**
     * Checks to see if every single metal is full. Used for ultimate vial
     * 
     * @param cap
     *            the player's Allomancy Capability
     * @return whether or not all metals are full
     */
    private boolean checkFullCapacity(AllomancyCapabilities cap) {
        for (int i = 0; i < 8; i++) {
            if (cap.getMetalAmounts(i) < 10) {
                return false;
            }
        }
        return true;
    }

    public ItemVial() {
        this.setHasSubtypes(true);
        this.setCreativeTab(Registry.tabsAllomancy);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        int meta = itemStack.getItemDamage();
        if ((meta < 0) || (meta >= unlocalName.length)) {
            meta = 0;
        }
        return "item.itemVial" + "." + unlocalName[meta];
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return stack.getItemDamage() == 9 ? EnumRarity.RARE : EnumRarity.COMMON;
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (int meta = 0; meta < ItemVial.unlocalName.length; meta++) {
            subItems.add(new ItemStack(item, 1, meta));
        }
    }
}
