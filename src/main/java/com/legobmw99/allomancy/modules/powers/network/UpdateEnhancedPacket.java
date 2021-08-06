package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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

    public static UpdateEnhancedPacket decode(FriendlyByteBuf buf) {
        return new UpdateEnhancedPacket(buf.readInt(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.enhance_time);
        buf.writeInt(this.entityID);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) { // Update player of own status
                Player player = (Player) ctx.get().getSender().level.getEntity(this.entityID);
                if (player != null) {
                    player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                        data.setEnhanced(this.enhance_time);
                        Network.sync(new UpdateEnhancedPacket(this.enhance_time, this.entityID), player);
                    });
                }
            } else {
                Player player = (Player) Minecraft.getInstance().level.getEntity(this.entityID);
                if (player != null) {
                    player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                        data.setEnhanced(this.enhance_time);
                    });
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
