package common.legobmw99.allomancy.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.common.Registry;

public class ItemVial extends ItemFood {
	public static String[] localName = { "Empty Vial", "Iron Elixer",
			"Steel Elixer", "Tin Elixer", "Pewter Elixer", "Zinc Elixer",
			"Bronze Elixer", "Copper Elixer", "Brass Elixer", };

	public static String[] unlocalName = { "emptyvial", "ironelixer",
		"steelelixer", "tinelixer", "pewterelixer", "zincelixer",
		"bronzeelixer", "copperelixer", "brasselixer", };

	public ItemStack onItemUseFinish(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
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

		return super.onItemUseFinish(par1ItemStack, par2World, par3EntityPlayer);
	}

	public int getHealAmount() {
		return 0;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.DRINK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 8;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		AllomancyData data;
		data = AllomancyData.forPlayer(par3EntityPlayer);
		if (data.MetalAmounts[par1ItemStack.getItemDamage() - 1] < 10
				&& par1ItemStack.getItemDamage() > 0) {
			par3EntityPlayer.setItemInUse(par1ItemStack,
					this.getMaxItemUseDuration(par1ItemStack));
		}
		return par1ItemStack;
	}

	public ItemVial() {
		super(0, 0, false);
		this.setAlwaysEdible();
		this.setHasSubtypes(true);
		this.setCreativeTab(Registry.tabsAllomancy);
	}


	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int meta = itemStack.getItemDamage();
		if ((meta < 0) || (meta >= unlocalName.length)) {
			meta = 0;
		}

		return "item.itemVial" + "." + unlocalName[meta];
	}

	

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int meta = 0; meta < 9; meta++) {
			list.add(new ItemStack(item, 1, meta));
		}
	}
}
