package com.entropicdreams.darva.items;

import java.util.List;

import com.entropicdreams.darva.AllomancyData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemVial extends ItemFood {
	public static String[] localName = { "Empty Vial", "Iron Elixer",
			"Steel Elixer", "Tin Elixer", "Pewter Elixer", "Zinc Elixer",
			"Bronze Elixer", "Copper Elixer", "Brass Elixer", };
	public static String[] textureName = { "emptyvial", "ironelixer",
			"steelelixer", "tinelixer", "pewterelixer", "zincelixer",
			"bronzeelixer", "copperelixer", "brasselixer", };

	public static String[] unlocalName = { "empty", "irondrink", "steeldrink",
			"tindrink", "pewterdrink", "zincdrink", "bronzedrink",
			"copperdrink", "brassdrink" };

	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		// TODO Auto-generated method stub
		AllomancyData data;
		data = AllomancyData.forPlayer(par3EntityPlayer);
		if (!par3EntityPlayer.capabilities.isCreativeMode) {
			--par1ItemStack.stackSize;
		}
		if (data == null) {
			return par1ItemStack;
		}

		if (par1ItemStack.getItemDamage() == 0)
			return par1ItemStack;

		if (data.MetalAmounts[par1ItemStack.getItemDamage() - 1] < 10) {
			data.MetalAmounts[par1ItemStack.getItemDamage() - 1]++;
		}

		return super.onEaten(par1ItemStack, par2World, par3EntityPlayer);
	}

	@Override
	public int getHealAmount() {
		return 0;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.drink;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 8;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.setItemInUse(par1ItemStack,
				this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	@SideOnly(Side.CLIENT)
	private Icon[] icons;

	public ItemVial(int par1) {
		super(par1, 0, false);
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
		for (int x = 0; x < textureName.length; x++) {
			icons[x] = IconRegister.registerIcon("allomancy:" + textureName[x]);
		}
	}

	@Override
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (int i = 0; i < icons.length; i++) {
			ItemStack itemstack = new ItemStack(id, 1, i);
			list.add(itemstack);
		}
	}
}
