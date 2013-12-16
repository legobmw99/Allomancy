package com.entropicdreams.darva.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.FlyingItem;
import com.entropicdreams.darva.ai.AIAttackOnCollideExtended;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketHandler implements IPacketHandler {

	public final static int Packet_Allomancy_Data = 0;
	public final static int Packet_Allomancy_Select_Metal = 1;
	public final static int Packet_Allomancy_Update_Burn = 2;
	public final static int Packet_Allomancy_Change_Emotion = 3;
	public final static int Packet_Allomancy_Move_Entity = 4;
	public final static int Packet_Allomancy_Update_Texture = 5;
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
			
			clientRec((EntityPlayer) player, packet);
		
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
		case PacketHandler.Packet_Allomancy_Change_Emotion:
			changeEmotions(packet, player);
			break;
		case PacketHandler.Packet_Allomancy_Move_Entity:
			moveEntity(packet,player);
		default:
			return;
		}

	}
	@SideOnly(Side.CLIENT)
	private void clientRec(EntityPlayer player, Packet250CustomPayload packet)
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
		case PacketHandler.Packet_Allomancy_Update_Texture:
			updateTexture(player, packet);
		}

	}
	private void updateTexture(EntityPlayer player, Packet250CustomPayload packet)
	{
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int entityId;
		int itemId;
		Item targItem;
		try {
			inputStream.readInt();
			
			itemId = inputStream.readInt();
			entityId = inputStream.readInt(); 
			targItem = Item.itemsList[itemId];
			FlyingItem fi = (FlyingItem) player.worldObj.getEntityByID(entityId);
			fi.carriedIcon= targItem.getIconFromDamage(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Throw away packet type info.
		
		
		
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
	public static Packet250CustomPayload changeEmotions(int entityID, boolean makeAggro)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(9);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try {
			outputStream.writeInt(PacketHandler.Packet_Allomancy_Change_Emotion);
			outputStream.writeInt(entityID);
			outputStream.writeBoolean(makeAggro);
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

	
	private void moveEntity(Packet250CustomPayload packet, EntityPlayerMP player)
	{
		int targetID;
		Entity target;
		double motionX, motionY, motionZ;
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		try {
			inputStream.readInt();
			motionX = inputStream.readDouble();
			motionY = inputStream.readDouble();
			motionZ = inputStream.readDouble();
			targetID = inputStream.readInt();
			
			target = player.worldObj.getEntityByID(targetID);
			if (target == null)
			{
				return;
			}
			if (target instanceof EntityItem)
			{
				FlyingItem fi = new FlyingItem(player.worldObj,player,(EntityItem) target);
				fi.setThrowableHeading(motionX, motionY, motionZ, 1, 1.0f);
				player.worldObj.spawnEntityInWorld(fi);
			}
			else
			{
			target.motionX = motionX;
			target.motionY = motionY;
			target.motionZ = motionZ;
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Throw away packet type data.
		
		
	}
	
	private void changeEmotions(Packet250CustomPayload packet, EntityPlayerMP player) //Nowhere better to stick this. *sigh*
	{
		int targetID;
		boolean makeAggro;
		EntityCreature target;
		
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		try {
			inputStream.readInt();
			targetID = inputStream.readInt();
			makeAggro = inputStream.readBoolean();
			target = (EntityCreature) player.worldObj.getEntityByID(targetID);
			
			if (target != null  && makeAggro)
			{
				target.tasks.taskEntries.clear();
		        target.tasks.addTask(1, new EntityAISwimming(target));
				target.tasks.addTask(5, new AIAttackOnCollideExtended(target, 1d, false ));
				target.targetTasks.addTask(5, new EntityAINearestAttackableTarget(target, EntityPlayer.class, 100, false));
		        target.tasks.addTask(5, new EntityAIWander(target, 0.8D));
		        target.tasks.addTask(6, new EntityAIWatchClosest(target, EntityPlayer.class, 8.0F));
		        target.tasks.addTask(6, new EntityAILookIdle(target));
		        target.targetTasks.addTask(2, new EntityAIHurtByTarget(target, false));
			}
			if (target !=null && !makeAggro)
			{
				System.out.println("peaceful");
				 target.tasks.addTask(0, new EntityAISwimming(target));
			        target.tasks.addTask(1, new EntityAIPanic(target, 2.0D));
			        target.tasks.addTask(5, new EntityAIWander(target, 1.0D));
			        target.tasks.addTask(6, new EntityAIWatchClosest(target, EntityPlayer.class, 6.0F));
			        target.tasks.addTask(7, new EntityAILookIdle(target));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Throw away packet type info.
		
	}
	public static Packet250CustomPayload moveEntity(double motionX, double motionY, double motionZ, int entityID)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(32);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try {
			outputStream.writeInt(PacketHandler.Packet_Allomancy_Move_Entity);
			outputStream.writeDouble(motionX);
			outputStream.writeDouble(motionY);
			outputStream.writeDouble(motionZ);
			outputStream.writeInt(entityID);

			
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
	public static Packet250CustomPayload updateIcon(int itemID, int entityID)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(32);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try {
			outputStream.writeInt(PacketHandler.Packet_Allomancy_Update_Texture);
			outputStream.writeInt(itemID);
			outputStream.writeInt(entityID);
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
}
