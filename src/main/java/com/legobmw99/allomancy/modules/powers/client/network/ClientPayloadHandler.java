package com.legobmw99.allomancy.modules.powers.client.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.client.util.Sounds;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.network.AllomancerDataPayload;
import com.legobmw99.allomancy.modules.powers.network.EnhanceTimePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Arrays;

public final class ClientPayloadHandler {

    private ClientPayloadHandler() {}

    private static void updateAllomancer(AllomancerDataPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(payload.player());
            if (player != null) {
                long burningBefore = Arrays
                        .stream(Metal.values())
                        .filter(player.getData(AllomancerAttachment.ALLOMANCY_DATA)::isBurning)
                        .count();

                long burningAfter = Arrays.stream(Metal.values()).filter(payload.data()::isBurning).count();
                if (player == Minecraft.getInstance().player && burningAfter < burningBefore) {
                    Sounds.soundForBurnChange(false);
                }
                player.setData(AllomancerAttachment.ALLOMANCY_DATA, payload.data());
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle allomancerData update", e);
            ctx.disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }

    private static void updateEnhanced(EnhanceTimePayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(payload.player());
            if (player != null) {
                player.getData(AllomancerAttachment.ALLOMANCY_DATA).setEnhanced(payload.enhanceTime());
            }
        }).exceptionally(e -> {
            Allomancy.LOGGER.error("Failed to handle client updateEnhanced", e);
            ctx.disconnect(Component.translatable("allomancy.networking.failed", e.getMessage()));
            return null;
        });
    }

    public static void registerClientPayloadHandlers(final RegisterClientPayloadHandlersEvent event) {
        event.register(EnhanceTimePayload.TYPE, ClientPayloadHandler::updateEnhanced);
        event.register(AllomancerDataPayload.TYPE, ClientPayloadHandler::updateAllomancer);
    }
}
