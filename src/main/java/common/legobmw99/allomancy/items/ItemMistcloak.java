package common.legobmw99.allomancy.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

import com.google.common.collect.Multimap;

public class ItemMistcloak extends ItemArmor{

	public ItemMistcloak(ArmorMaterial par2EnumArmorMaterial, int par3, EntityEquipmentSlot i) {
		super(par2EnumArmorMaterial, par3, i);
		this.setUnlocalizedName("mistcloak");
		this.setCreativeTab(CreativeTabs.COMBAT);
	}

	//TODO: fix this
	public Multimap getItemAttributeModifiers() {
		Multimap multimap = super.getItemAttributeModifiers(armorType.CHEST);
		multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getAttributeUnlocalizedName(), new AttributeModifier("Speed Modifier", .25, 1));
		return multimap;
	}


}