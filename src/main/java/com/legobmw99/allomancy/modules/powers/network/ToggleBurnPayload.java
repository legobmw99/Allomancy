package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleBurnPayload(Metal metal, boolean on) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(Allomancy.MODID, "toggle_burn");

    public ToggleBurnPayload(FriendlyByteBuf buf) {
        this(buf.readEnum(Metal.class), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.metal);
        buf.writeBoolean(this.on);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
