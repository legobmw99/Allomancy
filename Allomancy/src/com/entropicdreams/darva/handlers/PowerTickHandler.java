package com.entropicdreams.darva.handlers;

import java.util.EnumSet;

import net.minecraft.client.entity.EntityClientPlayerMP;

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
		EntityClientPlayerMP player;
		if (tickData[0] instanceof EntityClientPlayerMP)
		{
			player = (EntityClientPlayerMP) tickData[0];
		}
		else
			return;
		data = AllomancyData.forPlayer(player);
	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "Power Handler";
	}

}
