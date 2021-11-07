package com.legobmw99.allomancy.modules.powers.network;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Arrays;
import java.util.function.Supplier;


public class UpdateBurnPacket {

    private final Metal mt;
    private final boolean value;

    /**
     * Send request to the server to change the burning state of a metal
     *
     * @param mt    the metal
     * @param value whether it is burning
     */
    public UpdateBurnPacket(Metal mt, boolean value) {
        this.mt = mt;
        this.value = value; // Convert bool to int
    }

    public static UpdateBurnPacket decode(FriendlyByteBuf buf) {
        return new UpdateBurnPacket(buf.readEnum(Metal.class), buf.readBoolean());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.mt);
        buf.writeBoolean(this.value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                if (data.hasPower(this.mt) && data.getAmount(this.mt) > 0) {
                    data.setBurning(this.mt, this.value);
                    if (!this.value && this.mt == Metal.DURALUMIN) {
                        data.drainMetals(Arrays.stream(Metal.values()).filter(data::isBurning).toArray(Metal[]::new));
                    }
                    if (!this.value && data.isEnhanced()) {
                        data.drainMetals(this.mt);
                    }
                } else {
                    data.setBurning(this.mt, false);
                }
                Network.sync(data, player);
            });

        });
        ctx.get().setPacketHandled(true);
    }
}
