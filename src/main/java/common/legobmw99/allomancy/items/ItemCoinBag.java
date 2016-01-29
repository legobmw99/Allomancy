package common.legobmw99.allomancy.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import common.legobmw99.allomancy.common.AllomancyData;
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

        if (par3EntityPlayer.capabilities.isCreativeMode || (par3EntityPlayer.inventory.hasItem(Items.gold_nugget) && AllomancyData.forPlayer(par3EntityPlayer).MetalBurning[AllomancyData.matSteel]))
        {
            EntityGoldNugget entitygold = new EntityGoldNugget(par2World, par3EntityPlayer);
            par2World.spawnEntityInWorld(entitygold);
            
            if(!par3EntityPlayer.capabilities.isCreativeMode){
            par3EntityPlayer.inventory.consumeInventoryItem(Items.gold_nugget);
            }
        }

        return par1ItemStack;
    }


	   public int getItemEnchantability()
	    {
	        return 0;
	    }
}
