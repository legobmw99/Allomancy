package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.ai.AIAttackOnCollideExtended;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChangeEmotionPacket implements IMessage {
    public ChangeEmotionPacket() {
    }

    private int entityID;
    private int aggro;

    /**
     * Make a mob either angry or passive, depending on aggro
     * 
     * @param entityID
     *            the mob to be effected
     * @param aggro
     *            whether the mob should be mad or passive
     */
    public ChangeEmotionPacket(int entityID, boolean aggro) {
        this.entityID = entityID;
        this.aggro = aggro ? 1 : 0;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = ByteBufUtils.readVarInt(buf, 5);
        aggro = ByteBufUtils.readVarInt(buf, 1);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, entityID, 5);
        ByteBufUtils.writeVarInt(buf, aggro, 1);
    }

    public static class Handler implements IMessageHandler<ChangeEmotionPacket, IMessage> {

        @Override
        public IMessage onMessage(final ChangeEmotionPacket message, final MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityCreature target;
                    target = (EntityCreature) ctx.getServerHandler().playerEntity.world.getEntityByID(message.entityID);

                    if ((target != null) && message.aggro == 1) {
                        target.tasks.taskEntries.clear();

                        target.tasks.addTask(1, new EntityAISwimming(target));
                        target.tasks.addTask(5, new AIAttackOnCollideExtended(target, 1d, false));
                        target.targetTasks.addTask(5, new EntityAINearestAttackableTarget(target, EntityPlayer.class, false));
                        target.tasks.addTask(5, new EntityAIWander(target, 0.8D));
                        target.tasks.addTask(6, new EntityAIWatchClosest(target, EntityPlayer.class, 8.0F));
                        target.tasks.addTask(6, new EntityAILookIdle(target));
                        target.targetTasks.addTask(2, new EntityAIHurtByTarget(target, false));
                        if (target instanceof EntityCreeper) {
                            target.tasks.addTask(2, new EntityAICreeperSwell((EntityCreeper) target));
                        }
                        if(target instanceof EntityRabbit){
                            target.tasks.addTask(4, new AIEvilAttack((EntityRabbit)target));
                        }

                        return;
                    }
                    if ((target != null) && (message.aggro == 0)) {
                        target.tasks.taskEntries.clear();
                        target.setAttackTarget(target);
                        target.setLastAttacker(target);
                        target.setRevengeTarget(target);
                        target.tasks.addTask(0, new EntityAISwimming(target));
                        target.tasks.addTask(0, new EntityAIPanic(target, 0.5D));
                        target.tasks.addTask(5, new EntityAIWander(target, 1.0D));
                        target.tasks.addTask(6, new EntityAIWatchClosest(target, EntityPlayer.class, 6.0F));
                        target.tasks.addTask(7, new EntityAILookIdle(target));

                        return;
                    }

                }
            });
            return null;
        }
    }
    
    static class AIEvilAttack extends EntityAIAttackMelee
    {
        public AIEvilAttack(EntityRabbit rabbit)
        {
            super(rabbit, 1.4D, true);
        }

        protected double getAttackReachSqr(EntityLivingBase attackTarget)
        {
            return (double)(4.0F + attackTarget.width);
        }
    }
}