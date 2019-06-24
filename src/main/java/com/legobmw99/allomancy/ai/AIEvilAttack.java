package com.legobmw99.allomancy.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.RabbitEntity;

public class AIEvilAttack extends MeleeAttackGoal {
    public AIEvilAttack(RabbitEntity rabbit) {
        super(rabbit, 1.4D, true);
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return (double) (4.0F + attackTarget.getWidth());
    }
}