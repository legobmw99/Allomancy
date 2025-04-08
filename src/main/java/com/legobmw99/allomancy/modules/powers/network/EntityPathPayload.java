package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EntityPathPayload(int entityID, BlockPos pos) implements CustomPacketPayload {

    public static final Type<EntityPathPayload> TYPE = new Type<>(Allomancy.rl("entity_path"));
    public static final StreamCodec<ByteBuf, EntityPathPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, EntityPathPayload::entityID, BlockPos.STREAM_CODEC,
                                  EntityPathPayload::pos, EntityPathPayload::new);

    @Override
    public Type<EntityPathPayload> type() {
        return TYPE;
    }
}
