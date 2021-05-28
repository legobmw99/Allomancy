package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.modules.powers.data.AllomancyCapability;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateEnhancedPacket {

    private final int enhance_time;
    private final int entityID;


    public UpdateEnhancedPacket(boolean enhanced, int entityID) {
        this.enhance_time = enhanced ? 100 : 0;
        this.entityID = entityID;
    }

    public UpdateEnhancedPacket(int enhance_time, int entityID) {
        this.enhance_time = enhance_time;
        this.entityID = entityID;
    }

    public static UpdateEnhancedPacket decode(PacketBuffer buf) {
        return new UpdateEnhancedPacket(buf.readInt(), buf.readInt());
    }

    public void encode(PacketBuffer buf) {
        buf.writeInt(this.enhance_time);
        buf.writeInt(this.entityID);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) { // Update player of own status
                PlayerEntity player = (PlayerEntity) ctx.get().getSender().level.getEntity(this.entityID);
                if (player != null) {
                    player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
                        data.setEnhanced(this.enhance_time);
                        Network.sync(new UpdateEnhancedPacket(this.enhance_time, this.entityID), player);
                    });
                }
            } else {
                PlayerEntity player = (PlayerEntity) Minecraft.getInstance().level.getEntity(this.entityID);
                if (player != null) {
                    player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
                        data.setEnhanced(this.enhance_time);
                    });
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
