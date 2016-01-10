package common.legobmw99.allomancy.network.packets;

import common.legobmw99.allomancy.common.AllomancyData;

import io.netty.buffer.ByteBuf;
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
	        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	            	EntityPlayerMP player = ctx.getServerHandler().playerEntity;
	        		AllomancyData data;
	        		data = AllomancyData.forPlayer(player);
	        		AllomancyData.isMistborn = true;
	        		data.Dirty = false;
	            }
	        });		return null;
		}
	}
}
