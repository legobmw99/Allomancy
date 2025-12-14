package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record EnhanceTimePayload(int enhanceTime, UUID player) implements CustomPacketPayload {

    public static final Type<EnhanceTimePayload> TYPE = new Type<>(Allomancy.id("enhance_tick"));

    public static final StreamCodec<ByteBuf, EnhanceTimePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, EnhanceTimePayload::enhanceTime, UUIDUtil.STREAM_CODEC,
                                  EnhanceTimePayload::player, EnhanceTimePayload::new);

    public EnhanceTimePayload(boolean enhanced, UUID player) {
        this(enhanced ? 100 : 0, player);
    }

    @Override
    public Type<EnhanceTimePayload> type() {
        return TYPE;
    }
}
