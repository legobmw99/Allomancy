package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


public final class Network {
    public static void register(IEventBus bus) {
        bus.addListener(Network::registerPayloads);
    }

    private static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Allomancy.MODID).versioned("3.1");


        registrar.playToServer(EmotionPayload.TYPE, EmotionPayload.STREAM_CODEC, ServerPayloadHandler::changeEmotion);
        registrar.playToServer(BlockPushPullPayload.TYPE, BlockPushPullPayload.STREAM_CODEC,
                               ServerPayloadHandler::tryPushPullBlock);
        registrar.playToServer(EntityPushPullPayload.TYPE, EntityPushPullPayload.STREAM_CODEC,
                               ServerPayloadHandler::tryPushPullEntity);

        registrar.playToServer(ToggleBurnPayload.TYPE, ToggleBurnPayload.STREAM_CODEC,
                               ServerPayloadHandler::toggleBurnRequest);

        registrar.playBidirectional(EnhanceTimePayload.TYPE, EnhanceTimePayload.STREAM_CODEC,
                                    ServerPayloadHandler::updateEnhanced);

    }

    //    public static void syncAllomancerData(ServerPlayer player) {
    //        sync(new AllomancerDataPayload(player), player);
    //    }

    public static void sync(CustomPacketPayload msg, ServerPlayer player) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, msg);
    }

    private Network() {}
}
