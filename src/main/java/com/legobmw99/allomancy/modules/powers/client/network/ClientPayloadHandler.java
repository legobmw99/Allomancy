package com.legobmw99.allomancy.modules.powers.client.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.network.EnhanceTimePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPayloadHandler {

    private ClientPayloadHandler() {}

    private static void updateEnhanced(EnhanceTimePayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(payload.player());
            if (player != null) {
                AllomancerAttachment.get(player).setEnhanced(payload.enhanceTime());
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle client updateEnhanced", e);
            ctx.disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void registerClientPayloadHandlers(final RegisterClientPayloadHandlersEvent event) {
        event.register(EnhanceTimePayload.TYPE, ClientPayloadHandler::updateEnhanced);
    }
}
