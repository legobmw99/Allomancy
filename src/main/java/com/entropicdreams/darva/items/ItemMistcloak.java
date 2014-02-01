package com.entropicdreams.darva.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMistcloak extends ItemArmor {

	public ItemMistcloak(int par1, EnumArmorMaterial par2EnumArmorMaterial,
			int par3, int par4) {
		super(par1, par2EnumArmorMaterial, par3, par4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1iconregister) {
		this.itemIcon = par1iconregister.registerIcon("allomancy:mistcloak");
	}

	@Override
	public Multimap getItemAttributeModifiers() {
		Multimap multimap = super.getItemAttributeModifiers();
		multimap.put(SharedMonsterAttributes.movementSpeed
				.getAttributeUnlocalizedName(), new AttributeModifier(
				field_111210_e, "Speed modifier", .40, 2));
		return multimap;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			String type) {
		return "allomancy:textures/models/armor/mistcloak.png";
	}
}
