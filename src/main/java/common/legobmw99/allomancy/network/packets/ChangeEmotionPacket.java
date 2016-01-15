package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import common.legobmw99.allomancy.ai.AIAttackOnCollideExtended;

public class ChangeEmotionPacket implements IMessage{
	public  ChangeEmotionPacket(){}

	private int entityID;
	private int aggro;
	
	public  ChangeEmotionPacket(int entityID, boolean aggro){
		this.entityID = entityID;
		this.aggro = aggro ? 1 : 0;
	}
	
	
	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = ByteBufUtils.readVarInt(buf, 5);
		aggro = ByteBufUtils.readVarInt(buf,1);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, entityID, 5);
		ByteBufUtils.writeVarInt(buf, aggro, 1);
	}
	
	public static class Handler implements IMessageHandler<ChangeEmotionPacket, IMessage>{

		@Override
		public IMessage onMessage(final ChangeEmotionPacket message, final MessageContext ctx) {
	        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
	        mainThread.addScheduledTask(new Runnable() {
	            @Override
	            public void run() {
	            	EntityCreature target;
	        		target = (EntityCreature) ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityID);
	        		if ((target != null) && message.aggro == 1) {
	        			target.tasks.taskEntries.clear();
	        			target.tasks.addTask(1, new EntityAISwimming(target));
	    				target.tasks.addTask(5, new AIAttackOnCollideExtended(target,
	    						1d, false));
	        			target.targetTasks.addTask(5, new EntityAINearestAttackableTarget(
	        					target, EntityPlayer.class, false));
	        			target.tasks.addTask(5, new EntityAIWander(target, 0.8D));
	        			target.tasks.addTask(6, new EntityAIWatchClosest(target,
	        					EntityPlayer.class, 8.0F));
	        			target.tasks.addTask(6, new EntityAILookIdle(target));
	        			target.targetTasks.addTask(2, new EntityAIHurtByTarget(target,
	        					false));
	        		}
	        		if ((target != null) && !(message.aggro==1)) {
	        			target.tasks.addTask(0, new EntityAISwimming(target));
	        			target.tasks.addTask(1, new EntityAIPanic(target, 2.0D));
	        			target.tasks.addTask(5, new EntityAIWander(target, 1.0D));
	        			target.tasks.addTask(6, new EntityAIWatchClosest(target,
	        					EntityPlayer.class, 6.0F));
	        			target.tasks.addTask(7, new EntityAILookIdle(target));
	        		}

	            
	     		}
	        });		return null;
		}
	}
}