package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import common.legobmw99.allomancy.common.AllomancyCapabilities;

public class BecomeMistbornPacket implements IMessage {
	
	public BecomeMistbornPacket(){
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
	}

	public static class Handler implements IMessageHandler<BecomeMistbornPacket, IMessage>{

		@Override
		public IMessage onMessage(BecomeMistbornPacket message, final MessageContext ctx) {
	        IThreadListener mainThread = Minecraft.getMinecraft();
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	            	EntityPlayer player =  Minecraft.getMinecraft().thePlayer;
	            	AllomancyCapabilities cap;
	        		cap = AllomancyCapabilities.forPlayer(player);
	        		cap.setMistborn(true);
	            }
	        });		return null;
		}
	}
}
