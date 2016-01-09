package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.network.AbstractPacket;

public class AllomancyDataPacket extends AbstractPacket {

	AllomancyData data;
	private int[] value;

	public AllomancyDataPacket(AllomancyData data) {

	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		for (int element : this.value)
			buffer.writeInt(element);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 8; i++)
			this.value[i] = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		for (int i = 0; i < data.MetalAmounts.length; i++) {
			this.value[i] = data.MetalAmounts[i];
		}
		this.data = AllomancyData.forPlayer(player);
		this.data.updateData(this.value);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		for (int i = 0; i < data.MetalAmounts.length; i++) {
			this.value[i] = data.MetalAmounts[i];
		}
		this.data = AllomancyData.forPlayer(player);
		this.data.updateData(this.value);

	}

}
