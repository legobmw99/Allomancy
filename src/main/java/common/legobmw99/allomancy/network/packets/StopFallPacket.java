package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StopFallPacket implements IMessage {
	 
	public EntityPlayerMP player;
	
	public  StopFallPacket(){
	}
	
	
	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	public static class Handler implements IMessageHandler<StopFallPacket, IMessage>{

		@Override
		public IMessage onMessage(StopFallPacket message, final MessageContext ctx) {
	        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	            	 ctx.getServerHandler().playerEntity.fallDistance = 0;
	            }
	        });		return null;
		}
	}
}
