package com.legobmw99.allomancy.modules.powers.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class AIAttackOnCollideExtended extends Goal {
    World worldObj;
    CreatureEntity attacker;

    /**
     * An amount of decrementing ticks that allows the entity to attack once the
     * tick reaches 0.
     */
    int attackTick;

    /**
     * The speed with which the mob will approach the target
     */
    double speedTowardsTarget;

    /**
     * When true, the mob will continue chasing its target, even if it can't
     * find a path to them right now.
     */
    boolean longMemory;

    /**
     * The PathEntity of our entity.
     */
    Path entityPathEntity;
    Class classTarget;
    private int field_75445_i;

    private int failedPathFindingPenalty;

    public AIAttackOnCollideExtended(CreatureEntity par1EntityCreature,
                                     Class par2Class, double par3, boolean par5) {
        this(par1EntityCreature, par3, par5);
        this.classTarget = par2Class;
    }

    public AIAttackOnCollideExtended(CreatureEntity par1EntityCreature,
                                     double par2, boolean par4) {
        this.attacker = par1EntityCreature;
        this.worldObj = par1EntityCreature.world;
        this.speedTowardsTarget = par2;
        this.longMemory = par4;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        LivingEntity livingEntity = this.attacker.getAttackTarget();

        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if ((this.classTarget != null)
                && !this.classTarget.isAssignableFrom(livingEntity
                .getClass())) {
            return false;
        } else {
            if (--this.field_75445_i <= 0) {
                this.entityPathEntity = this.attacker.getNavigator().getPathToEntity(livingEntity, 0);
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
    public boolean shouldContinueExecuting() {

        LivingEntity livingEntity = this.attacker.getAttackTarget();
        if (livingEntity == null) {
            return false;
        }
        BlockPos pos1 = new BlockPos(livingEntity);

        return (livingEntity
                .isAlive() && (!this.longMemory ? !this.attacker
                .getNavigator().noPath() : this.attacker.isWithinHomeDistanceFromPosition(pos1)));
    }

    /**
     * Execute a one shot goal or start executing a continuous goal
     */
    @Override
    public void startExecuting() {
        this.attacker.getNavigator().setPath(this.entityPathEntity,
                this.speedTowardsTarget);
        this.field_75445_i = 0;
    }

    /**
     * Resets the goal
     */
    @Override
    public void resetTask() {
        this.attacker.getNavigator().clearPath();
    }

    /**
     * Ticks the goal
     */
    @Override
    public void tick() {
        LivingEntity livingEntity = this.attacker.getAttackTarget();
        if (livingEntity == null) {
            return;
        }
        this.attacker.getLookController().setLookPositionWithEntity(
                livingEntity, 30.0F, 30.0F);

        if ((this.longMemory || this.attacker.getEntitySenses().canSee(
                livingEntity))
                && (--this.field_75445_i <= 0)) {
            this.field_75445_i = this.failedPathFindingPenalty + 4
                    + this.attacker.getRNG().nextInt(7);
            this.attacker.getNavigator().tryMoveToEntityLiving(
                    livingEntity, this.speedTowardsTarget);
            if (this.attacker.getNavigator().getPath() != null) {
                PathPoint finalPathPoint = this.attacker.getNavigator()
                        .getPath().getFinalPathPoint();
                if ((finalPathPoint != null)
                        && (livingEntity.getDistanceSq(
                        finalPathPoint.x, finalPathPoint.y,
                        finalPathPoint.z) < 1)) {
                    this.failedPathFindingPenalty = 0;
                } else {
                    this.failedPathFindingPenalty += 10;
                }
            } else {
                this.failedPathFindingPenalty += 10;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        double d0 = (this.attacker.getWidth() * 2.0F * this.attacker.getWidth() * 2.0F)
                + livingEntity.getWidth();

        if (this.attacker.getDistanceSq(livingEntity) <= d0) {
            if (this.attackTick <= 0) {
                this.attackTick = 20;

                if (!this.attacker.getHeldItemMainhand().isEmpty()) {
                    this.attacker.swingArm(Hand.MAIN_HAND);
                }

                if (this.attacker instanceof MonsterEntity) {
                    this.attacker.attackEntityAsMob(livingEntity);
                } else {
                    livingEntity.attackEntityFrom(
                            DamageSource.causeMobDamage(this.attacker), 3);
                }
            }
        }
    }
}
