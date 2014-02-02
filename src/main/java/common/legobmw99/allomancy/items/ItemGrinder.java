package common.legobmw99.allomancy.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import common.legobmw99.allomancy.common.Registry;

public class ItemGrinder extends Item {

	public ItemGrinder() {
		super();
		// TODO Auto-generated constructor stub
		this.setUnlocalizedName("allomancy:Grinder");
		this.setCreativeTab(Registry.tabsAllomancy);
		this.setMaxDamage(31);
		this.maxStackSize = 1;
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}

	public ItemStack getContainerItemStack(ItemStack itemStack) {
		// TODO Auto-generated method stub
		return new ItemStack(Registry.itemAllomancyGrinder, 1,
				this.getDamage(itemStack) + 1);
	}

}
