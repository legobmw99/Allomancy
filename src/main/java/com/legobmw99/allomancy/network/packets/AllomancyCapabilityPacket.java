package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.AllomancyCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AllomancyCapabilityPacket {

    private CompoundNBT nbt;
    private int entityID;

    /**
     * Packet for sending Allomancy player data to a client
     *
     * @param data     the AllomancyCapabiltiy data for the player
     * @param entityID the player's ID
     */
    public AllomancyCapabilityPacket(AllomancyCapability data, int entityID) {
        this(data.serializeNBT() != null ? data.serializeNBT() : new AllomancyCapability().serializeNBT(), entityID);
    }

    private AllomancyCapabilityPacket(CompoundNBT data, int entityID) {
        this.nbt = data;
        this.entityID = entityID;
    }

    public static void encode(AllomancyCapabilityPacket pkt, PacketBuffer buf) {
        buf.writeCompoundTag(pkt.nbt);
        buf.writeInt(pkt.entityID);
    }

    public static AllomancyCapabilityPacket decode(PacketBuffer buf) {
        return new AllomancyCapabilityPacket(buf.readCompoundTag(), buf.readInt());
    }

    public static class Handler {

        public static void handle(final AllomancyCapabilityPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                PlayerEntity player = (PlayerEntity) Allomancy.proxy.getClientWorld().getEntityByID(message.entityID);
                if (player != null) {
                    AllomancyCapability playerCap = AllomancyCapability.forPlayer(player);
                    playerCap.deserializeNBT(message.nbt);
                }
            });
        }
    }
}
