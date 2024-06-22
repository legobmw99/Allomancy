package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.util.Emotional;
import com.legobmw99.allomancy.modules.powers.util.Physical;
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
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;

public class ServerPayloadHandler {

    public static void changeEmotion(final EmotionPayload data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer allomancer = (ServerPlayer) ctx.player();
            PathfinderMob target = (PathfinderMob) allomancer.level().getEntity(data.entityID());
            if (target == null) {
                return;
            }
            boolean enhanced = allomancer.getData(AllomancerAttachment.ALLOMANCY_DATA).isEnhanced();
            ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER
                    .get()
                    .trigger(allomancer, target, data.makeAggressive() ? Metal.ZINC : Metal.BRASS, enhanced);
            if (data.makeAggressive()) {
                Emotional.riot(target, allomancer, enhanced);
            } else {
                Emotional.soothe(target, allomancer, enhanced);
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle changeEmotions", e);
            ctx.disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });

    }

    public static void tryPushPullBlock(final BlockPushPullPayload data, final IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        Level level = player.level();
        BlockPos pos = data.block();
        // Sanity check to make sure  the block is loaded in the server
        if (level.isLoaded(pos)) {
            // activate blocks
            BlockState blockState = level.getBlockState(pos);
            var allomanticUseCap =
                    level.getCapability(ExtrasSetup.ALLOMANTICALLY_USABLE_BLOCK, pos, blockState, null, null);

            if (allomanticUseCap != null) {
                allomanticUseCap.useAllomantically(player, data.isPush());
            } else if (Physical.isBlockStateMetallic(blockState) // Check whitelist on server
                       || (player.isCrouching() && data.isPush() &&
                           player.getMainHandItem().getItem() == CombatSetup.COIN_BAG.get() // check coin bag
                                   /* causes problems if there is exactly 1 nugget, which just got fired!
                                    && (!player.getProjectile(player.getMainHandItem()).isEmpty())*/)) {
                Physical.lurch(data.direction(), player, pos);
            } else {
                Allomancy.LOGGER.warn("Illegal use of iron/steel by player: {}!", player);
                ctx.disconnect(Component.translatable("allomancy.networking.kicked",
                                                      "Tried to push or pull against an non-metallic block!"));
            }
        } else {
            Allomancy.LOGGER.warn("Illegal use of iron/steel by player: {}!", player);
            ctx.disconnect(Component.translatable("allomancy.networking.kicked",
                                                  "Tried to push or pull against an unloaded block!"));
        }
    }

    public static void tryPushPullEntity(final EntityPushPullPayload payload, final IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        Level level = player.level();
        Entity target = level.getEntity(payload.entityID());
        IAllomancerData data = player.getData(AllomancerAttachment.ALLOMANCY_DATA.get());
        Metal which = payload.isPush() ? Metal.STEEL : Metal.IRON;

        if (target != null) {
            if (Physical.isEntityMetallic(target)) {
                ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER.get().trigger(player, target, which, data.isEnhanced());

                // The player moves
                if (target instanceof IronGolem || target instanceof ItemFrame) {
                    Physical.lurch(payload.force(), player, target.blockPosition());
                } else if (target instanceof ItemEntity || target instanceof FallingBlockEntity ||
                           target instanceof ArmorStand ||
                           (target instanceof AbstractMinecart && !target.isVehicle())) {
                    Physical.lurch(payload.force() / 2.0, target, player.blockPosition());

                    // Split the difference
                } else if (!(target instanceof ThrowableItemProjectile)) {
                    if (target instanceof ServerPlayer targetPlayer) {
                        ExtrasSetup.METAL_USED_ON_PLAYER_TRIGGER
                                .get()
                                .trigger(targetPlayer, which, data.isEnhanced());
                    }
                    Physical.lurch(payload.force() / 2.0, target, player.blockPosition());
                    Physical.lurch(payload.force() / 2.0, player, target.blockPosition());
                }
            }
        }
    }

    public static void toggleBurnRequest(final ToggleBurnPayload payload, final IPayloadContext ctx) {
        assert ctx.flow().isServerbound();

        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();
            Metal mt = payload.metal();
            IAllomancerData data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

            boolean value = payload.on();

            if (!data.hasPower(mt) || data.getStored(mt) <= 0) {
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
            ctx.disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void updateEnhanced(final EnhanceTimePayload payload, final IPayloadContext ctx) {
        assert ctx.flow().isServerbound();

        ctx.enqueueWork(() -> {
            ServerPlayer source = (ServerPlayer) ctx.player();
            IAllomancerData data = source.getData(AllomancerAttachment.ALLOMANCY_DATA);
            if (!data.isBurning(Metal.NICROSIL)) {
                Allomancy.LOGGER.warn("Illegal use of Nicrosil by player: {}!", source);
                ctx.disconnect(Component.translatable("allomancy.networking.kicked",
                                                      "Tried to mark other player as enhanced while not burning " +
                                                      "Nicrosil!"));
                return;
            }

            Entity e = source.level().getEntity(payload.entityID());
            ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER.get().trigger(source, e, Metal.NICROSIL, data.isEnhanced());
            if (e instanceof ServerPlayer target) {
                ExtrasSetup.METAL_USED_ON_PLAYER_TRIGGER.get().trigger(target, Metal.NICROSIL, data.isEnhanced());
                if (!Emotional.hasTinFoilHat(target)) {
                    target.getData(AllomancerAttachment.ALLOMANCY_DATA).setEnhanced(payload.enhanceTime());
                    // broadcast back to player and tracking
                    Network.sync(payload, target);

                }
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle sever updateEnhanced", e);
            ctx.disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }
}
