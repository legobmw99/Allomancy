package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;

import common.legobmw99.allomancy.network.AbstractPacket;

public class AllomancyChangeEmotionPacket extends AbstractPacket {
	private int entityID;
	private boolean makeAggro;

	public AllomancyChangeEmotionPacket(int entityID, boolean makeAggro) {
		this.entityID = entityID;
		this.makeAggro = makeAggro;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		buffer.writeInt(this.entityID);
		buffer.writeBoolean(this.makeAggro);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		this.entityID = buffer.readInt();
		this.makeAggro = buffer.readBoolean();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		EntityCreature target;
		target = (EntityCreature) player.worldObj.getEntityByID(this.entityID);
		if ((target != null) && this.makeAggro) {
			target.tasks.taskEntries.clear();
			target.tasks.addTask(1, new EntityAISwimming(target));
			target.targetTasks.addTask(5, new EntityAINearestAttackableTarget(
					target, EntityPlayer.class, false));
			target.tasks.addTask(5, new EntityAIWander(target, 0.8D));
			target.tasks.addTask(6, new EntityAIWatchClosest(target,
					EntityPlayer.class, 8.0F));
			target.tasks.addTask(6, new EntityAILookIdle(target));
			target.targetTasks.addTask(2, new EntityAIHurtByTarget(target,
					false));
		}
		if ((target != null) && !this.makeAggro) {
			target.tasks.addTask(0, new EntityAISwimming(target));
			target.tasks.addTask(1, new EntityAIPanic(target, 2.0D));
			target.tasks.addTask(5, new EntityAIWander(target, 1.0D));
			target.tasks.addTask(6, new EntityAIWatchClosest(target,
					EntityPlayer.class, 6.0F));
			target.tasks.addTask(7, new EntityAILookIdle(target));
		}

	}

}
