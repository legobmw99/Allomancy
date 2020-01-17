package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.entity.GoldNuggetEntity;
import com.legobmw99.allomancy.modules.combat.entity.IronNuggetEntity;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.entity.ai.AIAttackOnCollideExtended;
import com.legobmw99.allomancy.modules.powers.entity.ai.AIEvilAttack;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.function.Predicate;

/**
 * Contains all static, common methods in one place
 */

public class PowerUtils {


    public static final byte PUSH = 1;
    public static final byte PULL = -1;


    /**
     * Block state wrapper on {@link PowerUtils#isBlockMetal}
     *
     * @param state BlockState to check
     * @return whether or not the block state is metal
     */
    public static boolean isBlockStateMetal(BlockState state) {
        return isBlockMetal(state.getBlock());
    }

    /**
     * Determines if a block is metal or not
     *
     * @param block to be checked
     * @return Whether or not the block is metal
     */
    public static boolean isBlockMetal(Block block) {
        return isOnWhitelist(block.getRegistryName().toString());
    }

    /**
     * Determines if an item is metal or not
     *
     * @param item to be checked
     * @return Whether or not the item is metal
     */
    public static boolean isItemMetal(ItemStack item) {
        return isOnWhitelist(item.getItem().getRegistryName().toString());
    }


    private static boolean isOnWhitelist(String string) {
        return PowersConfig.whitelist.contains(string);
    }

