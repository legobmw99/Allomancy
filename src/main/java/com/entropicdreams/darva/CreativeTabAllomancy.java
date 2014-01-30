package com.entropicdreams.darva;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import com.entropicdreams.darva.common.ModRegistry;

public class CreativeTabAllomancy extends CreativeTabs
{
    public CreativeTabAllomancy(int id, String mod_id)
    {
        super(id, mod_id);
    }
    @Override
    public String getTabLabel()
    {
    	return "Allomancy";
    }
	@Override
	public ItemStack getIconItemStack()
	{
		return new ItemStack(ModRegistry.itemVial, 1, 5);
	}
    @Override
    public int getTabIconItemIndex()
    {
        return 0;
    }
}
