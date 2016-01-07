package common.legobmw99.allomancy.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Multimap;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMistcloak extends ItemArmor {

	public ItemMistcloak(ArmorMaterial par2EnumArmorMaterial, int par3, int par4) {
		super(par2EnumArmorMaterial, par3, par4);
	}

	@Override
	public Multimap getItemAttributeModifiers() {
		Multimap multimap = super.getItemAttributeModifiers();
		multimap.put(SharedMonsterAttributes.movementSpeed
				.getAttributeUnlocalizedName(), new AttributeModifier(
				SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(), .40, 2));
		return multimap;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			String type) {
		return "allomancy:textures/models/armor/mistcloak.png";
	}

}
