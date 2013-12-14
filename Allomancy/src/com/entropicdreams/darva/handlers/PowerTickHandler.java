package com.entropicdreams.darva.handlers;

import java.util.EnumSet;
import java.util.LinkedList;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;

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
		System.out.println("Tick");
		LinkedList<EntityPlayerMP> players;
		
		players = (LinkedList<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		
		for(EntityPlayerMP curPlayer : players)
		{
			data = AllomancyData.forPlayer(curPlayer);
			
			if (data.isbTin())
				curPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 200));
			
		}
		
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "Power Handler";
	}

}
