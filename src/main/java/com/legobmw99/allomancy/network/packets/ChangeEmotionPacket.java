package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.ai.AIAttackOnCollideExtended;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.ServerWorld;
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
            IThreadListener mainThread = (ServerWorld) ctx.getServerHandler().player.world;
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    CreatureEntity target;
                    target = (CreatureEntity) ctx.getServerHandler().player.world.getEntityByID(message.entityID);

                    if ((target != null) && message.aggro == 1) {
                        target.tasks.taskEntries.clear();

                        target.tasks.addTask(1, new SwimGoal(target));
                        target.tasks.addTask(5, new AIAttackOnCollideExtended(target, 1d, false));
                        target.targetTasks.addTask(5, new NearestAttackableTargetGoal(target, PlayerEntity.class, false));
                        target.tasks.addTask(5, new RandomWalkingGoal(target, 0.8D));
                        target.tasks.addTask(6, new LookAtGoal(target, PlayerEntity.class, 8.0F));
                        target.tasks.addTask(6, new LookRandomlyGoal(target));
                        target.targetTasks.addTask(2, new HurtByTargetGoal(target, false));
                        if (target instanceof CreeperEntity) {
                            target.tasks.addTask(2, new CreeperSwellGoal((CreeperEntity) target));
                        }
                        if(target instanceof RabbitEntity){
                            target.tasks.addTask(4, new AIEvilAttack((RabbitEntity)target));
                        }

                        return;
                    }
                    if ((target != null) && (message.aggro == 0)) {
                        target.tasks.taskEntries.clear();
                        target.setAttackTarget(target);
                        target.setRevengeTarget(target);
                        target.setRevengeTarget(target);
                        target.tasks.addTask(0, new SwimGoal(target));
                        target.tasks.addTask(0, new PanicGoal(target, 0.5D));
                        target.tasks.addTask(5, new RandomWalkingGoal(target, 1.0D));
                        target.tasks.addTask(6, new LookAtGoal(target, PlayerEntity.class, 6.0F));
                        target.tasks.addTask(7, new LookRandomlyGoal(target));

                        return;
                    }

                }
            });
            return null;
        }
    }
    
    static class AIEvilAttack extends MeleeAttackGoal
    {
        public AIEvilAttack(RabbitEntity rabbit)
        {
            super(rabbit, 1.4D, true);
        }

        protected double getAttackReachSqr(LivingEntity attackTarget)
        {
            return (double)(4.0F + attackTarget.width);
        }
    }
}