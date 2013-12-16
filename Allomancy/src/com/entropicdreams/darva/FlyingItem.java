package com.entropicdreams.darva;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class FlyingItem extends EntityThrowable {

	public Icon carriedIcon;
	private ItemStack stack;
	
	public FlyingItem(World par1World)
    {
        super(par1World);
    }

	
	public FlyingItem(World par1World, EntityLivingBase par2EntityLivingBase, EntityItem carried) {
		super(par1World, par2EntityLivingBase);
		// TODO Auto-generated constructor stub
		stack = carried.getEntityItem();
		carriedIcon = stack.getItem().getIconFromDamage(stack.getItemDamage());
		if (carriedIcon == null)
		{
			System.out.println("WTF??");
		}
	}



	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		// TODO Auto-generated method stub

	}

}
