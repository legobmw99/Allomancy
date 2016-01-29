package common.legobmw99.allomancy.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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
		return new ItemStack(Registry.itemVial, 1, 5);
	}

	@Override
	public Item getTabIconItem() {
		return null;
	}
}
