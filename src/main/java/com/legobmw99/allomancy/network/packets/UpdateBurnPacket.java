package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.common.AllomancyCapabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateBurnPacket implements IMessage {

	public UpdateBurnPacket() {
	}

	private int mat;
	private int value;

	/**
	 * Takes data, makes it transmittable
	 * 
	 * @param mat
	 *            the index of the metal
	 * @param value
	 *            whether or not it is burning
	 */
	public UpdateBurnPacket(int mat, boolean value) {
		this.mat = mat;
		this.value = value ? 1 : 0; // Convert bool to int
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		mat = ByteBufUtils.readVarInt(buf, 5);
		value = ByteBufUtils.readVarInt(buf, 1);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, mat, 5);
		ByteBufUtils.writeVarInt(buf, value, 1);
	}

	public static class Handler implements IMessageHandler<UpdateBurnPacket, IMessage> {

		@Override
		public IMessage onMessage(final UpdateBurnPacket message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {

					EntityPlayerMP player = ctx.getServerHandler().playerEntity;
					AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
					boolean value;
					if (message.value == 1) { // Convert int back to bool
						value = true;
					} else {
						value = false;
					}
					if (cap.getMetalAmounts(message.mat) != 0) {
						cap.setMetalBurning(message.mat, value);
					} else {
						cap.setMetalBurning(message.mat, false);
					}
				}
			});
			return null;
		}
	}
}
