package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EmotionPayload(int entityID, boolean makeAggressive) implements CustomPacketPayload {

    public static final Type<EmotionPayload> TYPE = new Type<>(Allomancy.rl("change_emotion"));
    public static final StreamCodec<ByteBuf, EmotionPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, EmotionPayload::entityID, ByteBufCodecs.BOOL,
                                  EmotionPayload::makeAggressive, EmotionPayload::new);

    @Override
    public Type<EmotionPayload> type() {
        return TYPE;
    }
}
