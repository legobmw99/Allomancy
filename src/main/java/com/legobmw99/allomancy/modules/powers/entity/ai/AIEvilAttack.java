package com.legobmw99.allomancy.modules.powers.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.Rabbit;

public class AIEvilAttack extends MeleeAttackGoal {
    public AIEvilAttack(Rabbit rabbit) {
        super(rabbit, 1.4D, true);
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return 4.0F + attackTarget.getBbWidth();
    }
}