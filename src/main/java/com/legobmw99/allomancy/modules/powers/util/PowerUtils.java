package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.entity.ai.AIAttackOnCollideExtended;
import com.legobmw99.allomancy.modules.powers.entity.ai.AIEvilAttack;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.util.Metal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Contains all static, common methods in one place
 */

public class PowerUtils {

    public static final byte PUSH = 1;
    public static final byte PULL = -1;
    private static final Predicate<Goal> isAggroGoal = (goal) -> goal instanceof CreeperSwellGoal || goal instanceof AIAttackOnCollideExtended || goal instanceof MeleeAttackGoal ||
                                                                 goal instanceof TargetGoal || goal instanceof PanicGoal || goal.getClass().getName().contains("Fireball") ||
                                                                 goal.getClass().getName().contains("Attack") || goal.getClass().getName().contains("Anger");

    private static final Pattern ACTIVE_METAL_REGEX = Pattern.compile(
            ".*(iron|steel|tin_|pewter|zinc|brass|copper|bronze|duralumin|chromium|nicrosil|gold|electrum|cadmium|bendalloy|lead_|silver|platinum|nickle).*");


    public static boolean doesResourceContainsMetal(ResourceLocation input) {
        return ACTIVE_METAL_REGEX.matcher(input.getPath()).matches();
    }


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

