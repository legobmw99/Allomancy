package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.block.IAllomanticallyActivatedBlock;
import com.legobmw99.allomancy.util.AllomancyUtils;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TryPushPullBlock {

    private BlockPos blockPos;
    private byte direction;

    /**
     * Send a request to the server to use iron or steel on a block
     *
     * @param block     the block
     * @param direction the direction (1 for push, -1 for pull)
     */
    public TryPushPullBlock(BlockPos block, byte direction) {
        this.blockPos = block;
        this.direction = direction;
    }

    public static void encode(TryPushPullBlock pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.blockPos);
        buf.writeByte(pkt.direction);
    }

    public static TryPushPullBlock decode(PacketBuffer buf) {
        return new TryPushPullBlock(buf.readBlockPos(), buf.readByte());
    }


    public static void handle(final TryPushPullBlock message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
                    ServerPlayerEntity player = ctx.get().getSender();
                    BlockPos pos = message.blockPos;
                    // Sanity check to make sure server has same configs and that the block is loaded in the server
                    if ((player.world.isBlockLoaded(pos) && (AllomancyUtils.isBlockMetal(player.world.getBlockState(pos).getBlock()))) // Check Block
                            || (player.getHeldItemMainhand().getItem() == Registry.coin_bag && (!player.findAmmo(player.getHeldItemMainhand()).isEmpty()) /*some sort of find ammo func*/ &&
                            message.direction == AllomancyUtils.PUSH)) {
                        // Check for the coin bag
                        if (player.world.getBlockState(pos).getBlock() instanceof IAllomanticallyActivatedBlock) {
                            ((IAllomanticallyActivatedBlock) player.world.getBlockState(pos).getBlock())
                                    .onBlockActivatedAllomantically(player.world.getBlockState(pos), player.world, pos, player, message.direction == AllomancyUtils.PUSH);
                        } else {
                            AllomancyUtils.move(message.direction, player, pos);
                        }
                    }
                }
        );
    }
}
