package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record AllomancerDataPayload(CompoundTag nbt, UUID player) implements CustomPacketPayload {

    public static final Type<AllomancerDataPayload> TYPE = new Type<>(Allomancy.rl("player_data"));

    public static final StreamCodec<ByteBuf, AllomancerDataPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, AllomancerDataPayload::nbt, UUIDUtil.STREAM_CODEC,
                                  AllomancerDataPayload::player, AllomancerDataPayload::new);

    public AllomancerDataPayload(ServerPlayer player) {
        this(player.getData(AllomancerAttachment.ALLOMANCY_DATA).serializeNBT(player.registryAccess()),
             player.getUUID());
    }

    @Override
    public Type<AllomancerDataPayload> type() {
        return TYPE;
    }
}
