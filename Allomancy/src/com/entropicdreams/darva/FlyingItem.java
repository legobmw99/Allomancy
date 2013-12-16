package com.entropicdreams.darva;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class FlyingItem extends EntityThrowable {

	public Icon carriedIcon;
	private ItemStack stack;
	private EntityPlayer thrower;
	
	public FlyingItem(World par1World)
    {
        super(par1World);
    }
	
	
	public FlyingItem(World par1World, EntityLivingBase par2EntityLivingBase, EntityItem carried) {
		super(par1World, par2EntityLivingBase);
		// TODO Auto-generated constructor stub
		stack = carried.getEntityItem().copy();
		carriedIcon = stack.getItem().getIconFromDamage(0);
		this.setPosition(carried.posX, carried.posY, carried.posZ);
		thrower = (EntityPlayer) par2EntityLivingBase;
	}



	@Override
	protected float getGravityVelocity() {
		// TODO Auto-generated method stub
		return super.getGravityVelocity();
		
	}


	@Override
	protected void onImpact(MovingObjectPosition mop) {
		// TODO Auto-generated method stub

		if (!this.worldObj.isRemote)
		{
			if (mop.entityHit == null)
			{
				this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, mop.blockX,mop.blockY,mop.blockZ, this.stack));
			}
			else
			{
				if (this.thrower.equals(mop.entityHit))
				{
					//Player hit themselves, using iron pulling most likely.  give them the item.
					thrower.inventory.addItemStackToInventory(stack);
				}
				mop.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(thrower, mop.entityHit), 7);
			}
		}
	this.setDead();
	}
}