    /**
     * Determines if an entity is metal or not
     *
     * @param entity to be checked
     * @return Whether or not the entity is metallic
     */
    public static boolean isEntityMetal(Entity entity) {
        if (entity == null) {
            return false;
        }

        if (entity instanceof ItemEntity) {
            return isItemMetal(((ItemEntity) entity).getItem());
        }
        if (entity instanceof ItemFrameEntity) {
            return isItemMetal(((ItemFrameEntity) entity).getDisplayedItem());
        }

        if (entity instanceof FallingBlockEntity) {
            return isBlockStateMetal(((FallingBlockEntity) entity).getBlockState());
        }
        if (entity instanceof IronNuggetEntity || entity instanceof GoldNuggetEntity) {
            return true;
        }
        if (entity instanceof AbstractMinecartEntity) {
            return true;
        }
        if (entity instanceof MobEntity) {
            MobEntity ent = (MobEntity) entity;
            if (ent instanceof IronGolemEntity) {
                return true;
            }
            if (isItemMetal(ent.getHeldItem(Hand.MAIN_HAND)) || isItemMetal(ent.getHeldItem(Hand.OFF_HAND))) {
                return true;
            }
            for (ItemStack i : ent.getArmorInventoryList()) {
                if (isItemMetal(i)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void wipePlayer(PlayerEntity player) {
        AllomancyCapability capHurt = AllomancyCapability.forPlayer(player);
        capHurt.drainMetals(Metal.values());
        player.clearActivePotions();

        if (player instanceof ServerPlayerEntity)
            Network.sync(capHurt, player);
    }

    /**
     * Move an entity either toward or away from an anchor point
     *
     * @param directionScalar the direction and (possibly) scalar multiple of the magnitude
     * @param toMove          the entity to move
     * @param block           the point being moved toward or away from
     */
    public static void move(double directionScalar, Entity toMove, BlockPos block) {

        double motionX, motionY, motionZ, magnitude;
        if (toMove.isPassenger()) {
            toMove = toMove.getRidingEntity();
        }
        Vec3d vec = toMove.getPositionVec();
        double posX = vec.getX(), posY = vec.getY(), posZ = vec.getZ();
        // Calculate the length of the vector between the entity and anchor
        magnitude = Math.sqrt(Math.pow((posX - (double) (block.getX() + .5)), 2)
                + Math.pow((posY - (double) (block.getY() + .5)), 2)
                + Math.pow((posZ - (double) (block.getZ() + .5)), 2));
        // Get a unit(-ish) vector in the direction of motion
        motionX = ((posX - (double) (block.getX() + .5)) * directionScalar * (1.1) / magnitude);
        motionY = ((posY - (double) (block.getY() + .5)) * directionScalar * (1.1) / magnitude);
        motionZ = ((posZ - (double) (block.getZ() + .5)) * directionScalar * (1.1) / magnitude);
        // Move along that vector, additively increasing motion until you max
        // out at the above values
        double x = toMove.getMotion().getX(), y = toMove.getMotion().getY(), z = toMove.getMotion().getZ();
        toMove.setMotion(Math.abs(x + motionX) > 0.01
                ? MathHelper.clamp(x + motionX, -Math.abs(motionX), motionX) : 0, Math.abs(y + motionY) > 0.01
                ? MathHelper.clamp(y + motionY, -Math.abs(motionY), motionY) : 0, Math.abs(z + motionZ) > 0.01
                ? MathHelper.clamp(z + motionZ, -Math.abs(motionZ), motionZ) : 0);

        toMove.velocityChanged = true;

        // Only save players from fall damage
        if (toMove instanceof ServerPlayerEntity && Math.abs(directionScalar) <= 1) {
            toMove.fallDistance = 0;
        }
    }

    public static void teleport(World world, PlayerEntity player, BlockPos pos) {
        if (!world.isRemote) {
            if (player != null) {
                if (player.isPassenger()) {
                    player.stopRiding();
                }
                player.teleportKeepLoaded(pos.getX(), pos.getY() + 1.5, pos.getZ());
                player.fallDistance = 0.0F;
            }
        }
    }

    public static void riotEntity(CreatureEntity target, PlayerEntity allomancer, boolean enhanced) {
        try {
            if (!enhanced) {
                //Enable Targeting goals
                target.targetSelector.enableFlag(Goal.Flag.TARGET);
                //Add new goals
                target.setAttackTarget(allomancer);
                target.setRevengeTarget(allomancer);
                // TODO: try to use PrioritizedGoal::startExecuting for already hostiles
                target.targetSelector.addGoal(1, new AIAttackOnCollideExtended(target, 1d, false));
                target.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(target, PlayerEntity.class, false));
                target.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(target, target.getClass(), false));
                target.goalSelector.addGoal(4, new LookRandomlyGoal(target));
                target.targetSelector.addGoal(2, new HurtByTargetGoal(target).setCallsForHelp());
                if (target.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null && !(target instanceof GuardianEntity)) {
                    target.goalSelector.addGoal(3, new MeleeAttackGoal(target, 1.2D, true));
                }

                target.setAggroed(true);

                if (target instanceof CreeperEntity) {
                    target.goalSelector.addGoal(1, new CreeperSwellGoal((CreeperEntity) target));
                }
                if (target instanceof RabbitEntity) {
                    target.goalSelector.addGoal(1, new AIEvilAttack((RabbitEntity) target));
                }
                if (target instanceof AbstractSkeletonEntity) {
                    target.goalSelector.addGoal(1, new RangedBowAttackGoal<>((AbstractSkeletonEntity) target, 1.0D, 20, 15.0F));
                }
                if (target instanceof IllusionerEntity) {
                    target.goalSelector.addGoal(1, new RangedBowAttackGoal<>((IllusionerEntity) target, 0.5D, 20, 15.0F));
                }
                if (target instanceof PillagerEntity) {
                    target.goalSelector.addGoal(2, new RangedCrossbowAttackGoal<>((PillagerEntity) target, 1.0D, 8.0F));
                }
            } else {
                target.world.createExplosion(target,
                        target.getPositionVec().getX(), target.getPositionVec().getY(), target.getPositionVec().getZ(),
                        1.2F, false, Explosion.Mode.BREAK);
                target.remove();
            }
        } catch (Exception e) {
            Allomancy.LOGGER.error("Failed to riot entity " + target + "! Please report this error!", e);
        }
    }

    public static void sootheEntity(CreatureEntity target, PlayerEntity allomancer, boolean enhanced) {
        try {
            if (!enhanced) {
                if(target.isAIDisabled()){
                    target.setNoAI(false);
                }
                // Remove all current aggro goals
                // TODO: Try PrioritizedGoal::resetTask
                target.goalSelector.getRunningGoals().filter(isAggroGoal).forEach(target.goalSelector::removeGoal);
                target.targetSelector.getRunningGoals().filter(isAggroGoal).forEach(target.targetSelector::removeGoal);
                target.goalSelector.tick();
                target.targetSelector.tick();
                target.setAttackTarget(null);
                target.setRevengeTarget(null);
                //Disable targeting as a whole
                target.targetSelector.disableFlag(Goal.Flag.TARGET);
                target.setAggroed(false);
                //Add new goals
                target.goalSelector.addGoal(7, new LookAtGoal(target, PlayerEntity.class, 6.0F));

                if (target instanceof TameableEntity) {
                    if (Math.random() < 0.3)
                        ((TameableEntity) target).setTamedBy(allomancer);
                }
                if (target instanceof AbstractHorseEntity) {
                    if (Math.random() < 0.3)
                        ((AbstractHorseEntity) target).setTamedBy(allomancer);
                }
                if (target instanceof SheepEntity) {
                    target.goalSelector.addGoal(1, new EatGrassGoal(target));
                }
                if (target instanceof VillagerEntity) {
                    ((VillagerEntity) target).updateReputation(IReputationType.TRADE, allomancer);
                }
                if (target instanceof WanderingTraderEntity) {
                    target.goalSelector.addGoal(1, new TradeWithPlayerGoal((AbstractVillagerEntity) target));
                }
            } else { // Completely remove all ai if enhanced
                target.setNoAI(true);
            }

        } catch (Exception e) {
            Allomancy.LOGGER.error("Failed to soothe entity " + target + "! Please report this error!", e);
        }

    }

    private static Predicate<Goal> isAggroGoal = (goal) ->
            goal instanceof CreeperSwellGoal ||
                    goal instanceof AIAttackOnCollideExtended ||
                    goal instanceof MeleeAttackGoal ||
                    goal instanceof TargetGoal ||
                    goal instanceof PanicGoal ||
                    goal.getClass().getName().contains("Fireball") ||
                    goal.getClass().getName().contains("Attack") ||
                    goal.getClass().getName().contains("Anger");
}
