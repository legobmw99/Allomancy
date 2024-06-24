package com.legobmw99.allomancy.modules.powers.client.network;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.client.util.Sounds;
import com.legobmw99.allomancy.modules.powers.network.*;
import com.legobmw99.allomancy.modules.powers.util.Physical;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;

public final class PowerRequests {
    private PowerRequests() {}

    /**
     * Used to toggle a metal's burn state and play a sound effect
     *
     * @param metal the index of the metal to toggle
     * @param data  the Allomancer data of the player
     */
    public static void toggleBurn(Metal metal, IAllomancerData data) {
        if (!data.hasPower(metal)) {
            return;
        }

        if (data.getStored(metal) > 0) {
            data.setBurning(metal, !data.isBurning(metal));
        }

        sendToServer(new ToggleBurnPayload(metal, data.isBurning(metal)));

        Sounds.soundForBurnChange(data.isBurning(metal));
    }

    public static void emotionPushPull(IAllomancerData data, HitResult trace, Metal metal) {
        if (!data.isBurning(metal) || trace == null) {
            return;
        }

        boolean aggro = metal == Metal.ZINC;

        if (trace.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) trace).getEntity();
            if (entity instanceof PathfinderMob) {
                sendToServer(new EmotionPayload(entity.getId(), aggro));
            }
        }
    }

    public static void metallicPushPull(IAllomancerData data, HitResult trace, Metal metal) {
        if (!data.isBurning(metal) || trace == null) {
            return;
        }

        int force = (data.isEnhanced() ? 4 : 1) * (metal == Metal.STEEL ? 1 : -1);

        switch (trace) {
            case EntityHitResult e: {
                if (Physical.isEntityMetallic((e).getEntity())) {
                    sendToServer(new EntityPushPullPayload((e).getEntity().getId(), force));

                }
                break;
            }
            case BlockHitResult b: {
                BlockPos bp = b.getBlockPos();
                Player player = Minecraft.getInstance().player;
                if (Physical.isBlockStateMetallic(player.level().getBlockState(bp)) ||
                    (player.isCrouching() && metal == Metal.STEEL &&
                     player.getMainHandItem().getItem() == CombatSetup.COIN_BAG.get() &&
                     (!player.getProjectile(player.getMainHandItem()).isEmpty()))) {
                    sendToServer(new BlockPushPullPayload(bp, force));
                }
            }
            break;
            default:
        }
    }

    public static void nicrosilEnhance(IAllomancerData data, HitResult trace) {
        if (data.isBurning(Metal.NICROSIL)) {
            if ((trace != null) && (trace.getType() == HitResult.Type.ENTITY)) {
                Entity entity = ((EntityHitResult) trace).getEntity();
                if (entity instanceof Player) {
                    sendToServer(new EnhanceTimePayload(true, entity.getId()));
                }
            }
        }
    }

    private static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.sendToServer(msg);
    }
}
