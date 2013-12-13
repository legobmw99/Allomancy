package com.entropicdreams.darva.items;



import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemVial extends Item {
	
	public static String[] unlocalName = {
		"empty",
		"copperdrink",
		"irondrink",
		"tindrink",
		"zincdrink",
		"brassdrink",
		"steeldrink",
		"pewterdrink",
		"bronzedrink"
	};
	
	public static String[] localName = {
		"Empty Vial",
		"Copper Elixer",
		"Iron Elixer",
		"Tin Elixer",
		"Zinc Elixer",
		"Brass Elixer",
		"Steel Elixer",
		"Pewter Elixer",
		"Bronze Elixer",
	};
	public static String[] textureName = {
		"emptyvial",
		"copperelixer",
		"ironelixer",
		"tinelixer",
		"zincelixer",
		"brasselixer",
		"steelelixer",
		"pewterelixer",
		"bronzeelixer",
	};

	@SideOnly(Side.CLIENT)
	private Icon[] icons;
	public ItemVial(int par1) {
		super(par1);
		// TODO Auto-generated constructor stub
		setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	@Override
	public Icon getIconFromDamage(int par1) {
		// TODO Auto-generated method stub
		return icons[par1];
	}
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		// TODO Auto-generated method stub
		return unlocalName[par1ItemStack.getItemDamage()];
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister IconRegister) {
		super.registerIcons(IconRegister);
		icons = new Icon[textureName.length];
		for (int x = 0; x<textureName.length; x++)
		{
			icons[x] = IconRegister.registerIcon("allomancy:"+textureName[x]);
		}
	}
	@Override
	public void getSubItems(int id, CreativeTabs tab, List list) {
	for(int i = 0; i < icons.length; i++) {
		ItemStack itemstack = new ItemStack(id, 1, i);
		list.add(itemstack);
	}
	}
}
