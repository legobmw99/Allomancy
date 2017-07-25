package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyCapability;
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

	private int entityIDOther;

	/**
	 * Request the capabilities of another player
	 * 
	 * @param entityIDOther
	 *            the entity you are requesting the data of
	 * @param entityIDSender
	 *            the entity that is requesting
	 */
	public GetCapabilitiesPacket(int entityIDOther) {
		this.entityIDOther = entityIDOther;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityIDOther = ByteBufUtils.readVarInt(buf, 5);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, entityIDOther, 5);
	}

	public static class Handler implements IMessageHandler<GetCapabilitiesPacket, AllomancyCapabilityPacket> {
		private AllomancyCapability cap;

		@Override
		public AllomancyCapabilityPacket onMessage(final GetCapabilitiesPacket message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					
					Entity target = ctx.getServerHandler().player.world.getEntityByID(message.entityIDOther);
					if(target != null){
						cap = AllomancyCapability.forPlayer(target);
					} else {
						cap = null;
					}
				}
			});
			return new AllomancyCapabilityPacket(cap, message.entityIDOther);
		}
	}
}
