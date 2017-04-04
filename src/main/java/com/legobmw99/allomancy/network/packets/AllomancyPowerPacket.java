package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.common.AllomancyCapabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AllomancyPowerPacket implements IMessage {
	
    private int power;
	public AllomancyPowerPacket(){
		
	}
	public AllomancyPowerPacket(int pow){
	    this.power = pow;	
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
	    power = ByteBufUtils.readVarInt(buf, 5);

	}

	@Override
	public void toBytes(ByteBuf buf) {
	      ByteBufUtils.writeVarInt(buf, power, 5);

	}

	public static class Handler implements IMessageHandler<AllomancyPowerPacket, IMessage>{

		@Override
		public IMessage onMessage(final AllomancyPowerPacket message, final MessageContext ctx) {
	        IThreadListener mainThread = Minecraft.getMinecraft();
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	            	EntityPlayer player =  Minecraft.getMinecraft().player;
	            	AllomancyCapabilities cap;
	        		cap = AllomancyCapabilities.forPlayer(player);
	        		cap.setAllomancyPower(message.power);
	            }
	        });		return null;
		}
	}
}
