package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.portal.TeleportTransition.DO_NOTHING;


public final class Enhancement {
    private Enhancement() {}

    /**
     * Wipe all metals from the player and sync to tracking entities. Used by Aluminum and Nicrosil
     *
     * @param player The player to wipe
     */
    public static void wipePlayer(ServerPlayer player) {
        var data = AllomancerAttachment.get(player);
        data.drainMetals(Metal.values());
        player.removeAllEffects();
        AllomancerAttachment.sync(player);
    }

    /**
     * Teleports a player to the given dimension and blockpos
     *
     * @param player    The player to move
     * @param world     The server world
     * @param dimension Dimension to transport to
     * @param pos       BlockPos to move the player to using {@link Entity#teleportTo(double, double, double)}
     */
    private static void teleport(ServerPlayer player, ServerLevel world, ResourceKey<Level> dimension, BlockPos pos) {
        if (player != null) {
            if (player.isPassenger()) {
                player.stopRiding();
            }

            if (player.level().dimension() != dimension) {
                //change dimension
                player = player.teleport(
                        new TeleportTransition(world.getServer().getLevel(dimension), Vec3.atBottomCenterOf(pos),
                                               Vec3.ZERO, player.getXRot(), player.getYRot(), DO_NOTHING));
            }
            var center = pos.above().getCenter();
            player.teleportTo(center.x(), center.y(), center.z());
            player.fallDistance = 0.0F;
        }
    }

    public static void teleportToLastDeath(ServerPlayer curPlayer, ServerLevel level, IAllomancerData data) {

        curPlayer.getLastDeathLocation().ifPresent(death -> {
            teleport(curPlayer, level, death.dimension(), death.pos());
            if (data.isBurning(Metal.DURALUMIN)) {
                data.drainMetals(Metal.DURALUMIN);
            }
            data.drainMetals(Metal.GOLD);
        });
    }

    public static void teleportToSpawn(ServerPlayer curPlayer, ServerLevel level, IAllomancerData data) {
        var spawn = data.getSpawnLoc();

        if (spawn == null) {  // no spawn --> use world spawn
            spawn = GlobalPos.of(Level.OVERWORLD, level.getLevelData().getSpawnPos());
        }

        teleport(curPlayer, level, spawn.dimension(), spawn.pos());
        if (data.isBurning(Metal.DURALUMIN)) {
            data.drainMetals(Metal.DURALUMIN);
        }
        data.drainMetals(Metal.ELECTRUM);
    }

    public static void wipeNearby(ServerPlayer curPlayer, ServerLevel level) {
        int max = 20;
        Vec3 negative = curPlayer.position().add(-max, -max, -max);
        Vec3 positive = curPlayer.position().add(max, max, max);
        level.getEntitiesOfClass(ServerPlayer.class, new AABB(negative, positive)).forEach(player -> {
            ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER.get().trigger(curPlayer, player, Metal.CHROMIUM, true);
            if (!Emotional.hasTinFoilHat(player)) {
                wipePlayer(player);
            }
        });
    }
}
