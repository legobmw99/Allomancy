package com.entropicdreams.darva.handlers;


import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.entropicdreams.darva.AllomancyData;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PowerTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		AllomancyData data;
		World world;
		world = (World) tickData[0];
		
		if (world.isRemote)
		{
			EntityClientPlayerMP player;
			player = Minecraft.getMinecraft().thePlayer;
			data = AllomancyData.forPlayer(player);
			if (data.MetalBurning[data.matPewter])
			{
				
			}
			
		}
		else
		{
		
			List <EntityPlayerMP> list = MinecraftServer.getServer().getConfigurationManager().playerEntityList;

			
			for(EntityPlayerMP curPlayer : list )
			{
				data = AllomancyData.forPlayer(curPlayer);
				
				updateBurnTime(data,curPlayer);
				
				if (data.MetalBurning[data.matTin])
				{
					
					if( !curPlayer.isPotionActive(Potion.nightVision.getId()))
						curPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 1000));
					else
					{
						PotionEffect eff;
						eff =  curPlayer.getActivePotionEffect(Potion.nightVision);
						if (eff.getDuration() < 400)
						{
							curPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 1000));
						}
					}
					
				}
				if (data.MetalBurning[data.matTin] == false && curPlayer.isPotionActive(Potion.nightVision.getId()))
				{
					curPlayer.removePotionEffect(Potion.nightVision.getId());
				}
				
			}
		}
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "Power Handler";
	}

	private void updateBurnTime(AllomancyData data, EntityPlayerMP player)
	{
		data = AllomancyData.forPlayer(player);
		
		for (int i = 0; i < 8; i++)
		{
			if (data.MetalBurning[i])
			{
				data.BurnTime[i]--;
				if (data.BurnTime[i] == 0)
				{
					System.out.println("Burned one");
					data.BurnTime[i] = data.MaxBurnTime[i];
					data.MetalAmounts[i]--;
					PacketDispatcher.sendPacketToPlayer(PacketHandler.updateAllomancyData(data), (Player)player);
					if (data.MetalAmounts[i] == 0)
					{
						data.MetalBurning[i] = false;
						PacketDispatcher.sendPacketToPlayer(PacketHandler.changeBurn(i, false), (Player)player);
					}
				}

			}
		}
		
	}
}
