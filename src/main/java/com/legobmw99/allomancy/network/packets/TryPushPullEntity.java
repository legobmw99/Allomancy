package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TryPushPullEntity implements IMessage {
	public TryPushPullEntity() {
	}

	private int entityIDSender;
	private int entityIDOther;
	private int direction;

	/**
	 * 
	 * @param entityIDOther
	 *            the entity you are requesting the data of
	 * @param entityIDSender
	 *            the entity that is requesting
	 * @param direction
	 *            the direction (1 for push, -1 for pull)
	 */
	public TryPushPullEntity(int entityIDOther, int entityIDSender, int direction) {
		this.entityIDOther = entityIDOther;
		this.entityIDSender = entityIDSender;
		this.direction = direction;

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityIDSender = ByteBufUtils.readVarInt(buf, 5);
		entityIDOther = ByteBufUtils.readVarInt(buf, 5);
		direction = ByteBufUtils.readVarInt(buf, 5);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, entityIDSender, 5);
		ByteBufUtils.writeVarInt(buf, entityIDOther, 5);
		ByteBufUtils.writeVarInt(buf, direction, 5);

	}

	public static class Handler implements IMessageHandler<TryPushPullEntity, IMessage> {

		@Override
		public IMessage onMessage(final TryPushPullEntity message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Entity target = ctx.getServerHandler().player.world.getEntityByID(message.entityIDOther);
					Entity player = ctx.getServerHandler().player.world.getEntityByID(message.entityIDSender);
					BlockPos anchor;
					if (target == null || player == null) {
						return;
					} else {
						if (AllomancyUtils.isEntityMetal(target)) {
							// Pull you toward the entity
							if (target instanceof EntityIronGolem) {
								anchor = new BlockPos((int) target.posX, (int) target.posY, (int) target.posZ);
								AllomancyUtils.move(message.direction, player, anchor);

								// Pull the entity toward you
							} else if (target instanceof EntityItem) {
								anchor = new BlockPos((int) player.posX, (int) player.posY - 1, (int) player.posZ);
								AllomancyUtils.move(message.direction / 2.0, target, anchor);
							} else {
								anchor = new BlockPos((int) player.posX, (int) player.posY, (int) player.posZ);
								AllomancyUtils.move(message.direction, target, anchor);
							}
						}
					}
				}
			});
			return null;
		}
	}
}
