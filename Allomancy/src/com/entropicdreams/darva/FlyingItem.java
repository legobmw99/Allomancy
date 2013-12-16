package com.entropicdreams.darva;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class FlyingItem extends EntityThrowable {

	public Icon carriedIcon;
	private ItemStack stack;
	
	public FlyingItem(World par1World, double par2, double par4, double par6, EntityItem carried) {
		super(par1World, par2, par4, par6);
		// TODO Auto-generated constructor stub
		stack = carried.getEntityItem();
		carriedIcon = stack.getItem().getIconFromDamage(stack.getItemDamage());
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		// TODO Auto-generated method stub

	}

}
