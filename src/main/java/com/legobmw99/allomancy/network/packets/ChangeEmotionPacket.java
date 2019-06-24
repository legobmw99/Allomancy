package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.ai.AIAttackOnCollideExtended;
import com.legobmw99.allomancy.ai.AIEvilAttack;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeEmotionPacket {

    private int entityID;
    private boolean aggro;

    /**
     * Make a mob either angry or passive, depending on aggro
     *
     * @param entityID the mob to be effected
     * @param aggro    whether the mob should be mad or passive
     */
    public ChangeEmotionPacket(int entityID, boolean aggro) {
        this.entityID = entityID;
        this.aggro = aggro;
    }


    public static void encode(ChangeEmotionPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.entityID);
        buf.writeBoolean(pkt.aggro);
    }

    public static ChangeEmotionPacket decode(PacketBuffer buf) {
        return new ChangeEmotionPacket(buf.readInt(), buf.readBoolean());
    }


    public static class Handler {

        public static void handle(final ChangeEmotionPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                        CreatureEntity target;
                        target = (CreatureEntity) ctx.get().getSender().world.getEntityByID(message.entityID);

                        if ((target != null) && message.aggro) {
                            target.goalSelector.getRunningGoals().forEach(target.goalSelector::removeGoal);
                            target.setAttackTarget(target);
                            target.setRevengeTarget(target);
                            target.goalSelector.addGoal(1, new SwimGoal(target));
                            target.goalSelector.addGoal(5, new AIAttackOnCollideExtended(target, 1d, false));
                            target.goalSelector.addGoal(5, new NearestAttackableTargetGoal<PlayerEntity>(target, PlayerEntity.class, false));
                            target.goalSelector.addGoal(5, new RandomWalkingGoal(target, 0.8D));
                            target.goalSelector.addGoal(6, new LookAtGoal(target, PlayerEntity.class, 8.0F));
                            target.goalSelector.addGoal(6, new LookRandomlyGoal(target));
                            target.goalSelector.addGoal(2, new HurtByTargetGoal(target, PlayerEntity.class));
                            if (target instanceof CreeperEntity) {
                                target.goalSelector.addGoal(2, new CreeperSwellGoal((CreeperEntity) target));
                            }
                            if (target instanceof RabbitEntity) {
                                target.goalSelector.addGoal(4, new AIEvilAttack((RabbitEntity) target));
                            }

                        } else if ((target != null) ) {
                            target.goalSelector.getRunningGoals().forEach(target.goalSelector::removeGoal);

                            target.goalSelector.addGoal(0, new SwimGoal(target));
                            target.goalSelector.addGoal(0, new PanicGoal(target, 0.5D));
                            target.goalSelector.addGoal(5, new RandomWalkingGoal(target, 1.0D));
                            target.goalSelector.addGoal(6, new LookAtGoal(target, PlayerEntity.class, 6.0F));
                            target.goalSelector.addGoal(7, new LookRandomlyGoal(target));

                        }

                    }
            );
        }
    }


}