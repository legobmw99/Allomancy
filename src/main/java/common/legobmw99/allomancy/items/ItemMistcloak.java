package common.legobmw99.allomancy.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Multimap;

import common.legobmw99.allomancy.common.Registry;

public class ItemMistcloak extends ItemArmor{

	public ItemMistcloak(ArmorMaterial par2EnumArmorMaterial, int par3, EntityEquipmentSlot i) {
		super(par2EnumArmorMaterial, par3, i);
		this.setUnlocalizedName("mistcloak");
		this.setCreativeTab(CreativeTabs.COMBAT);
	}

	@Override
	public Multimap getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap multimap = super.getAttributeModifiers(slot,  new  ItemStack(Registry.Mistcloak));
		if(slot  == armorType.CHEST)
			multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getAttributeUnlocalizedName(), new AttributeModifier("Speed Modifier", .25, 1));
		return multimap;
	}


}