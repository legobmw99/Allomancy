package common.legobmw99.allomancy.network.packets;

import common.legobmw99.allomancy.common.AllomancyData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class BecomeMistbornPacket implements IMessage {
	
	public BecomeMistbornPacket(){
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	public static class Handler implements IMessageHandler<BecomeMistbornPacket, IMessage>{

		@Override
		public IMessage onMessage(BecomeMistbornPacket message, final MessageContext ctx) {
	        IThreadListener mainThread = Minecraft.getMinecraft();
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	            	EntityPlayer player =  Minecraft.getMinecraft().thePlayer;
	        		AllomancyData data;
	        		data = AllomancyData.forPlayer(player);
	        		data.isMistborn = true;
	            }
	        });		return null;
		}
	}
}
