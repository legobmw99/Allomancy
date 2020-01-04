package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.modules.combat.entity.GoldNuggetEntity;
import com.legobmw99.allomancy.modules.combat.entity.IronNuggetEntity;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.network.AllomancyCapabilityPacket;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Contains all static, common methods in one place
 */

public class AllomancyUtils {


    public static final byte PUSH = 1;
    public static final byte PULL = -1;


    /**
     * Determines if a block is metal or not
     *
     * @param block to be checked
     * @return Whether or not the item is metal
     */
    public static boolean isBlockMetal(Block block) {
        return PowersConfig.whitelist.contains(block.getRegistryName().toString());
    }

    /**
     * Determines if an item is metal or not
     *
     * @param item to be checked
     * @return Whether or not the item is metal
     */
    public static boolean isItemMetal(ItemStack item) {
        return PowersConfig.whitelist.contains(item.getItem().getRegistryName().toString());
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
            return isBlockMetal(((FallingBlockEntity) entity).getBlockState().getBlock());
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

    /**
     * Move an entity either toward or away from an anchor point
     *
     * @param directionScalar the direction and (possibly) scalar multiple of the magnitude
     * @param toMove          the entity to move
     * @param block             the point being moved toward or away from
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
        if (toMove instanceof ServerPlayerEntity) {
            toMove.fallDistance = 0;
        }
    }


    /**
     * Runs each worldTick, checking the burn times, abilities, and metal
     * amounts. Then syncs to the client to make sure everyone is on the same
     * page
     *
     * @param capability the AllomancyCapabilities data
     * @param player     the player being checked
     */
    public static void updateMetalBurnTime(AllomancyCapability capability, ServerPlayerEntity player) {
        for (int i = 0; i < 8; i++) {
            if (capability.getMetalBurning(i)) {
                if (capability.getAllomancyPower() != i && capability.getAllomancyPower() != 8) {
                    // put out any metals that the player shouldn't be able to burn
                    capability.setMetalBurning(i, false);
                    Network.sendTo(new AllomancyCapabilityPacket(capability, player.getEntityId()), player);
                } else {
                    capability.setBurnTime(i, capability.getBurnTime(i) - 1);
                    if (capability.getBurnTime(i) == 0) {
                        capability.setBurnTime(i, capability.MAX_BURN_TIME[i]);
                        capability.setMetalAmounts(i, capability.getMetalAmounts(i) - 1);
                        Network.sendTo(new AllomancyCapabilityPacket(capability, player.getEntityId()), player);
                        if (capability.getMetalAmounts(i) == 0) {
                            capability.setMetalBurning(i, false);
                            Network.sync(capability, player);
                        }
                    }
                }
            }
        }
    }
}
