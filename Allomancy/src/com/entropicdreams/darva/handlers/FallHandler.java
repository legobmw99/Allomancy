package com.entropicdreams.darva.handlers;

import net.minecraftforge.event.entity.living.LivingFallEvent;
import com.entropicdreams.darva.AllomancyData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;

public class FallHandler {
	@ForgeSubscribe
	public void onFall(LivingFallEvent event)
	{
		if (event.entity != null && event.entity instanceof EntityPlayer)
		{
		EntityPlayer player = (EntityPlayer)event.entity;
		AllomancyData data;
		data = AllomancyData.forPlayer(player);
		if (data.MetalBurning[data.matIron] || data.MetalBurning[data.matSteel])	
			event.setCanceled(true);
		}
	}
}
