package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyCapabilities;
import com.legobmw99.allomancy.util.Registry;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GetCapabilitiesPacket implements IMessage {
	public GetCapabilitiesPacket() {
	}

	private int entityIDSender;
	private int entityIDOther;

	/**
	 * Request the capabilities of another player
	 * 
	 * @param entityIDOther
	 *            the entity you are requesting the data of
	 * @param entityIDSender
	 *            the entity that is requesting
	 */
	public GetCapabilitiesPacket(int entityIDOther, int entityIDSender) {
		this.entityIDOther = entityIDOther;
		this.entityIDSender = entityIDSender;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityIDSender = ByteBufUtils.readVarInt(buf, 5);
		entityIDOther = ByteBufUtils.readVarInt(buf, 5);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, entityIDSender, 5);
		ByteBufUtils.writeVarInt(buf, entityIDOther, 5);
	}

	public static class Handler implements IMessageHandler<GetCapabilitiesPacket, IMessage> {
		@Override
		public IMessage onMessage(final GetCapabilitiesPacket message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Entity target = ctx.getServerHandler().player.world.getEntityByID(message.entityIDOther);
					EntityPlayer player = (EntityPlayer) ctx.getServerHandler().player.world.getEntityByID(message.entityIDSender);
					AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(target);
					
					Registry.network.sendTo(new AllomancyCapabiltiesPacket(cap, message.entityIDOther),(EntityPlayerMP) player);
				}
			});
			return null;
		}
	}
}
