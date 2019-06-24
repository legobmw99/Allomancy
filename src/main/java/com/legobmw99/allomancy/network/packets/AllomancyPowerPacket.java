package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.AllomancyCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AllomancyPowerPacket {

    private byte power;

    /**
     * Packet for sending just an Allomancy power number to a client
     *
     * @param pow the power
     */
    public AllomancyPowerPacket(byte pow) {
        this.power = pow;
    }


    public static void encode(AllomancyPowerPacket pkt, PacketBuffer buf) {
        buf.writeByte(pkt.power);
    }

    public static AllomancyPowerPacket decode(PacketBuffer buf) {
        return new AllomancyPowerPacket(buf.readByte());
    }


    public static class Handler {

        public static void handle(final AllomancyPowerPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                PlayerEntity player = Allomancy.proxy.getClientPlayer();
                AllomancyCapability cap;
                cap = AllomancyCapability.forPlayer(player);
                cap.setAllomancyPower(message.power);
            });
        }
    }
}
