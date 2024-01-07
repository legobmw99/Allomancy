package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record AllomancerDataPayload(CompoundTag nbt, UUID player) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(Allomancy.MODID, "player_data");


    public AllomancerDataPayload(ServerPlayer player) {
        this(player.getData(AllomancerAttachment.ALLOMANCY_DATA).serializeNBT(), player.getUUID());
    }

    public AllomancerDataPayload(final FriendlyByteBuf buf) {
        this(buf.readNbt(), buf.readUUID());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
        buf.writeUUID(this.player);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
