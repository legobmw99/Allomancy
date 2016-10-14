package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import common.legobmw99.allomancy.common.AllomancyCapabilities;

public class AllomancyCapabiltiesPacket implements IMessage{

	public AllomancyCapabiltiesPacket() {}

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



	public AllomancyCapabiltiesPacket(AllomancyCapabilities data, int entityID) {
		this.iron = data.getMetalAmounts()[0];
		this.steel = data.getMetalAmounts()[1];
		this.tin = data.getMetalAmounts()[2];
		this.pewter = data.getMetalAmounts()[3];
		this.zinc = data.getMetalAmounts()[4];
		this.brass = data.getMetalAmounts()[5];
		this.copper = data.getMetalAmounts()[6];
		this.bronze = data.getMetalAmounts()[7];
		
		this.biron = data.getMetalBurning()[0] ? 1 : 0;
		this.bsteel = data.getMetalBurning()[1] ? 1 : 0;
		this.btin = data.getMetalBurning()[2] ? 1 : 0;
		this.bpewter = data.getMetalBurning()[3] ? 1 : 0;
		this.bzinc = data.getMetalBurning()[4] ? 1 : 0;
		this.bbrass = data.getMetalBurning()[5] ? 1 : 0;
		this.bcopper = data.getMetalBurning()[6] ? 1 : 0;
		this.bbronze = data.getMetalBurning()[7] ? 1 : 0;
		
		this.entityID = entityID;

	}
	@Override
	public void fromBytes(ByteBuf buf) {

		iron = ByteBufUtils.readVarInt(buf,5);
		steel = ByteBufUtils.readVarInt(buf,5);
		tin = ByteBufUtils.readVarInt(buf,5);
		pewter = ByteBufUtils.readVarInt(buf,5);
		zinc = ByteBufUtils.readVarInt(buf,5);
		brass = ByteBufUtils.readVarInt(buf,5);
		copper = ByteBufUtils.readVarInt(buf,5);
		bronze = ByteBufUtils.readVarInt(buf,5);
		
		biron = ByteBufUtils.readVarInt(buf,5);
		bsteel = ByteBufUtils.readVarInt(buf,5);
		btin = ByteBufUtils.readVarInt(buf,5);
		bpewter = ByteBufUtils.readVarInt(buf,5);
		bzinc = ByteBufUtils.readVarInt(buf,5);
		bbrass = ByteBufUtils.readVarInt(buf,5);
		bcopper = ByteBufUtils.readVarInt(buf,5);
		bbronze = ByteBufUtils.readVarInt(buf,5);
		
		entityID =  ByteBufUtils.readVarInt(buf, 5);


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

	public static class Handler implements IMessageHandler<AllomancyCapabiltiesPacket, IMessage>{

		@Override
		public IMessage onMessage(final AllomancyCapabiltiesPacket message, final MessageContext ctx) {
			IThreadListener mainThread =  Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayer player =  (EntityPlayer) Minecraft.getMinecraft().thePlayer.worldObj.getEntityByID(message.entityID);
					AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);

					cap.getMetalAmounts()[0] = message.iron;
					cap.getMetalAmounts()[1] = message.steel;
					cap.getMetalAmounts()[2] = message.tin;
					cap.getMetalAmounts()[3] = message.pewter;
					cap.getMetalAmounts()[4] = message.zinc;
					cap.getMetalAmounts()[5] = message.brass;
					cap.getMetalAmounts()[6] = message.copper;
					cap.getMetalAmounts()[7] = message.bronze;
					
					cap.getMetalBurning()[0] = message.biron == 1 ? true : false;
					cap.getMetalBurning()[1] = message.bsteel == 1 ? true : false;
					cap.getMetalBurning()[2] = message.btin == 1 ? true : false;
					cap.getMetalBurning()[3] = message.bpewter == 1 ? true : false;
					cap.getMetalBurning()[4] = message.bzinc == 1 ? true : false;
					cap.getMetalBurning()[5] = message.bbrass == 1 ? true : false;
					cap.getMetalBurning()[6] = message.bcopper == 1 ? true : false;
					cap.getMetalBurning()[7] = message.bbronze == 1 ? true : false;
					
					

				}
			});		return null;
		}
	}
}
