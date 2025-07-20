package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class Enhancement {
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
                Network.syncAllomancerData(sp);
            }
        });
    }

    /**
     * Teleports a player to the given dimension and blockpos
     *
     * @param player    The player to move
     * @param world     The server world. Fails if clientside
     * @param dimension Dimension to call {@link Entity#changeDimension} on
     * @param pos       BlockPos to move the player to using {@link Entity#teleportToWithTicket}
     */
    private static void teleport(Player player, Level world, ResourceKey<Level> dimension, BlockPos pos) {
        if (!world.isClientSide) {
            if (player != null) {
                if (player.isPassenger()) {
                    player.stopRiding();
                }

                if (player.level().dimension() != dimension) {
                    //change dimension
                    player =
                            (Player) player.changeDimension(world.getServer().getLevel(dimension), new ITeleporter() {
                                @Override
                                public Entity placeEntity(Entity entity,
                                                          ServerLevel currentWorld,
                                                          ServerLevel destWorld,
                                                          float yaw,
                                                          Function<Boolean, Entity> repositionEntity) {
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

    public static void teleportToLastDeath(Player curPlayer, Level level, IAllomancerData data) {
        curPlayer.getLastDeathLocation().ifPresent(death -> {
            teleport(curPlayer, level, death.dimension(), death.pos());
            if (data.isBurning(Metal.DURALUMIN)) {
                data.drainMetals(Metal.DURALUMIN);
            }
            data.drainMetals(Metal.GOLD);
        });
    }

    public static void teleportToSpawn(Player curPlayer, Level level, IAllomancerData data) {
        GlobalPos spawn = data.getSpawnLoc();
        ResourceKey<Level> spawnDim;
        BlockPos spawnLoc;

        if (spawn != null) {
            spawnDim = spawn.dimension();
            spawnLoc = spawn.pos();
        } else {
            spawnDim = Level.OVERWORLD; // no spawn --> use world spawn
            spawnLoc = new BlockPos(level.getLevelData().getXSpawn(), level.getLevelData().getYSpawn(),
                                    level.getLevelData().getZSpawn());

        }

        teleport(curPlayer, level, spawnDim, spawnLoc);
        if (data.isBurning(Metal.DURALUMIN)) {
            data.drainMetals(Metal.DURALUMIN);
        }
        data.drainMetals(Metal.ELECTRUM);
    }

    public static void wipeNearby(Player curPlayer, Level level) {
        if (level instanceof ServerLevel) {
            int max = 20;
            Vec3 negative = curPlayer.position().add(-max, -max, -max);
            Vec3 positive = curPlayer.position().add(max, max, max);
            level
                    .getEntitiesOfClass(Player.class, new AABB(negative, positive))
                    .forEach(otherPlayer -> otherPlayer
                            .getCapability(AllomancerCapability.PLAYER_CAP)
                            .ifPresent(data -> data.drainMetals(Metal.values())));
        }
    }
}