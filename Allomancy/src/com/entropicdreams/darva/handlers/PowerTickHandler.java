package com.entropicdreams.darva.handlers;


import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.ModMain;

import cpw.mods.fml.common.FMLCommonHandler;  
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PowerTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub

	}

	@SideOnly(Side.CLIENT)
	private void clientTick()
	{
		AllomancyData data;
		EntityClientPlayerMP player;
		player = Minecraft.getMinecraft().thePlayer;
		data = AllomancyData.forPlayer(player);
		
		if(data.MetalBurning[data.matIron] || data.MetalBurning[data.matSteel] )
		{
			List<Entity> eList;
			AxisAlignedBB box;
			box = AxisAlignedBB.getBoundingBox(player.posX-10, player.posY-10, player.posZ-10,player.posX+10 , player.posY+10, player.posZ+10);
			eList = player.worldObj.getEntitiesWithinAABB(Entity.class, box );
			for (Entity curEntity : eList)
			{
				
				ModMain.MPC.tryAdd(curEntity);
			}
			
		}
		else
		{
			ModMain.MPC.particleTargets.clear();
		}
		
		if (data.MetalBurning[data.matZinc])
		{
			Entity entity;
			MovingObjectPosition mop;
			mop = Minecraft.getMinecraft().objectMouseOver;
			if (mop != null && mop.typeOfHit == EnumMovingObjectType.ENTITY && mop.entityHit instanceof EntityCreature && !(mop.entityHit instanceof EntityPlayer) )
			{
				entity = (EntityLiving) mop.entityHit;
				
				player.sendQueue.addToSendQueue(PacketHandler.changeEmotions(entity.entityId, true));
				
			}
		}
		if (data.MetalBurning[data.matBrass])
		{
			Entity entity;
			MovingObjectPosition mop;
			mop = Minecraft.getMinecraft().objectMouseOver;
			if (mop != null && mop.typeOfHit == EnumMovingObjectType.ENTITY && mop.entityHit instanceof EntityLiving && !(mop.entityHit instanceof EntityPlayer) )
			{
				System.out.println("here...");
				entity = (EntityLiving) mop.entityHit;
				
				player.sendQueue.addToSendQueue(PacketHandler.changeEmotions(entity.entityId, false));
				
			}
		}

		if (data.MetalBurning[data.matPewter])
		{
			if (player.onGround == true)
			{
				player.motionX *=1.4;
				player.motionZ *=1.4;
				
			}
			if (Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed())
			{
				player.motionY *=1.6;
			}
			
		}
		
		

	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		AllomancyData data;
		
		
		
		if (type.contains(TickType.PLAYER))
		{
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			if(side == Side.CLIENT)
			clientTick();
		}
		else
		{
			World world;
			world = (World) tickData[0];

		
			List <EntityPlayerMP> list = world.playerEntities;

			
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
		return EnumSet.of(TickType.WORLD, TickType.PLAYER);
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
