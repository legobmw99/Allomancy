package com.entropicdreams.darva.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.entropicdreams.darva.common.ModRegistry;

import cpw.mods.fml.common.ICraftingHandler;

public class CraftingHandler implements ICraftingHandler {

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item,
			IInventory craftMatrix) {
		ItemStack cur;
		for (int x = 0; x < craftMatrix.getSizeInventory(); x++) {
			cur = craftMatrix.getStackInSlot(x);
			if (cur == null)
				continue;
			if (cur.itemID == ModRegistry.itemGrinder.itemID) {
				cur.damageItem(1, player);
			}

		}

	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {
		// TODO Auto-generated method stub

	}

}
