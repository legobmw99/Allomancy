package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record ToggleBurnPayload(Metal metal, boolean on) implements CustomPacketPayload {
    public static final Type<ToggleBurnPayload> TYPE = new Type<>(Allomancy.rl("toggle_burn"));

    public static final StreamCodec<FriendlyByteBuf, ToggleBurnPayload> STREAM_CODEC =
            StreamCodec.composite(NeoForgeStreamCodecs.enumCodec(Metal.class), ToggleBurnPayload::metal,
                                  ByteBufCodecs.BOOL, ToggleBurnPayload::on, ToggleBurnPayload::new);

    @Override
    public Type<ToggleBurnPayload> type() {
        return TYPE;
    }
}
