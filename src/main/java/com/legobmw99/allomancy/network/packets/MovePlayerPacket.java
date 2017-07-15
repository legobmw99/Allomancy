package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyCapabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MovePlayerPacket implements IMessage {
	
	public MovePlayerPacket(){
		
	}
	private double motionX;
	private double motionY;
	private double motionZ;

	/**
	 * 
	 * @param motionX
	 *            the motion to be applied in the x-direction
	 * @param motionY
	 *            the motion to be applied in the y-direction
	 * @param motionZ
	 *            the motion to be applied in the z-direction
	 * @param entityID
	 *            the entity to be moved
	 */
	public MovePlayerPacket(double motionX, double motionY, double motionZ) {
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		// Because floats aren't applicable, divide to get decimals back
		motionX = ((double) ByteBufUtils.readVarInt(buf, 5)) / 100;
		motionY = ((double) ByteBufUtils.readVarInt(buf, 5)) / 100;
		motionZ = ((double) ByteBufUtils.readVarInt(buf, 5)) / 100;

	}

	@Override
	public void toBytes(ByteBuf buf) {

		// Because floats aren't applicable, multiply to get some decimals
		ByteBufUtils.writeVarInt(buf, (int) (motionX * 100), 5);
		ByteBufUtils.writeVarInt(buf, (int) (motionY * 100), 5);
		ByteBufUtils.writeVarInt(buf, (int) (motionZ * 100), 5);
	}


	public static class Handler implements IMessageHandler<MovePlayerPacket, IMessage>{

		@Override
		public IMessage onMessage(final MovePlayerPacket message, final MessageContext ctx) {
	        IThreadListener mainThread = Minecraft.getMinecraft();
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	            	EntityPlayer player =  Minecraft.getMinecraft().player;
	            	player.motionX = message.motionX;
	            	player.motionY = message.motionY;
	            	player.motionZ = message.motionZ;

	            }
	        });		return null;
		}
	}
}
