package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TryPushPullEntity implements IMessage {
	public TryPushPullEntity() {
	}

	private int entityIDOther;
	private int direction;

	/**
	 * Send a request to the server to use iron or steel on an entity
	 * 
	 * @param entityIDOther
	 *            the entity you are requesting the data of
	 * @param direction
	 *            the direction (1 for push, -1 for pull)
	 */
	public TryPushPullEntity(int entityIDOther, int direction) {
		this.entityIDOther = entityIDOther;
		this.direction = direction;

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityIDOther = ByteBufUtils.readVarInt(buf, 5);
		direction = ByteBufUtils.readVarInt(buf, 5);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, entityIDOther, 5);
		ByteBufUtils.writeVarInt(buf, direction, 5);

	}

	public static class Handler implements IMessageHandler<TryPushPullEntity, IMessage> {

		@Override
		public IMessage onMessage(final TryPushPullEntity message, final MessageContext ctx) {
			IThreadListener mainThread = (ServerWorld) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Entity target = ctx.getServerHandler().player.world.getEntityByID(message.entityIDOther);
					Entity player = ctx.getServerHandler().player;
					if (target == null) {
						return;
					} else {
						if (AllomancyUtils.isEntityMetal(target)) {
							// The player moves
							if (target instanceof IronGolemEntity || target instanceof ItemFrameEntity) {
								AllomancyUtils.move(message.direction, player, target.getPosition());

								// Depends if the minecart is filled
							} else if (target instanceof AbstractMinecartEntity) {
								if (target.isBeingRidden()) {
									if (target.isRidingOrBeingRiddenBy(player)) {
										//no op
									} else {
										AllomancyUtils.move(message.direction / 2.0, target, player.getPosition());
										AllomancyUtils.move(message.direction / 2.0, player, target.getPosition());
									}
								} else {
									AllomancyUtils.move(message.direction, target, player.getPosition());
								}
								// The target moves
							} else if (target instanceof ItemEntity) {
								AllomancyUtils.move(message.direction / 2.0, target, player.getPosition().down());

								// Split the difference
							} else {
								AllomancyUtils.move(message.direction / 2.0, target, player.getPosition());

								AllomancyUtils.move(message.direction / 2.0, player, target.getPosition());
							}
						}
					}
				}
			});
			return null;
		}
	}
}
