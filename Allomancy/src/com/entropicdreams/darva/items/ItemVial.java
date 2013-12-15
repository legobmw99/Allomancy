package com.entropicdreams.darva.items;



import java.util.List;

import com.entropicdreams.darva.AllomancyData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemVial extends ItemFood {
	public static String[] localName = {
		"Empty Vial",
		"Iron Elixer",
		"Steel Elixer",
		"Tin Elixer",
		"Pewter Elixer",
		"Zinc Elixer",
		"Bronze Elixer",
		"Copper Elixer",
		"Brass Elixer",
	};
	public static String[] textureName = {
		"emptyvial",
		"ironelixer",
		"steelelixer",
		"tinelixer",
		"pewterelixer",
		"zincelixer",
		"bronzeelixer",
		"copperelixer",
		"brasseelixer",
	};
	
	public static String[] unlocalName = {
		"empty",
		"irondrink",
		"steeldrink",
		"tindrink",
		"pewterdrink",
		"zincdrink",
		"bronzedrink",
		"copperdrink",
		"brassedrink"
	};
	
	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		// TODO Auto-generated method stub
		AllomancyData data;
		data = AllomancyData.forPlayer(par3EntityPlayer);
		if (data == null)
		{
			return par1ItemStack;
		}
		
		if (par1ItemStack.getItemDamage() == 0)
			return par1ItemStack;
		
		if (data.MetalAmounts[par1ItemStack.getItemDamage()-1] <10)
		{
			data.MetalAmounts[par1ItemStack.getItemDamage() -1 ]++;
			System.out.println(data.MetalAmounts[par1ItemStack.getItemDamage()-1]);
		}
		
		return super.onEaten(par1ItemStack, par2World, par3EntityPlayer);
	}
	@Override
	public int getHealAmount() {
		return 0;
	}
	@Override
	public boolean onItemUse(ItemStack ItemStack,
			EntityPlayer par2EntityPlayer, World par3World, int par4, int par5,
			int par6, int par7, float par8, float par9, float par10) {
		// TODO Auto-generated method stub

		
		
		return super.onItemUse(ItemStack, par2EntityPlayer, par3World, par4, par5,
				par6, par7, par8, par9, par10);
	}

	@SideOnly(Side.CLIENT)
	private Icon[] icons;
	public ItemVial(int par1) {
		super(par1,0, false);
		this.setAlwaysEdible();
		 
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