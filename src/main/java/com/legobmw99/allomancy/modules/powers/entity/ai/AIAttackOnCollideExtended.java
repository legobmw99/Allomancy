package com.legobmw99.allomancy.modules.powers.entity.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class AIAttackOnCollideExtended extends Goal {
    final Level worldObj;
    final PathfinderMob attacker;
    /**
     * The speed with which the mob will approach the target
     */
    final double speedTowardsTarget;
    /**
     * When true, the mob will continue chasing its target, even if it can't
     * find a path to them right now.
     */
    final boolean longMemory;
    /**
     * An amount of decrementing ticks that allows the entity to attack once the
     * tick reaches 0.
     */
    int attackTick;
    /**
     * The PathEntity of our entity.
     */
    Path entityPathEntity;
    Class<?> classTarget;
    private int ticksUntilNextPathRecalculation;

    private int failedPathFindingPenalty;

    public AIAttackOnCollideExtended(PathfinderMob par1EntityCreature, Class par2Class, double par3, boolean par5) {
        this(par1EntityCreature, par3, par5);
        this.classTarget = par2Class;
    }

    public AIAttackOnCollideExtended(PathfinderMob par1EntityCreature, double par2, boolean par4) {
        this.attacker = par1EntityCreature;
        this.worldObj = par1EntityCreature.level();
        this.speedTowardsTarget = par2;
        this.longMemory = par4;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.attacker.getTarget();

        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if ((this.classTarget != null) && !this.classTarget.isAssignableFrom(livingEntity.getClass())) {
            return false;
        } else {
            if (--this.ticksUntilNextPathRecalculation <= 0) {
                this.entityPathEntity = this.attacker.getNavigation().createPath(livingEntity, 0);
                this.ticksUntilNextPathRecalculation = 4 + this.attacker.getRandom().nextInt(7);
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
    public boolean canContinueToUse() {

        LivingEntity livingEntity = this.attacker.getTarget();
        if (livingEntity == null) {
            return false;
        }

        return (livingEntity.isAlive() && (!this.longMemory ? !this.attacker.getNavigation().isDone() : this.attacker.isWithinRestriction(livingEntity.blockPosition())));
    }

    /**
     * Execute a one shot goal or start executing a continuous goal
     */
    @Override
    public void start() {
        this.attacker.getNavigation().moveTo(this.entityPathEntity, this.speedTowardsTarget);
        this.ticksUntilNextPathRecalculation = 0;
    }

    /**
     * Resets the goal
     */
    @Override
    public void stop() {
        this.attacker.getNavigation().stop();
    }

    /**
     * Ticks the goal
     */
    @Override
    public void tick() {
        LivingEntity livingEntity = this.attacker.getTarget();
        if (livingEntity == null) {
            return;
        }
        this.attacker.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);

        if ((this.longMemory || this.attacker.getSensing().hasLineOfSight(livingEntity)) && (--this.ticksUntilNextPathRecalculation <= 0)) {
            this.ticksUntilNextPathRecalculation = this.failedPathFindingPenalty + 4 + this.attacker.getRandom().nextInt(7);
            this.attacker.getNavigation().moveTo(livingEntity, this.speedTowardsTarget);
            if (this.attacker.getNavigation().getPath() != null) {
                Node finalPathPoint = this.attacker.getNavigation().getPath().getEndNode();
                if ((finalPathPoint != null) && (livingEntity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)) {
                    this.failedPathFindingPenalty = 0;
                } else {
                    this.failedPathFindingPenalty += 10;
                }
            } else {
                this.failedPathFindingPenalty += 10;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        double d0 = (this.attacker.getBbWidth() * 2.0F * this.attacker.getBbWidth() * 2.0F) + livingEntity.getBbWidth();

        if (this.attacker.distanceToSqr(livingEntity) <= d0) {
            if (this.attackTick <= 0) {
                this.attackTick = 20;

                if (!this.attacker.getMainHandItem().isEmpty()) {
                    this.attacker.swing(InteractionHand.MAIN_HAND);
                }

                if (this.attacker instanceof Monster) {
                    this.attacker.doHurtTarget(livingEntity);
                } else {
                    livingEntity.hurt(livingEntity.level().damageSources().mobAttack(this.attacker), 3);
                }
            }
        }
    }
}
