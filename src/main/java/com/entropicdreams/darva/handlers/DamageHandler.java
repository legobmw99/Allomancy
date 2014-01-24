package com.entropicdreams.darva.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.entropicdreams.darva.AllomancyData;

public class DamageHandler {

	@ForgeSubscribe
	public void onDamage(LivingHurtEvent event) {

		if (event.source.getSourceOfDamage() instanceof EntityPlayerMP) {
			EntityPlayerMP source = (EntityPlayerMP) event.source
					.getSourceOfDamage();
			AllomancyData data;
			data = AllomancyData.forPlayer(source);
			if (data.MetalBurning[AllomancyData.matPewter]) {
				event.ammount += 2;
			}
		}
		if (event.entityLiving instanceof EntityPlayerMP) {
			AllomancyData data = AllomancyData.forPlayer(event.entityLiving);
			if (data.MetalBurning[AllomancyData.matPewter]) {
				event.ammount -= 2;
				data.damageStored++;
			}
		}
	}
}
