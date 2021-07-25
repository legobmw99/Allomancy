package com.legobmw99.allomancy.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.modules.powers.network.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Network {

    private static final String VERSION = "1.1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Allomancy.MODID, "networking"), () -> VERSION, VERSION::equals,
                                                                                  VERSION::equals);

    private static int index = 0;

    private static int nextIndex() {
        return index++;
    }

    public static void registerPackets() {
        INSTANCE.registerMessage(nextIndex(), AllomancerDataPacket.class, AllomancerDataPacket::encode, AllomancerDataPacket::decode, AllomancerDataPacket::handle);
        INSTANCE.registerMessage(nextIndex(), UpdateBurnPacket.class, UpdateBurnPacket::encode, UpdateBurnPacket::decode, UpdateBurnPacket::handle);
        INSTANCE.registerMessage(nextIndex(), ChangeEmotionPacket.class, ChangeEmotionPacket::encode, ChangeEmotionPacket::decode, ChangeEmotionPacket::handle);
        INSTANCE.registerMessage(nextIndex(), TryPushPullEntity.class, TryPushPullEntity::encode, TryPushPullEntity::decode, TryPushPullEntity::handle);
        INSTANCE.registerMessage(nextIndex(), TryPushPullBlock.class, TryPushPullBlock::encode, TryPushPullBlock::decode, TryPushPullBlock::handle);
        INSTANCE.registerMessage(nextIndex(), UpdateEnhancedPacket.class, UpdateEnhancedPacket::encode, UpdateEnhancedPacket::decode, UpdateEnhancedPacket::handle);
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) {
            INSTANCE.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendTo(Object msg, PacketDistributor.PacketTarget target) {
        INSTANCE.send(target, msg);
    }

    public static void sync(PlayerEntity player) {
        player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> sync(data, player));
    }

    public static void sync(IAllomancerData cap, PlayerEntity player) {
        sync(new AllomancerDataPacket(cap, player), player);
    }

    public static void sync(Object msg, PlayerEntity player) {
        sendTo(msg, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player));
    }

}
