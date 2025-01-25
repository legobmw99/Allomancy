package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Temporal {
    private Temporal() {}

    @SuppressWarnings("unchecked")
    public static void speedUpNearby(ServerPlayer curPlayer, ServerLevel level, IAllomancerData data) {
        curPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 10, 3, true, false));

        int max = data.isEnhanced() ? 10 : 5;
        BlockPos negative = curPlayer.blockPosition().offset(-max, -max, -max);
        BlockPos positive = curPlayer.blockPosition().offset(max, max, max);
        level
                .getEntitiesOfClass(LivingEntity.class, AABB.encapsulatingFullBlocks(negative, positive))
                .forEach(entity -> {
                    ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER
                            .get()
                            .trigger(curPlayer, entity, Metal.BENDALLOY, data.isEnhanced());
                    entity.aiStep();
                    entity.aiStep();
                });
        BlockPos.betweenClosedStream(negative, positive).forEach(bp -> {
            BlockState block = level.getBlockState(bp);
            BlockEntity te = level.getBlockEntity(bp);
            if (te == null) {
                if (block.isRandomlyTicking()) {
                    for (int i = 0; i < max * 4 / 15; i++) {
                        block.randomTick(level, bp, level.random);
                    }
                }
            } else {
                Block underlying_block = block.getBlock();
                if (underlying_block instanceof EntityBlock eb) {
                    BlockEntityTicker ticker = eb.getTicker(level, block, te.getType());
                    if (ticker != null) {
                        for (int i = 0; i < max * 4 / 3; i++) {
                            ticker.tick(level, bp, block, te);
                        }
                    }
                }
            }
        });
    }

    public static void slowDownNearby(ServerPlayer curPlayer, ServerLevel level, IAllomancerData data) {
        int max = data.isEnhanced() ? 20 : 10;
        Vec3 negative = curPlayer.position().add(-max, -max, -max);
        Vec3 positive = curPlayer.position().add(max, max, max);
        int slowness_amplifier = data.isEnhanced() ? 255 : 2; // Duralumin freezes entities
        level.getEntitiesOfClass(LivingEntity.class, new AABB(negative, positive)).forEach(entity -> {
            ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER
                    .get()
                    .trigger(curPlayer, entity, Metal.CADMIUM, data.isEnhanced());
            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10, 0, true, false));
            if (entity != curPlayer) {
                entity.addEffect(
                        new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, slowness_amplifier, true, false));
            }
        });
    }
}
