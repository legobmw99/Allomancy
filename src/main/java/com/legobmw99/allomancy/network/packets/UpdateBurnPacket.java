package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class UpdateBurnPacket {

    private byte mat;
    private boolean value;

    /**
     * Send request to the server to change the burning state of a metal
     *
     * @param mat   the index of the metal
     * @param value whether or not it is burning
     */
    public UpdateBurnPacket(byte mat, boolean value) {
        this.mat = mat;
        this.value = value; // Convert bool to int
    }

    public static void encode(UpdateBurnPacket pkt, PacketBuffer buf) {
        buf.writeByte(pkt.mat);
        buf.writeBoolean(pkt.value);
    }

    public static UpdateBurnPacket decode(PacketBuffer buf) {
        return new UpdateBurnPacket(buf.readByte(), buf.readBoolean());
    }


    public static class Handler {

        public static void handle(final UpdateBurnPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {

                ServerPlayerEntity player = ctx.get().getSender();
                AllomancyCapability cap = AllomancyCapability.forPlayer(player);

                if (cap.getMetalAmounts(message.mat) != 0) {
                    cap.setMetalBurning(message.mat, message.value);
                } else {
                    cap.setMetalBurning(message.mat, false);
                }

            });
        }
    }
}
