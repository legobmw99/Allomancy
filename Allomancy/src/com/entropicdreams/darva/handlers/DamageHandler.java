package com.entropicdreams.darva.handlers;

import com.entropicdreams.darva.AllomancyData;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class DamageHandler {

	@ForgeSubscribe
	public void onDamage(LivingHurtEvent event)
	{
		
		if (event.source.getSourceOfDamage() instanceof EntityPlayerMP)
		{
			EntityPlayerMP source = (EntityPlayerMP) event.source.getSourceOfDamage();
			AllomancyData data;
			data = AllomancyData.forPlayer(source);
			if (data.MetalBurning[data.matPewter])
			{
				event.ammount +=2;
			}
		}
	}
}
