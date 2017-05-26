package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyCapabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SelectMetalPacket implements IMessage {
	private int metal;

	public SelectMetalPacket() {}

	public SelectMetalPacket(int metal) {
		this.metal = metal;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		metal = ByteBufUtils.readVarInt(buf, 5);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, metal, 5);
	}

	public static class Handler implements IMessageHandler<SelectMetalPacket, IMessage> {

		@Override
		public IMessage onMessage(final SelectMetalPacket message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world; // or
																									// Minecraft.getMinecraft()
																									// on
																									// the
																									// client
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayerMP player = ctx.getServerHandler().playerEntity;
					AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
					cap.setSelected((message.metal));
				}
			});
			return null;
		}
	}
}
