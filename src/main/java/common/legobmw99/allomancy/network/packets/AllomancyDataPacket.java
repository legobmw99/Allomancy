package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import common.legobmw99.allomancy.common.AllomancyData;

public class AllomancyDataPacket implements IMessage{
	public AllomancyDataPacket() {}

	

	
	public AllomancyDataPacket(AllomancyData data) {
			 
		
		}
	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {

		
	}

	public static class Handler implements IMessageHandler<AllomancyDataPacket, IMessage>{

		@Override
		public IMessage onMessage(final AllomancyDataPacket message, final MessageContext ctx) {
	        IThreadListener mainThread =  Minecraft.getMinecraft();
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	            	EntityPlayer player =  Minecraft.getMinecraft().thePlayer;
	        		AllomancyData data = AllomancyData.forPlayer(player);
	        		for (int i = 0; i < 8; i++){
	        			data.MetalBurning[i] = false;
	        		}
	        		
	            }
	        });		return null;
		}
	}
}
