package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BlockPushPullPayload(BlockPos block, int direction) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(Allomancy.MODID, "block_push_pull");

    public BlockPushPullPayload(FriendlyByteBuf buf){
        this(buf.readBlockPos(), buf.readInt());
    }
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.block);
        buf.writeInt(this.direction);
    }

    public boolean isPush(){
        return this.direction > 0;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
