package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.modules.powers.util.PowerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TryPushPullEntity {

    private int entityIDOther;
    private int direction;

    /**
     * Send a request to the server to use iron or steel on an entity
     *
     * @param entityIDOther the entity you are requesting the data of
     * @param direction     the direction (1 for push, -1 for pull)
     */
    public TryPushPullEntity(int entityIDOther, int direction) {
        this.entityIDOther = entityIDOther;
        this.direction = direction;

    }

    public void encode(PacketBuffer buf) {
        buf.writeInt(this.entityIDOther);
        buf.writeInt(this.direction);
    }

    public static TryPushPullEntity decode(PacketBuffer buf) {
        return new TryPushPullEntity(buf.readInt(), buf.readInt());
    }


    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            Entity target = player.world.getEntityByID(entityIDOther);
            if (target != null) {
                if (PowerUtils.isEntityMetal(target)) {
                    // The player moves
                    if (target instanceof IronGolemEntity || target instanceof ItemFrameEntity) {
                        PowerUtils.move(direction, player, new BlockPos(target.getPositionVec()));

                        // Depends if the minecart is filled
                    } else if (target instanceof AbstractMinecartEntity) {
                        if (target.isBeingRidden()) {
                            if (!target.isRidingOrBeingRiddenBy(player)) {
                                PowerUtils.move(direction / 2.0, target, new BlockPos(player.getPositionVec()));
                                PowerUtils.move(direction / 2.0, player, new BlockPos(target.getPositionVec()));
                            }
                        } else {
                            PowerUtils.move(direction, target, new BlockPos(player.getPositionVec()));
                        }
                        // The target moves
                    } else if (target instanceof ItemEntity || target instanceof FallingBlockEntity) {
                        PowerUtils.move(direction / 2.0, target, new BlockPos(player.getPositionVec()).down());

                        // Split the difference
                    } else if (!(target instanceof ProjectileItemEntity)) {
                        PowerUtils.move(direction / 2.0, target, new BlockPos(player.getPositionVec()));

                        PowerUtils.move(direction / 2.0, player, new BlockPos(target.getPositionVec()));
                    }
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
