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

	private int iron;
	private int steel;
	private int tin;
	private int pewter;
	private int zinc;
	private int brass;
	private int copper;
	private int bronze;



	
	public AllomancyDataPacket(AllomancyData data) {
		this.iron = data.MetalAmounts[0];
		this.steel = data.MetalAmounts[1];
		this.tin = data.MetalAmounts[2];
		this.pewter = data.MetalAmounts[3];
		this.zinc = data.MetalAmounts[4];
		this.brass = data.MetalAmounts[5];
		this.copper = data.MetalAmounts[6];
		this.bronze = data.MetalAmounts[7];

		}
	@Override
	public void fromBytes(ByteBuf buf) {

		iron = ByteBufUtils.readVarInt(buf,5);
		steel = ByteBufUtils.readVarInt(buf,5);
		tin = ByteBufUtils.readVarInt(buf,5);
		pewter = ByteBufUtils.readVarInt(buf,5);
		zinc = ByteBufUtils.readVarInt(buf,5);
		brass = ByteBufUtils.readVarInt(buf,5);
		copper = ByteBufUtils.readVarInt(buf,5);
		bronze = ByteBufUtils.readVarInt(buf,5);
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		for (int i = 0; i < 8; i++){
			ByteBufUtils.writeVarInt(buf, iron, 5);
			ByteBufUtils.writeVarInt(buf, steel, 5);
			ByteBufUtils.writeVarInt(buf, tin, 5);
			ByteBufUtils.writeVarInt(buf, pewter, 5);
			ByteBufUtils.writeVarInt(buf, zinc, 5);
			ByteBufUtils.writeVarInt(buf, brass, 5);
			ByteBufUtils.writeVarInt(buf, copper, 5);
			ByteBufUtils.writeVarInt(buf, bronze, 5);

		}
		
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
	        		data.MetalAmounts[0] = message.iron;
	        		data.MetalAmounts[1] = message.steel;
	        		data.MetalAmounts[2] = message.tin;
	        		data.MetalAmounts[3] = message.pewter;
	        		data.MetalAmounts[4] = message.zinc;
	        		data.MetalAmounts[5] = message.brass;
	        		data.MetalAmounts[6] = message.copper;
	        		data.MetalAmounts[7] = message.bronze;
	        		
	            }
	        });		return null;
		}
	}
}
