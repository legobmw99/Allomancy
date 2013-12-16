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
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

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
		//We'll do this last... Most complicated.
	}
	
	private void tryAddItem(EntityItem entity)
	{
		particleTargets.add(entity);
	}
	public void tryPushEntity(Entity entity)
	{
		if (entity instanceof EntityItem)
		{
			tryPushItem((EntityItem) entity);
		}
	}
	private void tryPushItem(EntityItem item)
	{
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		motionX = ((player.posX - item.posX) * .03)*-1;
        motionY = ((player.posY - item.posY) *.03)*-1;
        motionZ = ((player.posZ - item.posZ) *.03)*-1;
        item.motionX = motionX;
        item.motionY = motionY;
        item.motionZ = motionZ;
		PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(motionX, motionY, motionZ, item.entityId));
		
	}
	
	public void tryPullEntity(Entity entity)
	{
		if (entity instanceof EntityItem)
		{
			tryPullItem((EntityItem) entity);
		}
	}
	
	private void tryPullItem(EntityItem item)
	{
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		motionX = (player.posX - item.posX) * .03;
        motionY = (player.posY - item.posY) *.03;
        motionZ = (player.posZ - item.posZ) *.03;
        PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(motionX, motionY, motionZ, item.entityId));

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
