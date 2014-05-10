package common.legobmw99.allomancy.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.common.Registry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

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

	public ItemVial() {
		super(0, 0, false);
		this.setAlwaysEdible();

		// TODO Auto-generated constructor stub
		this.setHasSubtypes(true);
		this.setCreativeTab(Registry.tabsAllomancy);
	}

	@Override
	public IIcon getIconFromDamage(int meta) {
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
	public void registerIcons(IIconRegister iconRegister) {
		this.icons = new IIcon[textureName.length];

		for (int i = 0; i < textureName.length; ++i) {
			this.icons[i] = iconRegister.registerIcon("allomancy:"
					+ textureName[i]);
		}
	}

	// TODO: public void getSubItems(Item item, CreativeTabs creativeTabs, List
	// list)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int meta = 0; meta < this.icons.length; meta++) {
			list.add(new ItemStack(item, 1, meta));
		}
	}
}
