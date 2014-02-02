package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.network.AbstractPacket;

public class AllomancySelectMetalPacket extends AbstractPacket {

	private int metal;
	AllomancyData data;

	public AllomancySelectMetalPacket(int metal) {
		this.metal = metal;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		buffer.writeInt(this.metal);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		this.metal = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		this.data = AllomancyData.forPlayer(player);
		this.data.setSelected(this.metal);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		this.data = AllomancyData.forPlayer(player);
		this.data.setSelected(this.metal);
	}

}
