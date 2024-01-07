package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EntityPushPullPayload (int entityID, int direction) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(Allomancy.MODID, "entity_push_pull");

    public EntityPushPullPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeInt(this.direction);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
