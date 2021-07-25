package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.modules.powers.PowerUtils;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeEmotionPacket {

    private final int entityID;
    private final boolean make_aggressive;


    /**
     * Make a mob either angry or passive, depending on aggro
     *
     * @param entityID        the mob to be effected
     * @param make_aggressive true if mob should be aggressive
     */
    public ChangeEmotionPacket(int entityID, boolean make_aggressive) {
        this.entityID = entityID;
        this.make_aggressive = make_aggressive;
    }

    public static ChangeEmotionPacket decode(PacketBuffer buf) {
        return new ChangeEmotionPacket(buf.readInt(), buf.readBoolean());
    }

    public void encode(PacketBuffer buf) {
        buf.writeInt(this.entityID);
        buf.writeBoolean(this.make_aggressive);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity allomancer = ctx.get().getSender();
            CreatureEntity target = (CreatureEntity) allomancer.level.getEntity(this.entityID);
            if (target == null) {
                return;
            }
            boolean enhanced = allomancer.getCapability(AllomancerCapability.PLAYER_CAP).filter(IAllomancerData::isEnhanced).isPresent();
            if (this.make_aggressive) {
                PowerUtils.riotEntity(target, allomancer, enhanced);
            } else {
                PowerUtils.sootheEntity(target, allomancer, enhanced);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}