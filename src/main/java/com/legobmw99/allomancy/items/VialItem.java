package com.legobmw99.allomancy.items;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class VialItem extends Item {

    public VialItem() {
        super(new Item.Properties().group(Registry.allomancy_group).maxStackSize(16));
        this.setRegistryName(new ResourceLocation(Allomancy.MODID, "vial"));

    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if(player == null){
            return ActionResultType.FAIL;
        }

        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(player);

        ItemStack stack = player.getHeldItem(context.getHand());

        if (cap == null) {
           return ActionResultType.PASS;
        }

        if (!stack.hasTag()) {
            return ActionResultType.PASS;
        }
        
    	for(int i = 0; i < 8; i++){
    		if(stack.getTag().contains(Registry.flake_metals[i]) && stack.getTag().getBoolean(Registry.flake_metals[i])){
    			if(cap.getMetalAmounts(i) < 10){
    				cap.setMetalAmounts(i, cap.getMetalAmounts(i) + 1);
    			}
    		}
    	}
    	
        if (!player.abilities.isCreativeMode) {
            stack.shrink(1);
            player.inventory.addItemStackToInventory(new ItemStack(Registry.vial, 1));
        }
    	
        return ActionResultType.SUCCESS;
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
        	for(int i = 0; i < 8; i++){
        		if(itemStackIn.getTag().contains(Registry.flake_metals[i]) && itemStackIn.getTag().getBoolean(Registry.flake_metals[i])){
        			filling++;
        			if(cap.getMetalAmounts(i) >= 10){
        				full++;
        			}
        		}
        	}
        	
        	if(filling == full){
	            return new ActionResult<ItemStack> (ActionResultType.PASS, itemStackIn);
        	} 
        	
            playerIn.setActiveHand(hand);
            return new ActionResult<ItemStack> (ActionResultType.SUCCESS, itemStackIn);
        } 
            return new ActionResult<ItemStack>(ActionResultType.FAIL, itemStackIn);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if(stack.hasTag()){
            for(int i = 0; i < 8; i++){
                if(stack.getTag().getBoolean(Registry.flake_metals[i])){
                    tooltip.add(new StringTextComponent(Registry.flake_metals[i]));
                }
            }

        }
    }



    @Override
    public Rarity getRarity(ItemStack stack)
    {
        return stack.hasTag() ? Rarity.UNCOMMON : Rarity.COMMON;
    }



   /* todo investigate
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) {
            subItems.add(new ItemStack(this, 1, 0));

            ItemStack resultItem = new ItemStack(Registry.vial, 1);
            CompoundNBT nbt = new CompoundNBT();
            for(int i = 0; i < 8; i++){
                nbt.setBoolean(Registry.flake_metals[i], true);
            }
            resultItem.setTagCompound(nbt);
            subItems.add(resultItem);

        }
    }*/
 
}
