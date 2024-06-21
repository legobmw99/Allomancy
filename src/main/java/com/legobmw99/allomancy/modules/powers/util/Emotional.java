package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.entity.ai.AIAttackOnCollideExtended;
import com.legobmw99.allomancy.modules.powers.entity.ai.AIEvilAttack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class Emotional {
    private static final Predicate<Goal> isAggroGoal =
            (goal) -> goal instanceof SwellGoal || goal instanceof AIAttackOnCollideExtended ||
                      goal instanceof MeleeAttackGoal || goal instanceof TargetGoal || goal instanceof PanicGoal ||
                      goal.getClass().getName().contains("Fireball") ||
                      goal.getClass().getName().contains("Attack") || goal.getClass().getName().contains("Anger");

    public static void riot(PathfinderMob target, Player allomancer, boolean enhanced) {
        try {
            if (!enhanced) {
                if (hasTinFoilHat(target)) {
                    return;
                }
                //Enable Targeting goals
                target.targetSelector.enableControlFlag(Goal.Flag.TARGET);
                //Add new goals
                target.setTarget(allomancer);
                target.setLastHurtByMob(allomancer);
                // TODO: try to use PrioritizedGoal::startExecuting for already hostiles
                target.targetSelector.addGoal(1, new AIAttackOnCollideExtended(target, 1d, false));
                target.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(target, Player.class, false));
                target.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(target, target.getClass(), false));
                target.goalSelector.addGoal(4, new RandomLookAroundGoal(target));
                target.targetSelector.addGoal(2, new HurtByTargetGoal(target).setAlertOthers());
                if (target.getAttribute(Attributes.ATTACK_DAMAGE) != null && !(target instanceof Guardian)) {
                    target.goalSelector.addGoal(3, new MeleeAttackGoal(target, 1.2D, true));
                }

                target.setAggressive(true);

                if (target instanceof Creeper creeper) {
                    target.goalSelector.addGoal(1, new SwellGoal(creeper));
                }
                if (target instanceof Rabbit rabbit) {
                    target.goalSelector.addGoal(1, new AIEvilAttack(rabbit));
                }
                if (target instanceof AbstractSkeleton skeleton) {
                    target.goalSelector.addGoal(1, new RangedBowAttackGoal<>(skeleton, 1.0D, 20, 15.0F));
                }
                if (target instanceof Illusioner illusioner) {
                    target.goalSelector.addGoal(1, new RangedBowAttackGoal<>(illusioner, 0.5D, 20, 15.0F));
                }
                if (target instanceof Pillager pillager) {
                    target.goalSelector.addGoal(2, new RangedCrossbowAttackGoal<>(pillager, 1.0D, 8.0F));
                }
            } else {
                target
                        .level()
                        .explode(target, target.position().x(), target.position().y(), target.position().z(), 1.2F,
                                 false, Level.ExplosionInteraction.MOB);
                target.kill();
            }
        } catch (Exception e) {
            Allomancy.LOGGER.error("Failed to riot entity " + target + "! Please report this error!", e);
        }
    }

    public static void soothe(PathfinderMob target, Player allomancer, boolean enhanced) {
        try {
            if (!enhanced) {
                if (hasTinFoilHat(target)) {
                    return;
                }

                if (target.isNoAi()) {
                    target.setNoAi(false);
                }
                // Reset all current aggro goals
                target.goalSelector
                        .getAvailableGoals()
                        .stream()
                        .filter(WrappedGoal::isRunning)
                        .filter(isAggroGoal)
                        .forEach(WrappedGoal::stop);
                target.targetSelector
                        .getAvailableGoals()
                        .stream()
                        .filter(WrappedGoal::isRunning)
                        .filter(isAggroGoal)
                        .forEach(WrappedGoal::stop);
                target.goalSelector.tick();
                target.targetSelector.tick();
                target.setTarget(null);
                target.setLastHurtByMob(null);
                //Disable targeting as a whole
                target.targetSelector.disableControlFlag(Goal.Flag.TARGET);
                target.setAggressive(false);
                //Add new goals
                target.goalSelector.addGoal(7, new LookAtPlayerGoal(target, Player.class, 6.0F));

                if (target instanceof TamableAnimal animal) {
                    if (Math.random() < 0.3) {
                        animal.tame(allomancer);
                    }
                }
                if (target instanceof AbstractHorse horse) {
                    if (Math.random() < 0.3) {
                        horse.tameWithName(allomancer);
                    }
                }
                if (target instanceof Sheep) {
                    target.goalSelector.addGoal(1, new EatBlockGoal(target));
                }
                if (target instanceof Villager villager) {
                    villager.onReputationEventFrom(ReputationEventType.TRADE, allomancer);
                }
                if (target instanceof WanderingTrader) {
                    target.goalSelector.addGoal(1, new TradeWithPlayerGoal((AbstractVillager) target));
                }
            } else { // Completely remove all AI if enhanced
                target.setNoAi(true);
            }

        } catch (Exception e) {
            Allomancy.LOGGER.error("Failed to soothe entity " + target + "! Please report this error!", e);
        }

    }

    public static boolean hasTinFoilHat(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == CombatSetup.ALUMINUM_HELMET.get();
    }
}
