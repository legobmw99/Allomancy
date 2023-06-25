package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.modules.powers.entity.ai.AIAttackOnCollideExtended;
import com.legobmw99.allomancy.modules.powers.entity.ai.AIEvilAttack;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Contains all static, common methods in one place
 */

public class PowerUtils {

    public static final byte PUSH = 1;
    public static final byte PULL = -1;
    private static final Predicate<Goal> isAggroGoal = (goal) -> goal instanceof SwellGoal || goal instanceof AIAttackOnCollideExtended || goal instanceof MeleeAttackGoal ||
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
     * @return whether the block state is metal
     */
    public static boolean isBlockStateMetal(BlockState state) {
        return isBlockMetal(state.getBlock());
    }

    /**
     * Determines if a block is metal or not
     *
     * @param block to be checked
     * @return Whether the block is metal
     */
    public static boolean isBlockMetal(Block block) {
        return isOnWhitelist(ForgeRegistries.BLOCKS.getKey(block).toString());
    }

    /**
     * Determines if an item is metal or not
     *
     * @param item to be checked
     * @return Whether the item is metal
     */
    public static boolean isItemMetal(ItemStack item) {
        return isOnWhitelist(ForgeRegistries.ITEMS.getKey(item.getItem()).toString());
    }

    private static boolean isOnWhitelist(String s) {
        return PowersConfig.whitelist.contains(s);
    }

    /**
     * Determines if an entity is metal or not
     *
     * @param entity to be checked
     * @return Whether the entity is metallic
     */
    public static boolean isEntityMetal(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof ItemEntity item) {
            return isItemMetal(item.getItem());
        }
        if (entity instanceof ItemFrame itemFrame) {
            return isItemMetal(itemFrame.getItem());
        }
        if (entity instanceof FallingBlockEntity fbe) {
            return isBlockStateMetal(fbe.getBlockState());
        }
        if (entity instanceof ProjectileNuggetEntity) {
            return true;
        }
        if (entity instanceof AbstractMinecart) {
            return true;
        }
        if (entity instanceof LivingEntity ent) {
            if (ent instanceof IronGolem) {
                return true;
            }
            if (isItemMetal(ent.getItemInHand(InteractionHand.MAIN_HAND)) || isItemMetal(ent.getItemInHand(InteractionHand.OFF_HAND))) {
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
    public static void wipePlayer(Player player) {
        player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
            data.drainMetals(Metal.values());
            player.removeAllEffects();

            if (player instanceof ServerPlayer sp) {
                Network.sync(data, sp);
            }
        });
    }

    /**
     * Move an entity either toward or away from an anchor point
     *
     * @param directionScalar the direction and (possibly) scalar multiple of the magnitude
     * @param toMove          the entity to move
     * @param block           the point being moved toward or away from
     */
    public static void move(double directionScalar, Entity toMove, BlockPos block) {

        if (toMove.isPassenger()) {
            toMove = toMove.getVehicle();
        }

        Vec3 motion = toMove.position().subtract(Vec3.atCenterOf(block)).normalize().scale(directionScalar * 1.1);
        Vec3 mod = clamp(cutoff(motion.add(toMove.getDeltaMovement()), 0.1), abs(motion).reverse(), abs(motion));
        toMove.setDeltaMovement(mod);
        toMove.hurtMarked = true;

        // Only save players from fall damage
        if (toMove instanceof ServerPlayer) {
            toMove.fallDistance = 0;
        }
    }


    /*
     * Three helper functions for working with Vector3ds
     */
    private static Vec3 clamp(Vec3 value, Vec3 min, Vec3 max) {
        return new Vec3(Mth.clamp(value.x, min.x, max.x), Mth.clamp(value.y, min.y, max.y), Mth.clamp(value.z, min.z, max.z));
    }

    private static Vec3 abs(Vec3 vec) {
        return new Vec3(Math.abs(vec.x), Math.abs(vec.y), Math.abs(vec.z));
    }

    private static Vec3 cutoff(Vec3 value, double e) {
        Vec3 mag = abs(value);
        return new Vec3(mag.x < e ? 0 : value.x, mag.y < e ? 0 : value.y, mag.z < e ? 0 : value.z);
    }


    /**
     * Teleports a player to the given dimension and blockpos
     *
     * @param player    The player to move
     * @param world     The server world. Fails if clientside
     * @param dimension Dimension to call {@link Entity#changeDimension} on
     * @param pos       BlockPos to move the player to using {@link Entity#teleportToWithTicket}
     */
    public static void teleport(Player player, Level world, ResourceKey<Level> dimension, BlockPos pos) {
        if (!world.isClientSide) {
            if (player != null) {
                if (player.isPassenger()) {
                    player.stopRiding();
                }

                if (player.level().dimension() != dimension) {
                    //change dimension
                    player = (Player) player.changeDimension(world.getServer().getLevel(dimension), new ITeleporter() {
                        @Override
                        public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
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

    public static void riotEntity(PathfinderMob target, Player allomancer, boolean enhanced) {
        try {
            if (!enhanced) {
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
                target.level().explode(target, target.position().x(), target.position().y(), target.position().z(), 1.2F, false, Level.ExplosionInteraction.MOB);
                target.kill();
            }
        } catch (Exception e) {
            Allomancy.LOGGER.error("Failed to riot entity " + target + "! Please report this error!", e);
        }
    }

    public static void sootheEntity(PathfinderMob target, Player allomancer, boolean enhanced) {
        try {
            if (!enhanced) {
                if (target.isNoAi()) {
                    target.setNoAi(false);
                }
                // Reset all current aggro goals
                target.goalSelector.getRunningGoals().filter(isAggroGoal).forEach(WrappedGoal::stop);
                target.targetSelector.getRunningGoals().filter(isAggroGoal).forEach(WrappedGoal::stop);
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
}
