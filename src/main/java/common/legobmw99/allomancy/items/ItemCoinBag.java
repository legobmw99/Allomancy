package common.legobmw99.allomancy.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import common.legobmw99.allomancy.common.AllomancyCapabilities;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.entity.EntityGoldNugget;

public class ItemCoinBag extends Item{
	public ItemCoinBag() {
		super();
		this.setUnlocalizedName("coinbag");
		this.setCreativeTab(Registry.tabsAllomancy);
		this.maxStackSize = 1;
		}
	

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        ItemStack itemstack = this.findArrow(par3EntityPlayer);
        if (par3EntityPlayer.capabilities.isCreativeMode || itemstack != null && AllomancyCapabilities.forPlayer(par3EntityPlayer).MetalBurning[AllomancyCapabilities.matSteel]){
        		EntityGoldNugget entitygold = new EntityGoldNugget(par2World, par3EntityPlayer);
        		par2World.spawnEntityInWorld(entitygold);
        		if(!par3EntityPlayer.capabilities.isCreativeMode){
        			--itemstack.stackSize;
        		}
        	}        
        return par1ItemStack;
    }

	/*
	 * Finds items in inventory
	 */
	  private ItemStack findArrow(EntityPlayer player){
	            for (int i = 0; i < player.inventory.getSizeInventory(); ++i){
	                ItemStack itemstack = player.inventory.getStackInSlot(i);
	                if (this.isArrow(itemstack)){
	                    return itemstack;
	                }
	            }

	            return null;
	  }

	  protected boolean isArrow(ItemStack stack){
	        return stack != null && stack.getItem() ==  Items.GOLD_NUGGET;
	  }
	  
	  public int getItemEnchantability()
	    {
	        return 0;
	    }
}