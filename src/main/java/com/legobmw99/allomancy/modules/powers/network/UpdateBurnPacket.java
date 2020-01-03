package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.network.Network;
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

    public void encode(PacketBuffer buf) {
        buf.writeByte(this.mat);
        buf.writeBoolean(this.value);
    }

    public static UpdateBurnPacket decode(PacketBuffer buf) {
        return new UpdateBurnPacket(buf.readByte(), buf.readBoolean());
    }


    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);

            if (cap.getMetalAmounts(mat) > 0) {
                cap.setMetalBurning(mat, value);
            } else {
                cap.setMetalBurning(mat, false);
            }
            Network.sync(cap, player);

        });
        ctx.get().setPacketHandled(true);
    }
}
