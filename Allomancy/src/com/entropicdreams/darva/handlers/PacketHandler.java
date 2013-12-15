package com.entropicdreams.darva.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

	public final static int Packet_Allomancy_Data = 0;
	public final static int Packet_Allomancy_Select_Metal = 1;
	public final static int Packet_Allomancy_Update_Burn = 2;
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		// TODO Auto-generated method stub
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			EntityPlayerMP mpPlr;
			mpPlr = (EntityPlayerMP) player;
			serverRec(mpPlr, packet);
		
		} else if (side == Side.CLIENT) {
			EntityClientPlayerMP mpPlr;
			mpPlr = (EntityClientPlayerMP) player;
			clientRec(mpPlr, packet);
		
		} else {
		        // We have an errornous state! 
		}		
		
	}
	private void serverRec(EntityPlayerMP player, Packet250CustomPayload packet)
	{
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		AllomancyData data;
		int Type = -1;
		
		try {
			Type = inputStream.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch (Type)
		{
		case PacketHandler.Packet_Allomancy_Data:
			 data = AllomancyData.forPlayer(player);
			data.updateData(packet);
			break;
		case PacketHandler.Packet_Allomancy_Select_Metal:
			data = AllomancyData.forPlayer(player);
			try {
				data.setSelected(inputStream.readInt());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case PacketHandler.Packet_Allomancy_Update_Burn:
			data= AllomancyData.forPlayer(player);
			data.updateBurn(packet);
			break;
		default:
			return;
		}

	}
	private void clientRec(EntityClientPlayerMP player, Packet250CustomPayload packet)
	{
		AllomancyData data;
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int Type = -1;
		
		try {
			Type = inputStream.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch (Type)
		{
		case PacketHandler.Packet_Allomancy_Data:
			data = AllomancyData.forPlayer(player);
			data.updateData(packet);
		default:
			return;
		case PacketHandler.Packet_Allomancy_Update_Burn:
			data = AllomancyData.forPlayer(player);
			data.updateBurn(packet); 
		}

	}
	
	public static Packet250CustomPayload changeBurn(int mat, boolean value )
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(9);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(PacketHandler.Packet_Allomancy_Update_Burn);
			outputStream.writeInt(mat);
			outputStream.writeBoolean(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "Allomancy_Data";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		return packet;

	}
	
	public static Packet250CustomPayload updateSelectedMetal(int metal)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(PacketHandler.Packet_Allomancy_Select_Metal);
			outputStream.writeInt(metal);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "Allomancy_Data";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		return packet;

		
	}
	public static Packet250CustomPayload updateAllomancyData(AllomancyData data)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(40);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
				outputStream.writeInt(PacketHandler.Packet_Allomancy_Data);
				for(int i = 0; i< data.MetalAmounts.length; i++)
				{
					outputStream.writeInt(data.MetalAmounts[i]);
				}
		        
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
