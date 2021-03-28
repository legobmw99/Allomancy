package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.api.block.IAllomanticallyUsableBlock;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.util.PowerUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TryPushPullBlock {

    private final BlockPos blockPos;
    private final int direction;

    /**
     * Send a request to the server to use iron or steel on a block
     *
     * @param block     the block
     * @param direction the direction (1 for push, -1 for pull)
     */
    public TryPushPullBlock(BlockPos block, int direction) {
        this.blockPos = block;
        this.direction = direction;
    }

    public static TryPushPullBlock decode(PacketBuffer buf) {
        return new TryPushPullBlock(buf.readBlockPos(), buf.readInt());
    }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(this.blockPos);
        buf.writeInt(this.direction);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            BlockPos pos = this.blockPos;
            // Sanity check to make sure server has same configs and that the block is loaded in the server
            if ((player.level.hasChunkAt(pos) && (PowerUtils.isBlockStateMetal(player.level.getBlockState(pos)))) // Check Block
                || (player.getMainHandItem().getItem() == CombatSetup.COIN_BAG.get() && (!player.getProjectile(player.getMainHandItem()).isEmpty()) && this.direction > 0)) {
                // Check for the coin bag
                if (player.level.getBlockState(pos).getBlock() instanceof IAllomanticallyUsableBlock) {
                    ((IAllomanticallyUsableBlock) player.level.getBlockState(pos).getBlock()).useAllomantically(player.level.getBlockState(pos), player.level, pos,
                                                                                                                player, this.direction > 0);
                } else {
                    PowerUtils.move(this.direction, player, pos);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
