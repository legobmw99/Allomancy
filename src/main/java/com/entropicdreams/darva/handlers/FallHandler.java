package com.entropicdreams.darva.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.ModMain;

public class FallHandler {
	@ForgeSubscribe
	public void onFall(LivingFallEvent event)
	{
		if (event.entity != null && event.entity instanceof EntityPlayer)
		{
		EntityPlayer player = (EntityPlayer)event.entity;
		AllomancyData data;
		data = AllomancyData.forPlayer(player);
		ItemStack itemstack = player.inventory.armorItemInSlot(2);
		 	if (itemstack != null)
		 	{
		 		if (itemstack.itemID == ModMain.Mistcloak.itemID)
		 		{
		 			event.setCanceled(true);
		 		}
		 	}
		}
	}
}