    private static boolean isOnWhitelist(String s) {
        return PowersConfig.whitelist.contains(s);
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
            return isItemMetal(((ItemFrameEntity) entity).getItem());
        }
        if (entity instanceof FallingBlockEntity) {
            return isBlockStateMetal(((FallingBlockEntity) entity).getBlockState());
        }
        if (entity instanceof ProjectileNuggetEntity) {
            return true;
        }
        if (entity instanceof AbstractMinecartEntity) {
            return true;
        }
        if (entity instanceof LivingEntity) {
            LivingEntity ent = (LivingEntity) entity;
            if (ent instanceof IronGolemEntity) {
                return true;
            }
            if (isItemMetal(ent.getItemInHand(Hand.MAIN_HAND)) || isItemMetal(ent.getItemInHand(Hand.OFF_HAND))) {
                return true;
            }
            for (ItemStack itemStack : ent.getArmorSlots()) {
                if (isItemMetal(itemStack)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Wipe all metals from the player and sync to tracking entities. Used by Aluminum and Nicrosil
     *
     * @param player The player to wipe
     */
    public static void wipePlayer(PlayerEntity player) {
        AllomancyCapability capHurt = AllomancyCapability.forPlayer(player);
        capHurt.drainMetals(Metal.values());
        player.removeAllEffects();

        if (player instanceof ServerPlayerEntity) {
            Network.sync(capHurt, player);
        }
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
            toMove = toMove.getVehicle();
        }
        Vector3d vec = toMove.position();
        double posX = vec.x(), posY = vec.y(), posZ = vec.z();
        // Calculate the length of the vector between the entity and anchor
        magnitude = Math.sqrt(Math.pow((posX - (block.getX() + .5)), 2) + Math.pow((posY - (block.getY() + .5)), 2) + Math.pow((posZ - (block.getZ() + .5)), 2));
        // Get a unit(-ish) vector in the direction of motion
        motionX = ((posX - (block.getX() + .5)) * directionScalar * (1.1) / magnitude);
        motionY = ((posY - (block.getY() + .5)) * directionScalar * (1.1) / magnitude);
        motionZ = ((posZ - (block.getZ() + .5)) * directionScalar * (1.1) / magnitude);
        // Move along that vector, additively increasing motion until you max
        // out at the above values
        double x = toMove.getDeltaMovement().x(), y = toMove.getDeltaMovement().y(), z = toMove.getDeltaMovement().z();
        toMove.setDeltaMovement(Math.abs(x + motionX) > 0.01 ? MathHelper.clamp(x + motionX, -Math.abs(motionX), motionX) : 0,
                                Math.abs(y + motionY) > 0.01 ? MathHelper.clamp(y + motionY, -Math.abs(motionY), motionY) : 0,
                                Math.abs(z + motionZ) > 0.01 ? MathHelper.clamp(z + motionZ, -Math.abs(motionZ), motionZ) : 0);

        toMove.hurtMarked = true;

        // Only save players from fall damage
        if (toMove instanceof ServerPlayerEntity && Math.abs(directionScalar) <= 1) {
            toMove.fallDistance = 0;
        }
    }

    /**
     * Teleports a player to the given dimension and blockpos
     *
     * @param player    The player to move
     * @param world     The server world. Fails if clientside
     * @param dimension Dimension to call {@link Entity#changeDimension} on
     * @param pos       BlockPos to move the player to using {@link Entity#teleportKeepLoaded}
     */
    public static void teleport(PlayerEntity player, World world, RegistryKey<World> dimension, BlockPos pos) {
        if (!world.isClientSide) {
            if (player != null) {
                if (player.isPassenger()) {
                    player.stopRiding();
                }

                if (player.level.dimension() != dimension) {
                    //change dimension
                    player = (PlayerEntity) player.changeDimension(world.getServer().getLevel(dimension), new ITeleporter() {
                        @Override
                        public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                            Entity repositionedEntity = repositionEntity.apply(false);
                            repositionedEntity.teleportTo(pos.getX(), pos.getY(), pos.getZ());
                            return repositionedEntity;
                        }
                    });
                }

                player.teleportToWithTicket(pos.getX(), pos.getY() + 1.5, pos.getZ());
                player.fallDistance = 0.0F;
            }
        }
    }

    public static void riotEntity(CreatureEntity target, PlayerEntity allomancer, boolean enhanced) {
        try {
            if (!enhanced) {
                //Enable Targeting goals
                target.targetSelector.enableControlFlag(Goal.Flag.TARGET);
                //Add new goals
                target.setTarget(allomancer);
                target.setLastHurtByMob(allomancer);
                // TODO: try to use PrioritizedGoal::startExecuting for already hostiles
                target.targetSelector.addGoal(1, new AIAttackOnCollideExtended(target, 1d, false));
                target.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(target, PlayerEntity.class, false));
                target.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(target, target.getClass(), false));
                target.goalSelector.addGoal(4, new LookRandomlyGoal(target));
                target.targetSelector.addGoal(2, new HurtByTargetGoal(target).setAlertOthers());
                if (target.getAttribute(Attributes.ATTACK_DAMAGE) != null && !(target instanceof GuardianEntity)) {
                    target.goalSelector.addGoal(3, new MeleeAttackGoal(target, 1.2D, true));
                }

                target.setAggressive(true);

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
                target.level.explode(target, target.position().x(), target.position().y(), target.position().z(), 1.2F, false, Explosion.Mode.BREAK);
                target.remove();
            }
        } catch (Exception e) {
            Allomancy.LOGGER.error("Failed to riot entity " + target + "! Please report this error!", e);
        }
    }

    public static void sootheEntity(CreatureEntity target, PlayerEntity allomancer, boolean enhanced) {
        try {
            if (!enhanced) {
                if (target.isNoAi()) {
                    target.setNoAi(false);
                }
                // Reset all current aggro goals
                target.goalSelector.getRunningGoals().filter(isAggroGoal).forEach(PrioritizedGoal::stop);
                target.targetSelector.getRunningGoals().filter(isAggroGoal).forEach(PrioritizedGoal::stop);
                target.goalSelector.tick();
                target.targetSelector.tick();
                target.setTarget(null);
                target.setLastHurtByMob(null);
                //Disable targeting as a whole
                target.targetSelector.disableControlFlag(Goal.Flag.TARGET);
                target.setAggressive(false);
                //Add new goals
                target.goalSelector.addGoal(7, new LookAtGoal(target, PlayerEntity.class, 6.0F));

                if (target instanceof TameableEntity) {
                    if (Math.random() < 0.3) {
                        ((TameableEntity) target).tame(allomancer);
                    }
                }
                if (target instanceof AbstractHorseEntity) {
                    if (Math.random() < 0.3) {
                        ((AbstractHorseEntity) target).tameWithName(allomancer);
                    }
                }
                if (target instanceof SheepEntity) {
                    target.goalSelector.addGoal(1, new EatGrassGoal(target));
                }
                if (target instanceof VillagerEntity) {
                    ((VillagerEntity) target).onReputationEventFrom(IReputationType.TRADE, allomancer);
                }
                if (target instanceof WanderingTraderEntity) {
                    target.goalSelector.addGoal(1, new TradeWithPlayerGoal((AbstractVillagerEntity) target));
                }
            } else { // Completely remove all ai if enhanced
                target.setNoAi(true);
            }

        } catch (Exception e) {
            Allomancy.LOGGER.error("Failed to soothe entity " + target + "! Please report this error!", e);
        }

    }
}
