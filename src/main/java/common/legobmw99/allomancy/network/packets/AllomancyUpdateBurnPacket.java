package common.legobmw99.allomancy.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.network.AbstractPacket;

public class AllomancyUpdateBurnPacket extends AbstractPacket {

	private int mat;
	private boolean value;
	AllomancyData data;
	public AllomancyUpdateBurnPacket(int mat, boolean value){
	this.mat = mat;
	this.value = value;
	}
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		buffer.writeInt(mat);
        buffer.writeBoolean(value);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
	      mat = buffer.readInt();
	      value = buffer.readBoolean();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		data = AllomancyData.forPlayer(player);
		data.updateBurn(mat, value);
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		data = AllomancyData.forPlayer(player);
		data.updateBurn(mat, value);
	}

}
