package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.setup.Metal;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class UpdateBurnPacket {

    private Metal mt;
    private boolean value;

    /**
     * Send request to the server to change the burning state of a metal
     *
     * @param mt    the metal
     * @param value whether or not it is burning
     */
    public UpdateBurnPacket(Metal mt, boolean value) {
        this.mt = mt;
        this.value = value; // Convert bool to int
    }

    public void encode(PacketBuffer buf) {
        buf.writeEnumValue(mt);
        buf.writeBoolean(this.value);
    }

    public static UpdateBurnPacket decode(PacketBuffer buf) {
        return new UpdateBurnPacket(buf.readEnumValue(Metal.class), buf.readBoolean());
    }


    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);

            if (cap.hasPower(mt) && cap.getAmount(mt) > 0) {
                cap.setBurning(mt, value);
            } else {
                cap.setBurning(mt, false);
            }
            Network.sync(cap, player);

        });
        ctx.get().setPacketHandled(true);
    }
}
