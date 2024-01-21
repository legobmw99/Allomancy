package com.legobmw99.allomancy.modules.powers.client.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.client.util.Sounds;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.network.AllomancerDataPayload;
import com.legobmw99.allomancy.modules.powers.network.EnhanceTimePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Arrays;

public class ClientPayloadHandler {

    public static void updateAllomancer(final AllomancerDataPayload payload, final PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(payload.player());
            if (player == Minecraft.getInstance().player) {
                var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
                long burningBefore = Arrays.stream(Metal.values()).filter(data::isBurning).count();
                data.deserializeNBT(payload.nbt());
                long burningAfter = Arrays.stream(Metal.values()).filter(data::isBurning).count();
                if (burningAfter < burningBefore) {
                    Sounds.soundForBurnChange(false);
                }
            } else {
                player.getData(AllomancerAttachment.ALLOMANCY_DATA).deserializeNBT(payload.nbt());
            }


        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle allomancerData update", e);
            ctx.packetHandler().disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void updateEnhanced(final EnhanceTimePayload payload, final PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            Entity e = Minecraft.getInstance().level.getEntity(payload.entityID());
            if (e instanceof Player player) {
                player.getData(AllomancerAttachment.ALLOMANCY_DATA).setEnhanced(payload.enhanceTime());
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle client updateEnhanced", e);
            ctx.packetHandler().disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }
}
