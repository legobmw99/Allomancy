package com.entropicdreams.darva;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
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
		System.out.println("Added item");
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
