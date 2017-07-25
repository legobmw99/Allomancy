package com.legobmw99.allomancy.network.packets;

import com.legobmw99.allomancy.util.AllomancyCapability;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AllomancyCapabilityPacket implements IMessage {

	public AllomancyCapabilityPacket() {
	}

	private int iron;
	private int steel;
	private int tin;
	private int pewter;
	private int zinc;
	private int brass;
	private int copper;
	private int bronze;
	private int biron;
	private int bsteel;
	private int btin;
	private int bpewter;
	private int bzinc;
	private int bbrass;
	private int bcopper;
	private int bbronze;
	private int entityID;

	/**
	 * Takes the data and transcribes it into transmittable data
	 * 
	 * @param data
	 *            the AllomancyCapabiltiy data for the player
	 * @param entityID
	 *            the player's ID
	 */
	public AllomancyCapabilityPacket(AllomancyCapability data, int entityID) {
		if (data == null) {
			this.iron = this.steel = this.tin = this.pewter = this.zinc = this.brass = this.copper = this.bronze = 0;
			this.biron = this.bsteel = this.btin = this.bpewter = this.bzinc = this.bbrass = this.bcopper = this.bbronze = 0;
		} else {
			this.iron = data.getMetalAmounts(0);
			this.steel = data.getMetalAmounts(1);
			this.tin = data.getMetalAmounts(2);
			this.pewter = data.getMetalAmounts(3);
			this.zinc = data.getMetalAmounts(4);
			this.brass = data.getMetalAmounts(5);
			this.copper = data.getMetalAmounts(6);
			this.bronze = data.getMetalAmounts(7);

			this.biron = data.getMetalBurning(0) ? 1 : 0;
			this.bsteel = data.getMetalBurning(1) ? 1 : 0;
			this.btin = data.getMetalBurning(2) ? 1 : 0;
			this.bpewter = data.getMetalBurning(3) ? 1 : 0;
			this.bzinc = data.getMetalBurning(4) ? 1 : 0;
			this.bbrass = data.getMetalBurning(5) ? 1 : 0;
			this.bcopper = data.getMetalBurning(6) ? 1 : 0;
			this.bbronze = data.getMetalBurning(7) ? 1 : 0;
		}

		this.entityID = entityID;

	}

	@Override
	public void fromBytes(ByteBuf buf) {

		iron = ByteBufUtils.readVarInt(buf, 5);
		steel = ByteBufUtils.readVarInt(buf, 5);
		tin = ByteBufUtils.readVarInt(buf, 5);
		pewter = ByteBufUtils.readVarInt(buf, 5);
		zinc = ByteBufUtils.readVarInt(buf, 5);
		brass = ByteBufUtils.readVarInt(buf, 5);
		copper = ByteBufUtils.readVarInt(buf, 5);
		bronze = ByteBufUtils.readVarInt(buf, 5);

		biron = ByteBufUtils.readVarInt(buf, 5);
		bsteel = ByteBufUtils.readVarInt(buf, 5);
		btin = ByteBufUtils.readVarInt(buf, 5);
		bpewter = ByteBufUtils.readVarInt(buf, 5);
		bzinc = ByteBufUtils.readVarInt(buf, 5);
		bbrass = ByteBufUtils.readVarInt(buf, 5);
		bcopper = ByteBufUtils.readVarInt(buf, 5);
		bbronze = ByteBufUtils.readVarInt(buf, 5);

		entityID = ByteBufUtils.readVarInt(buf, 5);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, iron, 5);
		ByteBufUtils.writeVarInt(buf, steel, 5);
		ByteBufUtils.writeVarInt(buf, tin, 5);
		ByteBufUtils.writeVarInt(buf, pewter, 5);
		ByteBufUtils.writeVarInt(buf, zinc, 5);
		ByteBufUtils.writeVarInt(buf, brass, 5);
		ByteBufUtils.writeVarInt(buf, copper, 5);
		ByteBufUtils.writeVarInt(buf, bronze, 5);

		ByteBufUtils.writeVarInt(buf, biron, 5);
		ByteBufUtils.writeVarInt(buf, bsteel, 5);
		ByteBufUtils.writeVarInt(buf, btin, 5);
		ByteBufUtils.writeVarInt(buf, bpewter, 5);
		ByteBufUtils.writeVarInt(buf, bzinc, 5);
		ByteBufUtils.writeVarInt(buf, bbrass, 5);
		ByteBufUtils.writeVarInt(buf, bcopper, 5);
		ByteBufUtils.writeVarInt(buf, bbronze, 5);

		ByteBufUtils.writeVarInt(buf, entityID, 5);

	}

	public static class Handler implements IMessageHandler<AllomancyCapabilityPacket, IMessage> {

		@Override
		public IMessage onMessage(final AllomancyCapabilityPacket message, final MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().player.world
							.getEntityByID(message.entityID);
					
					if (player != null) {
						AllomancyCapability cap = AllomancyCapability.forPlayer(player);
						cap.setMetalAmounts(0, message.iron);
						cap.setMetalAmounts(1, message.steel);
						cap.setMetalAmounts(2, message.tin);
						cap.setMetalAmounts(3, message.pewter);
						cap.setMetalAmounts(4, message.zinc);
						cap.setMetalAmounts(5, message.brass);
						cap.setMetalAmounts(6, message.copper);
						cap.setMetalAmounts(7, message.bronze);

						boolean biron, bsteel, btin, bpewter, bzinc, bbrass, bcopper, bbronze;
						biron = message.biron == 1;
						bsteel = message.bsteel == 1;
						btin = message.btin == 1;
						bpewter = message.bpewter == 1;
						bzinc = message.bzinc == 1;
						bbrass = message.bbrass == 1;
						bcopper = message.bcopper == 1;
						bbronze = message.bbronze == 1;

						cap.setMetalBurning(0, biron);
						cap.setMetalBurning(1, bsteel);
						cap.setMetalBurning(2, btin);
						cap.setMetalBurning(3, bpewter);
						cap.setMetalBurning(4, bzinc);
						cap.setMetalBurning(5, bbrass);
						cap.setMetalBurning(6, bcopper);
						cap.setMetalBurning(7, bbronze);
					}
				}
			});
			return null;
		}
	}
}
