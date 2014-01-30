package common.legobmw99.allomancy.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	//TODO: public ItemStack getIconItemStack()
	public ItemStack func_151244_d() 
	{
		return new ItemStack(Registry.itemVial, 1, 5);
	}
	@Override
	public Item getTabIconItem() {
		// TODO Auto-generated method stub
		return null;
	}
}
