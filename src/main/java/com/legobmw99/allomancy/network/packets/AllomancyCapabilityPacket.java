package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyCapability;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AllomancyCapabilityPacket implements IMessage {

    public AllomancyCapabilityPacket() {
    }

    private NBTTagCompound nbt;
    private int entityID;

    /**
     * Packet for sending Allomancy player data to a client
     *
     * data can be null, so we handle that case as well.
     *
     * @param data     the AllomancyCapabiltiy data for the player
     * @param entityID the player's ID
     */
    public AllomancyCapabilityPacket(AllomancyCapability data, int entityID) {
        if (data != null) {
            this.nbt = data.serializeNBT() != null ? data.serializeNBT() : new AllomancyCapability().serializeNBT();
        } else {
            this.nbt = new NBTTagCompound();
        }
        this.entityID = entityID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
        entityID = ByteBufUtils.readVarInt(buf, 5);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
        ByteBufUtils.writeVarInt(buf, entityID, 5);
    }

    public static class Handler implements IMessageHandler<AllomancyCapabilityPacket, IMessage> {

        @Override
        public IMessage onMessage(final AllomancyCapabilityPacket message, final MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().player.world.getEntityByID(message.entityID);
                    if (player != null) {
                        AllomancyCapability playerCap = AllomancyCapability.forPlayer(player);
                        playerCap.deserializeNBT(message.nbt);
                    }
                }
            });
            return null;
        }
    }
}
