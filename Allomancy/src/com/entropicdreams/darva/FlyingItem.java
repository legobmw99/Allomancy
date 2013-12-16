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
		carriedIcon = stack.getItem().getIconFromDamage(0);
		this.setPosition(carried.posX, carried.posY, carried.posZ);
	}



	@Override
	protected float getGravityVelocity() {
		// TODO Auto-generated method stub
		return super.getGravityVelocity();
		
	}


	@Override
	protected void onImpact(MovingObjectPosition mop) {
		// TODO Auto-generated method stub

		//this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, mop.blockX,mop.blockY,mop.blockZ));
		this.setDead();
	}

}
