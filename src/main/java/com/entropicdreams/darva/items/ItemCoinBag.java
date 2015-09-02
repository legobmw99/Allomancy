package com.entropicdreams.darva.items;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.common.Registry;
import com.entropicdreams.darva.entity.EntityGoldNugget;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class ItemCoinBag extends Item{
	public ItemCoinBag(int par1) {
		super(par1);
		// TODO Auto-generated constructor stub
		this.setUnlocalizedName("allomancy:CoinBag");
		this.setCreativeTab(Registry.tabsAllomancy);
		this.maxStackSize = 1;
		}
	

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        ArrowNockEvent event = new ArrowNockEvent(par3EntityPlayer, par1ItemStack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return event.result;
        }

        if (par3EntityPlayer.capabilities.isCreativeMode || par3EntityPlayer.inventory.hasItem(Item.goldNugget.itemID) && AllomancyData.forPlayer(par3EntityPlayer).MetalBurning[AllomancyData.matSteel])
        {
            EntityGoldNugget entitygold = new EntityGoldNugget(par2World, par3EntityPlayer,  2.0F);
            par2World.spawnEntityInWorld(entitygold);
            par3EntityPlayer.inventory.consumeInventoryItem(Item.goldNugget.itemID);

        }

        return par1ItemStack;
    }


	   public int getItemEnchantability()
	    {
	        return 0;
	    }
}
