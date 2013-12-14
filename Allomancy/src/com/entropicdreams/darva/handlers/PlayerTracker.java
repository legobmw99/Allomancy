package com.entropicdreams.darva.handlers;

import com.entropicdreams.darva.AllomancyData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker {

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}
	@ForgeSubscribe
	 public void onEntityConstruct(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
        	event.entity.registerExtendedProperties(AllomancyData.IDENTIFIER, new AllomancyData());
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
	public void onPlayerRespawn(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

}
