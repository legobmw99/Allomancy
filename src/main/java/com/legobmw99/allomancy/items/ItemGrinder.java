package com.legobmw99.allomancy.items;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.Registry;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemGrinder extends Item {

	public ItemGrinder() {
		super();
		this.setCreativeTab(Registry.tabsAllomancy);
		this.maxStackSize = 1;
		this.setUnlocalizedName("handgrinder");
		this.setRegistryName(new ResourceLocation(Allomancy.MODID, "grinder"));
		this.setContainerItem(this);
	}
}