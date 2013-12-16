package com.entropicdreams.darva;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import com.entropicdreams.darva.handlers.PacketHandler;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class MetalParticleController implements ITickHandler {
	public LinkedList<Entity> particleTargets;
	
	
	public MetalParticleController()
	{
		particleTargets = new LinkedList<Entity>();
	}
	
	public void tryAdd(Entity entity)
	{
		if (particleTargets.contains(entity))
			return;
		if (entity instanceof EntityLiving)
		{
			tryAddLiving((EntityLiving) entity);
			return;
		}
		if (entity instanceof EntityItem)
		{
			tryAddItem((EntityItem) entity);
		}
	}
	
	
	private void tryAddLiving(EntityLiving entity)
	{
		if (entity == null)
			return;
		if (entity instanceof EntityIronGolem || (entity.getHeldItem() != null &&( entity.getHeldItem().itemID == Item.swordIron.itemID || entity.getHeldItem().itemID == Item.swordGold.itemID) ))
		{
			particleTargets.add(entity);
		}
		
	}
	
	private void tryAddItem(EntityItem entity)
	{
		particleTargets.add(entity);
	}
	public void tryPushEntity(Entity entity)
	{
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		if (entity instanceof EntityItem)
		{
			motionX = ((player.posX - entity.posX) * .1)*-1;
	        motionY = ((player.posY - entity.posY) *.1);
	        motionZ = ((player.posZ - entity.posZ) *.1)*-1;
	        entity.motionX = motionX;
	        entity.motionY = motionY;
	        entity.motionZ = motionZ;
	        PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(motionX, motionY, motionZ, entity.entityId));
		}
		
		if (entity instanceof EntityLiving)
		{
			tryPushMob((EntityCreature) entity);
		}
		
	}
	
	public void tryPullEntity(Entity entity)
	{
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		if (entity instanceof EntityItem)
		{
			motionX = (player.posX - entity.posX) * .1;
	        motionY = (player.posY - entity.posY) *.1;
	        motionZ = (player.posZ - entity.posZ) *.1;
	        entity.motionX = motionX;
	        entity.motionY = motionY;
	        entity.motionZ = motionZ;
	        PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(motionX, motionY, motionZ, entity.entityId));

		}
		if (entity instanceof EntityLiving)
		{
			tryPullMob((EntityCreature) entity);
	 	}

	}
	private void tryPullMob(EntityCreature entity)
	{
		
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (entity instanceof EntityIronGolem)
		{
			System.out.println("push entity");
			motionX = ((player.posX - entity.posX) * .1) *-1;
	        motionY = ((player.posY - entity.posY) *.1);
	        motionZ = ((player.posZ - entity.posZ) *.1)*-1;
	        player.motionX = motionX;
	        player.motionY = motionY;
	        player.motionZ = motionZ;
			//waaaaay too damn heavy to push... you get moved.
		}
		
		if (entity.getHeldItem() == null)
		{
			return;
		}
		
		if (entity.getHeldItem().itemID == Item.swordIron.itemID || entity.getHeldItem().itemID == Item.swordGold.itemID)
		{
			//Pull em towards you.
			motionX = ((player.posX - entity.posX) * .1)*-1;
	        motionY = ((player.posY - entity.posY) *.1);
	        motionZ = ((player.posZ - entity.posZ) *.1)*-1;
	        entity.motionX = motionX;
	        entity.motionY = motionY;
	        entity.motionZ = motionZ;
	        PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(motionX, motionY, motionZ, entity.entityId));
		}
	}
	private void tryPushMob(EntityCreature entity)
	{
		
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (entity instanceof EntityIronGolem)
		{
			System.out.println("push entity");
			motionX = ((player.posX - entity.posX) * .1);
	        motionY = ((player.posY - entity.posY) *.1);
	        motionZ = ((player.posZ - entity.posZ) *.1);
	        player.motionX = motionX;
	        player.motionY = motionY;
	        player.motionZ = motionZ;
			//waaaaay too damn heavy to push... you get moved.
		}
		
		if (entity.getHeldItem() == null)
		{
			return;
		}
		
		if (entity.getHeldItem().itemID == Item.swordIron.itemID || entity.getHeldItem().itemID == Item.swordGold.itemID)
		{
			//Pull em towards you.
			motionX = (player.posX - entity.posX) * .1;
	        motionY = (player.posY - entity.posY) *.1;
	        motionZ = (player.posZ - entity.posZ) *.1;
	        entity.motionX = motionX;
	        entity.motionY = motionY;
	        entity.motionZ = motionZ;
	        PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(motionX, motionY, motionZ, entity.entityId));
		}
	}
		
		@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		LinkedList<Entity> toRemove = new LinkedList<Entity>(); 
		
		if (player == null)
				return;
		
		for(Entity entity : particleTargets)
		{
			
			if (entity.isDead == true)
				toRemove.add(entity);
			
			if (player.getDistanceToEntity(entity) > 10)
				toRemove.add(entity);
		}
		
		for(Entity entity : toRemove)
		{
			particleTargets.remove(entity);
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}
}
