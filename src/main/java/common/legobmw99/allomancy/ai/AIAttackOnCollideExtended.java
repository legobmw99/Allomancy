package common.legobmw99.allomancy.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AIAttackOnCollideExtended extends EntityAIBase {
	World worldObj;
	EntityCreature attacker;

	/**
	 * An amount of decrementing ticks that allows the entity to attack once the
	 * tick reaches 0.
	 */
	int attackTick;

	/** The speed with which the mob will approach the target */
	double speedTowardsTarget;

	/**
	 * When true, the mob will continue chasing its target, even if it can't
	 * find a path to them right now.
	 */
	boolean longMemory;

	/** The PathEntity of our entity. */
	PathEntity entityPathEntity;
	Class classTarget;
	private int field_75445_i;

	private int failedPathFindingPenalty;

	public AIAttackOnCollideExtended(EntityCreature par1EntityCreature,
			Class par2Class, double par3, boolean par5) {
		this(par1EntityCreature, par3, par5);
		this.classTarget = par2Class;
	}

	public AIAttackOnCollideExtended(EntityCreature par1EntityCreature,
			double par2, boolean par4) {
		this.attacker = par1EntityCreature;
		this.worldObj = par1EntityCreature.worldObj;
		this.speedTowardsTarget = par2;
		this.longMemory = par4;
		this.setMutexBits(3);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

		if (entitylivingbase == null) {
			return false;
		} else if (!entitylivingbase.isEntityAlive()) {
			return false;
		} else if ((this.classTarget != null)
				&& !this.classTarget.isAssignableFrom(entitylivingbase
						.getClass())) {
			return false;
		} else {
			if (--this.field_75445_i <= 0) {
				this.entityPathEntity = this.attacker.getNavigator()
						.getPathToEntityLiving(entitylivingbase);
				this.field_75445_i = 4 + this.attacker.getRNG().nextInt(7);
				return this.entityPathEntity != null;
			} else {
				return true;
			}
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean continueExecuting() {
		
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
		if (entitylivingbase == null) {
			return false;
		}
		BlockPos pos1 = new BlockPos(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ);

		return (entitylivingbase == null) ? false : (!entitylivingbase
				.isEntityAlive() ? false : (!this.longMemory ? !this.attacker
				.getNavigator().noPath() : this.attacker.isWithinHomeDistanceFromPosition(pos1)));
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.attacker.getNavigator().setPath(this.entityPathEntity,
				this.speedTowardsTarget);
		this.field_75445_i = 0;
	}

	/**
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		this.attacker.getNavigator().clearPathEntity();
	}

	/**
	 * Updates the task
	 */
	@Override
	public void updateTask() {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
		if (entitylivingbase == null) {
			return;
		}
		this.attacker.getLookHelper().setLookPositionWithEntity(
				entitylivingbase, 30.0F, 30.0F);

		if ((this.longMemory || this.attacker.getEntitySenses().canSee(
				entitylivingbase))
				&& (--this.field_75445_i <= 0)) {
			this.field_75445_i = this.failedPathFindingPenalty + 4
					+ this.attacker.getRNG().nextInt(7);
			this.attacker.getNavigator().tryMoveToEntityLiving(
					entitylivingbase, this.speedTowardsTarget);
			if (this.attacker.getNavigator().getPath() != null) {
				PathPoint finalPathPoint = this.attacker.getNavigator()
						.getPath().getFinalPathPoint();
				if ((finalPathPoint != null)
						&& (entitylivingbase.getDistanceSq(
								finalPathPoint.xCoord, finalPathPoint.yCoord,
								finalPathPoint.zCoord) < 1)) {
					this.failedPathFindingPenalty = 0;
				} else {
					this.failedPathFindingPenalty += 10;
				}
			} else {
				this.failedPathFindingPenalty += 10;
			}
		}

		this.attackTick = Math.max(this.attackTick - 1, 0);
		double d0 = (this.attacker.width * 2.0F * this.attacker.width * 2.0F)
				+ entitylivingbase.width;

		if (this.attacker.getDistanceSq(entitylivingbase.posX,entitylivingbase.posY, entitylivingbase.posZ) <= d0) {
			if (this.attackTick <= 0) {
				this.attackTick = 20;

				if (this.attacker.getHeldItemMainhand() != null) {
					this.attacker.swingArm(EnumHand.MAIN_HAND);
				}

				if (this.attacker instanceof EntityAnimal) {
					entitylivingbase.attackEntityFrom(
							DamageSource.causeMobDamage(this.attacker), 3);
				} else {
					this.attacker.attackEntityAsMob(entitylivingbase);
				}
			}
		}
	}
}
