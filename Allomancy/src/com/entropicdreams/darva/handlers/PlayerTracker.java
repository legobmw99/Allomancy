package com.entropicdreams.darva.handlers;

import com.entropicdreams.darva.AllomancyData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PlayerTracker  {

	public void onPlayerLogin(EntityPlayer player) {
		// TODO Auto-generated method stub
		if (player instanceof EntityPlayerMP)
		{
			PacketDispatcher.sendPacketToPlayer(PacketHandler.updateAllomancyData(AllomancyData.forPlayer(player)), (Player) player);
			System.out.println("sent");
		}

		
	}


}
