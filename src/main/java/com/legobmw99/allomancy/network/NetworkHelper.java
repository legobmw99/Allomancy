package com.legobmw99.allomancy.network;

import com.legobmw99.allomancy.network.packets.AllomancyCapabilityPacket;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

public class NetworkHelper {

    public static void sendToServer(Object msg) {
        Registry.NETWORK.sendToServer(msg);
    }


    public static void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) {
            Registry.NETWORK.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendTo(Object msg, PacketDistributor.PacketTarget target) {
        Registry.NETWORK.send(target, msg);
    }

    public static void sync(PlayerEntity playerIn) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(playerIn);
        sendTo(new AllomancyCapabilityPacket(cap, playerIn.getEntityId()), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerIn));
    }

}
