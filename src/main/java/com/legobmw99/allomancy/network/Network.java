package com.legobmw99.allomancy.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.network.packets.*;
import com.legobmw99.allomancy.util.AllomancyCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Network {

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Allomancy.MODID, "networking"), () -> "1.0", s -> true, s -> true);

    private static int index = 0;

    private static int nextIndex() {
        return index++;
    }

    public static void registerPackets() {
        INSTANCE.registerMessage(nextIndex(), AllomancyCapabilityPacket.class, AllomancyCapabilityPacket::encode, AllomancyCapabilityPacket::decode, AllomancyCapabilityPacket::handle);
        INSTANCE.registerMessage(nextIndex(), UpdateBurnPacket.class, UpdateBurnPacket::encode, UpdateBurnPacket::decode, UpdateBurnPacket::handle);
        INSTANCE.registerMessage(nextIndex(), ChangeEmotionPacket.class, ChangeEmotionPacket::encode, ChangeEmotionPacket::decode, ChangeEmotionPacket::handle);
        INSTANCE.registerMessage(nextIndex(), TryPushPullEntity.class, TryPushPullEntity::encode, TryPushPullEntity::decode, TryPushPullEntity::handle);
        INSTANCE.registerMessage(nextIndex(), TryPushPullBlock.class, TryPushPullBlock::encode, TryPushPullBlock::decode, TryPushPullBlock::handle);
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) {
            INSTANCE.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendTo(Object msg, PacketDistributor.PacketTarget target) {
        INSTANCE.send(target, msg);
    }

    public static void sync(PlayerEntity player) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        sync(cap, player);
    }

    public static void sync(AllomancyCapability cap, PlayerEntity player) {
        sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player));
    }

}
