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




	public AllomancyCapabiltiesPacket(AllomancyCapabilities data) {
		this.iron = data.MetalAmounts[0];
		this.steel = data.MetalAmounts[1];
		this.tin = data.MetalAmounts[2];
		this.pewter = data.MetalAmounts[3];
		this.zinc = data.MetalAmounts[4];
		this.brass = data.MetalAmounts[5];
		this.copper = data.MetalAmounts[6];
		this.bronze = data.MetalAmounts[7];

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

	}

	@Override
	public void toBytes(ByteBuf buf) {
		for (int i = 0; i < 8; i++){
			ByteBufUtils.writeVarInt(buf, iron, 5);
			ByteBufUtils.writeVarInt(buf, steel, 5);
			ByteBufUtils.writeVarInt(buf, tin, 5);
			ByteBufUtils.writeVarInt(buf, pewter, 5);
			ByteBufUtils.writeVarInt(buf, zinc, 5);
			ByteBufUtils.writeVarInt(buf, brass, 5);
			ByteBufUtils.writeVarInt(buf, copper, 5);
			ByteBufUtils.writeVarInt(buf, bronze, 5);

		}

	}

	public static class Handler implements IMessageHandler<AllomancyCapabiltiesPacket, IMessage>{

		@Override
		public IMessage onMessage(final AllomancyCapabiltiesPacket message, final MessageContext ctx) {
			IThreadListener mainThread =  Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayer player =  Minecraft.getMinecraft().thePlayer;
					AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
					for (int i = 0; i < 8; i++){
						cap.MetalBurning[i] = false;

					}
					cap.MetalAmounts[0] = message.iron;
					cap.MetalAmounts[1] = message.steel;
					cap.MetalAmounts[2] = message.tin;
					cap.MetalAmounts[3] = message.pewter;
					cap.MetalAmounts[4] = message.zinc;
					cap.MetalAmounts[5] = message.brass;
					cap.MetalAmounts[6] = message.copper;
					cap.MetalAmounts[7] = message.bronze;

				}
			});		return null;
		}
	}
}
