package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
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

	private int X;
	private int Y;
	private int Z;
	private int entityID;
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
	 * 			  the direction (1 for push, -1 for pull)
	 */
	public TryPushPullBlock(BlockPos block, int entityID, int direction) {
		this.X = block.getX();
		this.Y = block.getY();
		this.Z = block.getZ();
		this.entityID = entityID;
		this.direction = direction;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		X = ByteBufUtils.readVarInt(buf, 5);
		Y = ByteBufUtils.readVarInt(buf, 5);
		Z = ByteBufUtils.readVarInt(buf, 5);
		entityID = ByteBufUtils.readVarInt(buf, 5);
		direction = ByteBufUtils.readVarInt(buf, 5);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, X, 5);
		ByteBufUtils.writeVarInt(buf, Y, 5);
		ByteBufUtils.writeVarInt(buf, Z, 5);
		ByteBufUtils.writeVarInt(buf, entityID, 5);
		ByteBufUtils.writeVarInt(buf, direction, 5);

	}

	public static class Handler implements IMessageHandler<TryPushPullBlock, IMessage> {

		@Override
		public IMessage onMessage(final TryPushPullBlock message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Entity player = ctx.getServerHandler().player.world.getEntityByID(message.entityID);
					BlockPos block = new BlockPos(message.X,message.Y,message.Z);
					if (player == null) {
						return;
					} else {
						//Sanity check to make sure server has same configs
						if(AllomancyUtils.isBlockMetal(ctx.getServerHandler().player.world.getBlockState(block).getBlock())){
							AllomancyUtils.move(message.direction, player, block);
						}
					}
				}
			});
			return null;
		}
	}
}
