package com.legobmw99.allomancy.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabAllomancy extends CreativeTabs {
	public CreativeTabAllomancy(int id, String mod_id) {
		super(id, mod_id);
	}

	@Override
	public String getTabLabel() {
		return "Allomancy";
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Registry.vial, 1);
	}

	@Override
	public ItemStack getTabIconItem() {
		return null;
	}
}
