package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.block.IAllomanticallyUsableBlock;
import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.powers.PowerUtils;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Arrays;

public class ServerPayloadHandler {

    public static void handleEmotionChange(final EmotionPayload data, final PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ServerPlayer allomancer = (ServerPlayer) ctx.player().get();
            PathfinderMob target = (PathfinderMob) ctx.level().get().getEntity(data.entityID());
            if (target == null) {
                return;
            }
            boolean enhanced = allomancer.getData(AllomancerAttachment.ALLOMANCY_DATA).isEnhanced();
            ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER.get().trigger(allomancer, target, data.makeAggressive() ? Metal.ZINC : Metal.BRASS, enhanced);
            if (data.makeAggressive()) {
                PowerUtils.riotEntity(target, allomancer, enhanced);
            } else {
                PowerUtils.sootheEntity(target, allomancer, enhanced);
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle changeEmotions", e);
            ctx.packetHandler().disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });

    }

    public static void tryPushPullBlock(final BlockPushPullPayload data, final PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            Player player = ctx.player().get();
            Level level = ctx.level().get();
            BlockPos pos = data.block();
            // Sanity check to make sure  the block is loaded in the server
            if (level.isLoaded(pos)) {
                // activate blocks
                if (level.getBlockState(pos).getBlock() instanceof IAllomanticallyUsableBlock block) {
                    block.useAllomantically(level.getBlockState(pos), level, pos, player, data.isPush());
                } else if (PowerUtils.isBlockStateMetal(player.level().getBlockState(pos)) // Check whitelist on server
                           || (player.getMainHandItem().getItem() == CombatSetup.COIN_BAG.get() // check coin bag
                               && (!player.getProjectile(player.getMainHandItem()).isEmpty()) && data.isPush())) {
                    PowerUtils.move(data.direction(), player, pos);
                }
            } else {
                Allomancy.LOGGER.warn("Illegal use of iron/steel by player: {}!", player);
                ctx.packetHandler().disconnect(Component.translatable("allomancy.networking.kicked", "Tried to push or pull against an unloaded block!"));
            }
        });
    }

    public static void tryPushPullEntity(final EntityPushPullPayload payload, final PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player().get();
            Level level = ctx.level().get();
            Entity target = level.getEntity(payload.entityID());
            IAllomancerData data = player.getData(AllomancerAttachment.ALLOMANCY_DATA.get());
            Metal which = payload.isPush() ? Metal.STEEL : Metal.IRON;

            if (target != null) {
                if (PowerUtils.isEntityMetal(target)) {
                    ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER.get().trigger(player, target, which, data.isEnhanced());

                    // The player moves
                    if (target instanceof IronGolem || target instanceof ItemFrame) {
                        PowerUtils.move(payload.direction(), player, target.blockPosition());
                    } else if (target instanceof ItemEntity || target instanceof FallingBlockEntity || target instanceof ArmorStand ||
                               (target instanceof AbstractMinecart && !target.isVehicle())) {
                        PowerUtils.move(payload.direction() / 2.0, target, player.blockPosition());

                        // Split the difference
                    } else if (!(target instanceof ThrowableItemProjectile)) {
                        if (target instanceof ServerPlayer targetPlayer) {
                            ExtrasSetup.METAL_USED_ON_PLAYER_TRIGGER.get().trigger(targetPlayer, which, data.isEnhanced());
                        }
                        PowerUtils.move(payload.direction() / 2.0, target, player.blockPosition());
                        PowerUtils.move(payload.direction() / 2.0, player, target.blockPosition());
                    }
                }
            }
        });
    }

    public static void toggleBurnRequest(final ToggleBurnPayload payload, final PlayPayloadContext ctx) {
        assert ctx.flow().getReceptionSide().isServer();

        ctx.workHandler().submitAsync(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player().get();
            Metal mt = payload.metal();
            IAllomancerData data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

            boolean value = payload.on();

            if (!data.hasPower(mt) || data.getAmount(mt) <= 0) {
                value = false;
            }

            data.setBurning(mt, value);
            if (!value && mt == Metal.DURALUMIN) {
                data.drainMetals(Arrays.stream(Metal.values()).filter(data::isBurning).toArray(Metal[]::new));
            }
            if (!value && data.isEnhanced()) {
                data.drainMetals(mt);
            }

            Network.syncAllomancerData(player);

        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle toggleBurn", e);
            ctx.packetHandler().disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void updateEnhanced(final EnhanceTimePayload payload, final PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ServerPlayer source = (ServerPlayer) ctx.player().get();
            var data = source.getData(AllomancerAttachment.ALLOMANCY_DATA);
            if (!data.isBurning(Metal.NICROSIL)) {
                Allomancy.LOGGER.warn("Illegal use of Nicrosil by player: {}!", source);
                ctx.packetHandler().disconnect(Component.translatable("allomancy.networking.kicked", "Tried to mark other player as enhanced while not burning Nicrosil!"));
                return;
            }

            Entity e = ctx.level().get().getEntity(payload.entityID());
            ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER.get().trigger(source, e, Metal.NICROSIL, data.isEnhanced());
            if (e instanceof ServerPlayer player) {
                ExtrasSetup.METAL_USED_ON_PLAYER_TRIGGER.get().trigger(player, Metal.NICROSIL, data.isEnhanced());
                if (!PowerUtils.hasTinFoilHat(player)) {
                    var target_data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
                    target_data.setEnhanced(payload.enhanceTime());
                    // broadcast back to player and tracking
                    Network.sync(payload, player);

                }
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle sever updateEnhanced", e);
            ctx.packetHandler().disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }
}
