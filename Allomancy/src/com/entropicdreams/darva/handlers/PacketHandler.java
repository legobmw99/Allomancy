package com.entropicdreams.darva.handlers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.entropicdreams.darva.AllomancyData;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler {

	public static int Packet_Allomancy_Data = 0;
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		// TODO Auto-generated method stub
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (side == Side.SERVER) {
			EntityPlayerMP mpPlr;
			mpPlr = (EntityPlayerMP) player;
		
		
		} else if (side == Side.CLIENT) {
			EntityClientPlayerMP mpPlr;
			mpPlr = (EntityClientPlayerMP) player;
		
		} else {
		        // We have an errornous state! 
		}		
		
	}
	
	public static Packet250CustomPayload updateAllomancyData(AllomancyData data)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(40);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
				outputStream.writeInt(PacketHandler.Packet_Allomancy_Data);
		        outputStream.writeInt(data.getBrass());
		        outputStream.writeInt(data.getBronze());
		        outputStream.writeInt(data.getCopper());
		        outputStream.writeInt(data.getIron());
		        outputStream.writeInt(data.getPewter());
		        outputStream.writeInt(data.getSelected());
		        outputStream.writeInt(data.getSteel());
		        outputStream.writeInt(data.getTin());
		        outputStream.writeInt(data.getZinc());
		        
		} catch (Exception ex) {
		        ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "Allomancy_Data";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		return packet;
	}


}
