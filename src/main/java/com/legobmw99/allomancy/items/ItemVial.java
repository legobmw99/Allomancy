package com.legobmw99.allomancy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.Registry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemVial extends Item {

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(entityLiving);


        if (cap == null) {
            return stack;
        }

        if (!stack.hasTagCompound()) {
            return stack;
        }
        
    	for(int i = 0; i < 8; i++){
    		if(stack.getTagCompound().hasKey(Registry.flakeMetals[i]) && stack.getTagCompound().getBoolean(Registry.flakeMetals[i])){
    			if(cap.getMetalAmounts(i) < 10){
    				cap.setMetalAmounts(i, cap.getMetalAmounts(i) + 1);
    			}
    		}
    	}
    	
        if (!((EntityPlayer) (entityLiving)).capabilities.isCreativeMode) {
            stack.shrink(1);
            ((EntityPlayer) (entityLiving)).inventory.addItemStackToInventory(new ItemStack(Registry.itemVial, 1));
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
        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(playerIn);
        //If all the ones being filled are full, don't allow
        int filling = 0, full = 0; 
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (itemStackIn.getTagCompound() != null) {
        	for(int i = 0; i < 8; i++){
        		if(itemStackIn.getTagCompound().hasKey(Registry.flakeMetals[i]) && itemStackIn.getTagCompound().getBoolean(Registry.flakeMetals[i])){
        			filling++;
        			if(cap.getMetalAmounts(i) >= 10){
        				full++;
        			}
        		}
        	}
        	
        	if(filling == full){
	            return new ActionResult(EnumActionResult.FAIL, itemStackIn);
        	} 
        	
            playerIn.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
        } 
            return new ActionResult(EnumActionResult.FAIL, itemStackIn);
    }


    public ItemVial() {
        this.setCreativeTab(Registry.tabsAllomancy);
        this.setRegistryName(new ResourceLocation(Allomancy.MODID, "itemVial"));
		this.setUnlocalizedName("itemVial");

    }
    
	@Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced){
		if(stack.getTagCompound() != null){
			for(int i = 0; i < 8; i++){
				if(stack.getTagCompound().getBoolean(Registry.flakeMetals[i])){
					tooltip.add(Registry.flakeMetals[i]);
				}
			}
		}
	}


    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return stack.hasTagCompound() ? EnumRarity.UNCOMMON : EnumRarity.COMMON;
    }
    
 
}
