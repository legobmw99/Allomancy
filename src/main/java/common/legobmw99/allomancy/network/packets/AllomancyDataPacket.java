package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import common.legobmw99.allomancy.common.AllomancyData;

public class AllomancyDataPacket implements IMessage{
	public AllomancyDataPacket() {}

	
	AllomancyData data;
	private int[] value;
	
	public AllomancyDataPacket(AllomancyData data) {
			 this.value = data.MetalAmounts;
		
		}
	@Override
	public void fromBytes(ByteBuf buf) {
		for (int i = 0; i < 8; i++)
			this.value[i] = ByteBufUtils.readVarInt(buf,200);		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		for (int element : this.value)
			ByteBufUtils.writeVarInt(buf,element,200);
		
	}

	public static class Handler implements IMessageHandler<AllomancyDataPacket, IMessage>{

		@Override
		public IMessage onMessage(final AllomancyDataPacket message, final MessageContext ctx) {
	        IThreadListener mainThread =  Minecraft.getMinecraft();
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	            	EntityPlayerMP player =  ctx.getServerHandler().playerEntity;
	        		AllomancyData data = AllomancyData.forPlayer(player);
	        		for (int i : message.value){
	        			data.MetalAmounts[i] = message.value[i];
	        		}
	        		data.updateData(message.value, player);
	            }
	        });		return null;
		}
	}
}
