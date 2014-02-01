package com.entropicdreams.darva.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.common.Registry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemVial extends ItemFood {
	public static String[] localName = { "Empty Vial", "Iron Elixer",
			"Steel Elixer", "Tin Elixer", "Pewter Elixer", "Copper Elixer",
			"Bronze Elixer", "Zinc Elixer", "Brass Elixer", };
	public static String[] textureName = { "emptyvial", "ironelixer",
			"steelelixer", "tinelixer", "pewterelixer", "copperelixer",
			"bronzeelixer", "zincelixer", "brasselixer", };
	public static String[] unlocalName = { "empty", "irondrink", "steeldrink",
			"tindrink", "pewterdrink", "zincdrink", "brassdrink",
			"copperdrink", "bronzedrink" };

	@SideOnly(Side.CLIENT)
	private Icon[] icons;

	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		// TODO Auto-generated method stub
		AllomancyData data;
		data = AllomancyData.forPlayer(par3EntityPlayer);
		if (par3EntityPlayer.capabilities.isCreativeMode) {
			++par1ItemStack.stackSize;
		}
		if (data == null) {
			return par1ItemStack;
		}

		if (par1ItemStack.getItemDamage() == 0) {
			return par1ItemStack;
		}

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
		return 12;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.setItemInUse(par1ItemStack,
				this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	public ItemVial(int par1) {
		super(par1, 0, false);
		this.setAlwaysEdible();
		this.setHasSubtypes(true);
		this.setCreativeTab(Registry.tabsAllomancy);
	}

	@Override
	public Icon getIconFromDamage(int meta) {
		// TODO Auto-generated method stub
		if ((meta < 0) || (meta >= this.icons.length)) {
			meta = 0;
		}
		return this.icons[meta];
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		// TODO Auto-generated method stub
		int meta = itemStack.getItemDamage();
		if ((meta < 0) || (meta >= unlocalName.length)) {
			meta = 0;
		}

		return super.getUnlocalizedName() + "." + unlocalName[meta];
	}

	@Override
	public void registerIcons(IconRegister IconRegister) {
		this.icons = new Icon[textureName.length];

		for (int i = 0; i < textureName.length; ++i) {
			this.icons[i] = IconRegister.registerIcon("allomancy:"
					+ textureName[i]);
		}
	}

	@Override
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (int i = 0; i < this.icons.length; i++) {
			list.add(new ItemStack(id, 1, i));
		}
	}
}
