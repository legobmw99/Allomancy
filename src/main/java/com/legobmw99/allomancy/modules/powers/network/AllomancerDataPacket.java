package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AllomancerDataPacket {

    private final CompoundNBT nbt;
    private final UUID uuid;

    /**
     * Packet for sending Allomancy player data to a client
     *
     * @param data   the AllomancerCapability data for the player
     * @param player the player
     */
    public AllomancerDataPacket(IAllomancerData data, PlayerEntity player) {
        this.uuid = player.getUUID();
        this.nbt = (data != null && AllomancerCapability.PLAYER_CAP != null) ? (CompoundNBT) AllomancerCapability.PLAYER_CAP.writeNBT(data, null) : new CompoundNBT();

    }

    private AllomancerDataPacket(CompoundNBT nbt, UUID uuid) {
        this.nbt = nbt;
        this.uuid = uuid;
    }

    public static AllomancerDataPacket decode(PacketBuffer buf) {
        return new AllomancerDataPacket(buf.readNbt(), buf.readUUID());
    }

    public void encode(PacketBuffer buf) {
        buf.writeNbt(this.nbt);
        buf.writeUUID(this.uuid);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().level.getPlayerByUUID(this.uuid);

            if (player != null && AllomancerCapability.PLAYER_CAP != null) {
                player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(cap -> AllomancerCapability.PLAYER_CAP.readNBT(cap, null, this.nbt));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
