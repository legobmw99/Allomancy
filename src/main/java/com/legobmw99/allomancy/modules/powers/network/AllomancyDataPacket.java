package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.api.IAllomancyData;
import com.legobmw99.allomancy.modules.powers.data.AllomancyCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AllomancyDataPacket {

    private final CompoundNBT nbt;
    private final UUID uuid;

    /**
     * Packet for sending Allomancy player data to a client
     *
     * @param data   the AllomancyCapability data for the player
     * @param player the player
     */
    public AllomancyDataPacket(IAllomancyData data, PlayerEntity player) {
        this.uuid = player.getUUID();
        this.nbt = (data != null && AllomancyCapability.PLAYER_CAP != null) ? (CompoundNBT) AllomancyCapability.PLAYER_CAP.writeNBT(data, null) : new CompoundNBT();

    }

    private AllomancyDataPacket(CompoundNBT nbt, UUID uuid) {
        this.nbt = nbt;
        this.uuid = uuid;
    }

    public static AllomancyDataPacket decode(PacketBuffer buf) {
        return new AllomancyDataPacket(buf.readNbt(), buf.readUUID());
    }

    public void encode(PacketBuffer buf) {
        buf.writeNbt(this.nbt);
        buf.writeUUID(this.uuid);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().level.getPlayerByUUID(this.uuid);

            if (player != null && AllomancyCapability.PLAYER_CAP != null) {
                player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(cap -> AllomancyCapability.PLAYER_CAP.readNBT(cap, null, this.nbt));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
