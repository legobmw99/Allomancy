package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.api.block.IAllomanticallyActivatedBlock;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.util.AllomancyUtils;
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

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(this.blockPos);
        buf.writeByte(this.direction);
    }

    public static TryPushPullBlock decode(PacketBuffer buf) {
        return new TryPushPullBlock(buf.readBlockPos(), buf.readByte());
    }


    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
                    ServerPlayerEntity player = ctx.get().getSender();
                    BlockPos pos = blockPos;
                    // Sanity check to make sure server has same configs and that the block is loaded in the server
                    if ((player.world.isBlockLoaded(pos) && (AllomancyUtils.isBlockMetal(player.world.getBlockState(pos).getBlock()))) // Check Block
                            || (player.getHeldItemMainhand().getItem() == CombatSetup.COIN_BAG.get() && (!player.findAmmo(player.getHeldItemMainhand()).isEmpty()) /*some sort of find ammo func*/ &&
                            direction == AllomancyUtils.PUSH)) {
                        // Check for the coin bag
                        if (player.world.getBlockState(pos).getBlock() instanceof IAllomanticallyActivatedBlock) {
                            ((IAllomanticallyActivatedBlock) player.world.getBlockState(pos).getBlock())
                                    .onBlockActivatedAllomantically(player.world.getBlockState(pos), player.world, pos, player, direction == AllomancyUtils.PUSH);
                        } else {
                            AllomancyUtils.move(direction, player, pos);
                        }
                    }
                }
        );
        ctx.get().setPacketHandled(true);
    }
}
