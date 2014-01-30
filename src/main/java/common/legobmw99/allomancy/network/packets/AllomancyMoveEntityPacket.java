package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import common.legobmw99.allomancy.network.AbstractPacket;

public class AllomancyMoveEntityPacket extends AbstractPacket{
	
	private double motionX;
	private double motionY;
	private double motionZ;
	private int entityID;

	public AllomancyMoveEntityPacket(double motionX,
			double motionY, double motionZ, int entityID)
	{
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		this.entityID = entityID;
	}
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		buffer.writeDouble(motionX);
		buffer.writeDouble(motionY);
		buffer.writeDouble(motionZ);
		buffer.writeInt(entityID);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		motionX = buffer.readDouble();
		motionY = buffer.readDouble();
		motionZ = buffer.readDouble();
		entityID = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		Entity target = player.worldObj.getEntityByID(entityID);
		if (target == null) {
			return;
		}
		else {
			target.motionX = motionX;
			target.motionY = motionY;
			target.motionZ = motionZ;
		}
	}
}
