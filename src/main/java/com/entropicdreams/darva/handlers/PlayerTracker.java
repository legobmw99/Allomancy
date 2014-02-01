package com.entropicdreams.darva.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.entropicdreams.darva.AllomancyData;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PlayerTracker implements IPlayerTracker {

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		NBTTagCompound old = player.getEntityData();
		if (old.hasKey("Allomancy_Data")) {
			player.getEntityData().setCompoundTag("Allomancy_Data",
					old.getCompoundTag("Allomancy_Data"));
		}

	}

	@ForgeSubscribe
	public void onEntityConstruct(EntityEvent.EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			event.entity.registerExtendedProperties(AllomancyData.IDENTIFIER,
					new AllomancyData((EntityPlayer) event.entity));
		}
	}

	@ForgeSubscribe
	public void onPlayerLogin(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			AllomancyData data = AllomancyData.forPlayer(event.entity);
			PacketDispatcher.sendPacketToPlayer(
					PacketHandler.updateAllomancyData(data),
					(Player) event.entity);
			if (AllomancyData.isMistborn == true) {
				PacketDispatcher.sendPacketToPlayer(
						PacketHandler.becomeMistborn(), (Player) event.entity);
			}
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			AllomancyData data = AllomancyData.forPlayer(player);
			PacketDispatcher.sendPacketToPlayer(
					PacketHandler.updateAllomancyData(data), (Player) player);
			if (AllomancyData.isMistborn == true) {
				PacketDispatcher.sendPacketToPlayer(
						PacketHandler.becomeMistborn(), (Player) player);
			}
		}
	}

}
