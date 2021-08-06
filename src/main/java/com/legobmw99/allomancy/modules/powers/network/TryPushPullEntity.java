package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.modules.powers.PowerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class TryPushPullEntity {

    private final int entityIDOther;
    private final int direction;

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

    public static TryPushPullEntity decode(FriendlyByteBuf buf) {
        return new TryPushPullEntity(buf.readInt(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityIDOther);
        buf.writeInt(this.direction);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            Entity target = player.level.getEntity(this.entityIDOther);
            if (target != null) {
                if (PowerUtils.isEntityMetal(target)) {
                    // The player moves
                    if (target instanceof IronGolem || target instanceof ItemFrame) {
                        PowerUtils.move(this.direction, player, target.blockPosition());

                    } else if (target instanceof ItemEntity || target instanceof FallingBlockEntity || target instanceof ArmorStand ||
                               (target instanceof AbstractMinecart && !target.isVehicle())) {
                        PowerUtils.move(this.direction / 2.0, target, player.blockPosition());

                        // Split the difference
                    } else if (!(target instanceof ThrowableItemProjectile)) {
                        PowerUtils.move(this.direction / 2.0, target, player.blockPosition());

                        PowerUtils.move(this.direction / 2.0, player, target.blockPosition());
                    }
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
