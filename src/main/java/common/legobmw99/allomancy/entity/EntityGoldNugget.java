package common.legobmw99.allomancy.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityGoldNugget extends EntityThrowable {

    public EntityGoldNugget(World par1World)
    {
        super(par1World);
    }

    public EntityGoldNugget(World par1World, EntityLivingBase par2EntityLivingBase)
    {
        super(par1World, par2EntityLivingBase);
    }

    public EntityGoldNugget(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }
    @Override
    protected float getVelocity()
    {
        return 4.5F;
    }
	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		// TODO Auto-generated method stub
		if (movingobjectposition.entityHit != null)
        {
            byte b0 = 0;

            if (movingobjectposition.entityHit instanceof EntityBlaze)
            {
                b0 = 3;
            }

            movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float) 4);
        }

        if (!this.worldObj.isRemote)
        {
        	ItemStack goldAmmo = new ItemStack(Items.gold_nugget, 1, 0);
        	if(this.worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops") && movingobjectposition.entityHit == null){
				this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, goldAmmo));
			}
            this.setDead();
        }
	}

    
 

}
