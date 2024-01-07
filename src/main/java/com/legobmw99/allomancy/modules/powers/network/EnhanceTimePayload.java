package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EnhanceTimePayload(int enhanceTime, int entityID) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(Allomancy.MODID, "enhance_tick");

    public EnhanceTimePayload(boolean enhanced, int entityID) {
        this(enhanced ? 100 : 0, entityID);
    }

    public EnhanceTimePayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.enhanceTime);
        buf.writeInt(this.entityID);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
