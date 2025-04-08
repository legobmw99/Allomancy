package com.legobmw99.allomancy.modules.powers.client.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.client.util.Sounds;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.network.AllomancerDataPayload;
import com.legobmw99.allomancy.modules.powers.network.EnhanceTimePayload;
import com.legobmw99.allomancy.modules.powers.network.EntityPathPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;

public final class ClientPayloadHandler {

    private ClientPayloadHandler() {}

    public static void updateAllomancer(AllomancerDataPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(payload.player());
            if (player == Minecraft.getInstance().player) {
                var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
                long burningBefore = Arrays.stream(Metal.values()).filter(data::isBurning).count();
                data.deserializeNBT(ctx.player().registryAccess(), payload.nbt());
                long burningAfter = Arrays.stream(Metal.values()).filter(data::isBurning).count();
                if (burningAfter < burningBefore) {
                    Sounds.soundForBurnChange(false);
                }
            } else if (player != null) {
                player
                        .getData(AllomancerAttachment.ALLOMANCY_DATA)
                        .deserializeNBT(ctx.player().registryAccess(), payload.nbt());
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle allomancerData update", e);
            ctx.disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void updateEnhanced(EnhanceTimePayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Entity e = Minecraft.getInstance().level.getEntity(payload.entityID());
            if (e instanceof Player player) {
                player.getData(AllomancerAttachment.ALLOMANCY_DATA).setEnhanced(payload.enhanceTime());
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle client updateEnhanced", e);
            ctx.disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void takePath(EntityPathPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Mob mob = (Mob) Minecraft.getInstance().level.getEntity(payload.entityID());
            if (mob != null) {
                Minecraft.getInstance().level.addParticle(ParticleTypes.DUST_PLUME, payload.pos().getX() + 0.5,
                                                          payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5, 0,
                                                          0, 0);
            }
        });
    }
}
