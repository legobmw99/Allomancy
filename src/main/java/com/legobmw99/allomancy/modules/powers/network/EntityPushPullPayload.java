package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EntityPushPullPayload(int entityID, int force) implements CustomPacketPayload {

    public static final Type<EntityPushPullPayload> TYPE = new Type<>(Allomancy.rl("entity_push_pull"));

    public static final StreamCodec<ByteBuf, EntityPushPullPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, EntityPushPullPayload::entityID, ByteBufCodecs.INT,
                                  EntityPushPullPayload::force, EntityPushPullPayload::new);


    public boolean isPush() {
        return this.force > 0;
    }

    @Override
    public Type<EntityPushPullPayload> type() {
        return TYPE;
    }
}
