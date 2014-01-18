package com.entropicdreams.darva.handlers;

import com.entropicdreams.darva.AllomancyData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PlayerTracker {

	@ForgeSubscribe
	public void onPlayerLogin(EntityJoinWorldEvent event) {
		// TODO Auto-generated method stub
		if (event.entity instanceof EntityPlayerMP) {
			PacketDispatcher.sendPacketToPlayer(
					PacketHandler.updateAllomancyData(AllomancyData
							.forPlayer(event.entity)), (Player) event.entity);
		}
	}

	@ForgeSubscribe
	public void onEntityConstruct(EntityEvent.EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			event.entity.registerExtendedProperties(AllomancyData.IDENTIFIER,
					new AllomancyData((EntityPlayer) event.entity));
		}
	}

}
