package common.legobmw99.allomancy.items;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Multimap;
import common.legobmw99.allomancy.common.Registry;

public class ItemMistcloak extends ItemArmor{

	public ItemMistcloak(ArmorMaterial par2EnumArmorMaterial, int par3, int par4) {
		super(par2EnumArmorMaterial, par3, par4);
	}

	@Override
	public Multimap getItemAttributeModifiers() {
		Multimap multimap = super.getItemAttributeModifiers(/*new ItemStack(Registry.Mistcloak)*/);
		multimap.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Speed Modifier", .25, 1));
		return multimap;
	}

	public String getArmorTextureFile(ItemStack stack) {
		return "allomancy:textures/models/armor/mistcloak.png";
	}

}
