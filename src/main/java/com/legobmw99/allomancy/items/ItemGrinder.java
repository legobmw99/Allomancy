package com.legobmw99.allomancy.items;

import com.legobmw99.allomancy.util.Registry;

import net.minecraft.item.Item;

public class ItemGrinder extends Item {

	public ItemGrinder() {
		super();
		this.setCreativeTab(Registry.tabsAllomancy);
		this.setMaxDamage(31);
		this.maxStackSize = 1;
		this.setUnlocalizedName("handgrinder");
	}

}