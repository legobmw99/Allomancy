package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.block.BlockIronLever;
import com.legobmw99.allomancy.block.IAllomanticallyActivatedBlock;
import com.legobmw99.allomancy.util.AllomancyUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TryPushPullBlock implements IMessage {
	public TryPushPullBlock() {
	}

	private long blockPos;
	private int direction;

	/**
	 * 
	 * @param X
	 *            the x-coordinate of the block
	 * @param Y
	 *            the y-coordinate of the block
	 * @param Z
	 *            the z-coordinate of the block
	 * @param entityID
	 *            the player's entityID
	 * @param direction
	 *            the direction (1 for push, -1 for pull)
	 */
	public TryPushPullBlock(BlockPos block, int direction) {
		this.blockPos = block.toLong();
		this.direction = direction;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		blockPos = buf.readLong();
		direction = ByteBufUtils.readVarInt(buf, 5);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(blockPos);
		ByteBufUtils.writeVarInt(buf, direction, 5);

	}

	public static class Handler implements IMessageHandler<TryPushPullBlock, IMessage> {

		@Override
		public IMessage onMessage(final TryPushPullBlock message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayerMP player = ctx.getServerHandler().player;
					BlockPos block = BlockPos.fromLong(message.blockPos);
					// Sanity check to make sure server has same configs and that the block is loaded in the server
					if (player.getEntityWorld().isBlockLoaded(block) && AllomancyUtils.isBlockMetal(ctx.getServerHandler().player.world.getBlockState(block).getBlock())) {
						if(player.world.getBlockState(block).getBlock() instanceof IAllomanticallyActivatedBlock){
							((IAllomanticallyActivatedBlock)player.world.getBlockState(block).getBlock())
								.onBlockActivatedAllomantically(player.world, block, player.world.getBlockState(block), player, message.direction == AllomancyUtils.PUSH);
						} else {
							AllomancyUtils.move(message.direction, player, block);
						}
					}
				}
			});
			return null;
		}
	}
}
