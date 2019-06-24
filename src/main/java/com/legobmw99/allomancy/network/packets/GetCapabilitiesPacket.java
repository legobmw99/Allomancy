package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GetCapabilitiesPacket {

    private int entityIDOther;

    /**
     * Request the capabilities of another player
     *
     * @param entityIDOther  the entity you are requesting the data of
     */
    public GetCapabilitiesPacket(int entityIDOther) {
        this.entityIDOther = entityIDOther;
    }

    public static void encode(GetCapabilitiesPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.entityIDOther);
    }

    public static GetCapabilitiesPacket decode(PacketBuffer buf) {
        return new GetCapabilitiesPacket(buf.readInt());
    }


    public static class Handler {

        public static void handle(final GetCapabilitiesPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                        AllomancyCapability cap;
                        Entity target = ctx.get().getSender().world.getEntityByID(message.entityIDOther);
                        if (target != null) {
                            cap = AllomancyCapability.forPlayer(target);
                        } else {
                            cap = null;
                        }
                        Registry.NETWORK.reply(new AllomancyCapabilityPacket(cap, message.entityIDOther), ctx.get());
                    }
            );
        }
    }
}
