package com.entropicdreams.darva.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class DamageHandler {

	@ForgeSubscribe
	public void onDamage(LivingHurtEvent event)
	{
		System.out.println("hmmm");
		if (event.source.getSourceOfDamage() instanceof EntityPlayerMP)
		{
			System.out.println("Ahah!");
		}
	}
}
