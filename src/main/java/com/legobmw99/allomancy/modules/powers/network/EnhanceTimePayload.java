package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EnhanceTimePayload(int enhanceTime, int entityID) implements CustomPacketPayload {

    public static final Type<EnhanceTimePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, "enhance_tick"));

    public static final StreamCodec<ByteBuf, EnhanceTimePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, EnhanceTimePayload::enhanceTime, ByteBufCodecs.INT,
                                  EnhanceTimePayload::entityID, EnhanceTimePayload::new);

    public EnhanceTimePayload(boolean enhanced, int entityID) {
        this(enhanced ? 100 : 0, entityID);
    }

    @Override
    public Type<EnhanceTimePayload> type() {
        return TYPE;
    }
}
