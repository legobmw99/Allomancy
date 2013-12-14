package com.entropicdreams.darva.handlers;


import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.entropicdreams.darva.AllomancyData;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class PowerTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		AllomancyData data;
		World world;
		world = (World) tickData[0];
		
		if (world.isRemote)
		{
		}
		else
		{
		
			List <EntityPlayerMP> list = MinecraftServer.getServer().getConfigurationManager().playerEntityList;

			
			for(EntityPlayerMP curPlayer : list )
			{
				data = AllomancyData.forPlayer(curPlayer);
				
				if (data.isbTin())
				{
					if( !curPlayer.isPotionActive(Potion.nightVision.getId()))
						curPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 1000));
					else
					{
						PotionEffect eff;
						eff =  curPlayer.getActivePotionEffect(Potion.nightVision);
						if (eff.getDuration() < 400)
						{
							curPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 1000));
						}
					}
					
				}
				if (data.isbTin() == false && curPlayer.isPotionActive(Potion.nightVision.getId()))
				{
					curPlayer.removePotionEffect(Potion.nightVision.getId());
				}
				
			}
		}
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "Power Handler";
	}

}
