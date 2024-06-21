package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BlockPushPullPayload(BlockPos block, int direction) implements CustomPacketPayload {
    public static final Type<BlockPushPullPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, "block_push_pull"));


    public static final StreamCodec<ByteBuf, BlockPushPullPayload> STREAM_CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, BlockPushPullPayload::block, ByteBufCodecs.INT,
                                  BlockPushPullPayload::direction, BlockPushPullPayload::new);


    public boolean isPush() {
        return this.direction > 0;
    }


    @Override
    public Type<BlockPushPullPayload> type() {
        return TYPE;
    }
}
