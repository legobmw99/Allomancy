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
		this.iron = data.MetalAmounts[0];
		this.steel = data.MetalAmounts[1];
		this.tin = data.MetalAmounts[2];
		this.pewter = data.MetalAmounts[3];
		this.zinc = data.MetalAmounts[4];
		this.brass = data.MetalAmounts[5];
		this.copper = data.MetalAmounts[6];
		this.bronze = data.MetalAmounts[7];
		
		this.biron = data.MetalBurning[0] ? 1 : 0;
		this.bsteel = data.MetalBurning[1] ? 1 : 0;
		this.btin = data.MetalBurning[2] ? 1 : 0;
		this.bpewter = data.MetalBurning[3] ? 1 : 0;
		this.bzinc = data.MetalBurning[4] ? 1 : 0;
		this.bbrass = data.MetalBurning[5] ? 1 : 0;
		this.bcopper = data.MetalBurning[6] ? 1 : 0;
		this.bbronze = data.MetalBurning[7] ? 1 : 0;
		
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

					cap.MetalAmounts[0] = message.iron;
					cap.MetalAmounts[1] = message.steel;
					cap.MetalAmounts[2] = message.tin;
					cap.MetalAmounts[3] = message.pewter;
					cap.MetalAmounts[4] = message.zinc;
					cap.MetalAmounts[5] = message.brass;
					cap.MetalAmounts[6] = message.copper;
					cap.MetalAmounts[7] = message.bronze;
					
					cap.MetalBurning[0] = message.biron == 1 ? true : false;
					cap.MetalBurning[1] = message.bsteel == 1 ? true : false;
					cap.MetalBurning[2] = message.btin == 1 ? true : false;
					cap.MetalBurning[3] = message.bpewter == 1 ? true : false;
					cap.MetalBurning[4] = message.bzinc == 1 ? true : false;
					cap.MetalBurning[5] = message.bbrass == 1 ? true : false;
					cap.MetalBurning[6] = message.bcopper == 1 ? true : false;
					cap.MetalBurning[7] = message.bbronze == 1 ? true : false;
					
					

				}
			});		return null;
		}
	}
}
