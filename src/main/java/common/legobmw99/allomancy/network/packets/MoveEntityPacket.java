package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MoveEntityPacket implements IMessage {
	public MoveEntityPacket(){}

	
	
	private double motionX;
	private double motionY;
	private double motionZ;
	private int entityID;
	public MoveEntityPacket(double motionX, double motionY,double motionZ, int entityID) {
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		this.entityID = entityID;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		motionX = ((double) ByteBufUtils.readVarInt(buf, 3000))/100;
		motionY = ((double) ByteBufUtils.readVarInt(buf, 3000))/100;
		motionZ = ((double) ByteBufUtils.readVarInt(buf, 3000))/100;
		entityID =  ByteBufUtils.readVarInt(buf, 2147483647);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf,(int)(motionX*100), 3000);
		ByteBufUtils.writeVarInt(buf,(int)(motionY*100), 3000);
		ByteBufUtils.writeVarInt(buf, (int)(motionZ*100), 3000);
		ByteBufUtils.writeVarInt(buf, entityID, 2147483647);		
	}

	public static class Handler implements IMessageHandler<MoveEntityPacket, IMessage>{

		@Override
		public IMessage onMessage(final MoveEntityPacket message, final MessageContext ctx) {
	        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	        		Entity target = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityID);
	        		if (target == null) {
	        			return;
	        		} else {
	        			target.motionX = message.motionX;
	        			target.motionY = message.motionY;
	        			target.motionZ = message.motionZ;
	        		}	            }
	        });		return null;
		}
	}
}
