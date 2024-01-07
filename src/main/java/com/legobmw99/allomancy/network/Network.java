package com.legobmw99.allomancy.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.ServerPayloadHandler;
import com.legobmw99.allomancy.modules.powers.client.ClientPayloadHandler;
import com.legobmw99.allomancy.modules.powers.network.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;


public class Network {
    public static void registerPayloads(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(Allomancy.MODID).versioned("2.0");

        registrar.play(AllomancerDataPayload.ID, AllomancerDataPayload::new, handler -> handler.client(ClientPayloadHandler::handleAllomancyData));
        registrar.play(EmotionPayload.ID, EmotionPayload::new, handler -> handler.server(ServerPayloadHandler::handleEmotionChange));
        registrar.play(BlockPushPullPayload.ID, BlockPushPullPayload::new, handler -> handler.server(ServerPayloadHandler::tryPushPullBlock));
        registrar.play(EntityPushPullPayload.ID, EntityPushPullPayload::new, handler -> handler.server(ServerPayloadHandler::tryPushPullEntity));
        registrar.play(ToggleBurnPayload.ID, ToggleBurnPayload::new, handler -> handler.server(ServerPayloadHandler::toggleBurnRequest));
        registrar.play(EnhanceTimePayload.ID, EnhanceTimePayload::new,
                       handler -> handler.server(ServerPayloadHandler::updateEnhanced).client(ClientPayloadHandler::updateEnhanced));

    }

    public static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.SERVER.noArg().send(msg);
    }
    public static void syncAllomancerData(ServerPlayer player) {
        sync(new AllomancerDataPayload(player), player);
    }
    public static void sync(CustomPacketPayload msg, ServerPlayer player) {
        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(player).send(msg);
    }
}
