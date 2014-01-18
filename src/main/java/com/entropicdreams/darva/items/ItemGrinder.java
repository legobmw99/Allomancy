package com.entropicdreams.darva.items;

import com.entropicdreams.darva.ModMain;
import com.entropicdreams.darva.common.ModRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemGrinder extends Item {

	public ItemGrinder(int par1) {
		super(par1);
		// TODO Auto-generated constructor stub
		setUnlocalizedName("allomancy:Grinder");
		setCreativeTab(CreativeTabs.tabMisc);
		setMaxDamage(31);
		this.maxStackSize = 1;
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasContainerItem() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ItemStack getContainerItemStack(ItemStack itemStack) {
		// TODO Auto-generated method stub
		return new ItemStack(ModRegistry.itemGrinder, 1,
				this.getDamage(itemStack) + 1);
	}

}
