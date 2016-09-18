package common.legobmw99.allomancy.network.packets;


import common.legobmw99.allomancy.common.AllomancyCapabilities;
import common.legobmw99.allomancy.common.Registry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GetCapabilitiesPacket implements IMessage {
	public GetCapabilitiesPacket(){}

	
	

	private int entityIDSender;
	private int entityIDOther;
	public GetCapabilitiesPacket(int entityIDOther, int entityIDSender) {

		this.entityIDOther = entityIDOther;
		this.entityIDSender = entityIDSender;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		
		entityIDSender =  ByteBufUtils.readVarInt(buf, 5);
		entityIDOther =  ByteBufUtils.readVarInt(buf, 5);
		

	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeVarInt(buf, entityIDSender, 5);		
		ByteBufUtils.writeVarInt(buf, entityIDOther, 5);		
	}

	public static class Handler implements IMessageHandler<GetCapabilitiesPacket, IMessage>{

		@Override
		public IMessage onMessage(final GetCapabilitiesPacket message, final MessageContext ctx) {
	        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	        		Entity target = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityIDOther);
	        		EntityPlayer player = (EntityPlayer) ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityIDSender);
	        		AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(target);
	        		
	                Registry.network.sendTo(new AllomancyCapabiltiesPacket(cap, message.entityIDOther),(EntityPlayerMP) player);
	            }
	        });		return null;
		}
	}
}
