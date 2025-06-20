package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.data.AllomancerData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record AllomancerDataPayload(AllomancerData data, UUID player) implements CustomPacketPayload {

    public static final Type<AllomancerDataPayload> TYPE = new Type<>(Allomancy.rl("player_data"));

    public static final StreamCodec<FriendlyByteBuf, AllomancerDataPayload> STREAM_CODEC =
            StreamCodec.composite(AllomancerData.STREAM_CODEC, AllomancerDataPayload::data, UUIDUtil.STREAM_CODEC,
                                  AllomancerDataPayload::player, AllomancerDataPayload::new);

    public AllomancerDataPayload(ServerPlayer player) {
        this(player.getData(AllomancerAttachment.ALLOMANCY_DATA), player.getUUID());
    }

    @Override
    public Type<AllomancerDataPayload> type() {
        return TYPE;
    }
}
