package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EmotionPayload(int entityID, boolean makeAggressive) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(Allomancy.MODID, "change_emotion");

    public EmotionPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeBoolean(this.makeAggressive);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
