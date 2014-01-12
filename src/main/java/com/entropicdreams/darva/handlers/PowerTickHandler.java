package com.entropicdreams.darva.handlers;


import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.FlyingItem;
import com.entropicdreams.darva.ModMain;
import com.entropicdreams.darva.vector3;

import cpw.mods.fml.common.FMLCommonHandler;  
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PowerTickHandler implements ITickHandler {

	private Entity pointedEntity;
	private Minecraft mc = Minecraft.getMinecraft();
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub

	}
	@ForgeSubscribe
	public void onBlockBreak(BreakEvent event)
	{
				ModMain.MPC.particleBlockTargets.clear();
	}	

	@SideOnly(Side.CLIENT)
	private void clientTick()
	{
		AllomancyData data;
		EntityClientPlayerMP player;
		player = Minecraft.getMinecraft().thePlayer;
		data = AllomancyData.forPlayer(player);
		MovingObjectPosition mop;
		vector3 vec;
		if(data.MetalBurning[data.matIron] || data.MetalBurning[data.matSteel] )
		{
			List<Entity> eList;
			Entity target;
			AxisAlignedBB box;
			box = AxisAlignedBB.getBoundingBox(player.posX-10, player.posY-10, player.posZ-10,player.posX+10 , player.posY+10, player.posZ+10);
			eList = player.worldObj.getEntitiesWithinAABB(Entity.class, box );
			for (Entity curEntity : eList)
			{				
				ModMain.MPC.tryAdd(curEntity);
			}
			
			int xLoc,zLoc, yLoc;
			xLoc= (int) player.posX;
			zLoc = (int) player.posZ;
			yLoc = (int) player.posY;
			
			for(int x = xLoc-5; x<xLoc+5;x++)
			{
				for (int z = zLoc-5; z <zLoc +5; z++)
				{
					for (int y = yLoc -5; y < yLoc+5; y++)
					{
						if  (ModMain.MPC.isBlockMetal(player.worldObj.getBlockId(x, y, z)))
								{
									ModMain.MPC.particleBlockTargets.add(new vector3(x,y,z));
								}
					}
				}
			}
			
			if (player.getCurrentEquippedItem() == null &&  Minecraft.getMinecraft().gameSettings.keyBindAttack.pressed == true)
			{
				
				
				if (data.MetalBurning[data.matIron])
				{
					getMouseOver();
					if (this.pointedEntity != null )
					{
						target = this.pointedEntity;
						ModMain.MPC.tryPullEntity(target);
					}
					if (Minecraft.getMinecraft().objectMouseOver != null)
					{
						if (Minecraft.getMinecraft().objectMouseOver.entityHit != null)
							{
							ModMain.MPC.tryPullEntity(Minecraft.getMinecraft().objectMouseOver.entityHit);
							}
						if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
						{
							mop = Minecraft.getMinecraft().objectMouseOver;
							vec = new vector3(mop.blockX,mop.blockY,mop.blockZ);
							if (ModMain.instance.MPC.isBlockMetal(player.worldObj.getBlockId(vec.X, vec.Y, vec.Z)))
								ModMain.instance.MPC.tryPullBlock(vec);
						}
					}
					

				}
					
			}
			if (player.getCurrentEquippedItem() == null &&  Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed == true)
			{
				if (data.MetalBurning[data.matSteel])
				{
					getMouseOver();
					if (this.pointedEntity != null )
					{
						target = this.pointedEntity;
						ModMain.MPC.tryPushEntity(target);
					}
					
					if (Minecraft.getMinecraft().objectMouseOver != null)
					{
						if (Minecraft.getMinecraft().objectMouseOver.entityHit != null)
							{
							ModMain.MPC.tryPushEntity(Minecraft.getMinecraft().objectMouseOver.entityHit);
							}
						if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
						{
							mop = Minecraft.getMinecraft().objectMouseOver;
							vec = new vector3(mop.blockX,mop.blockY,mop.blockZ);
							if (ModMain.instance.MPC.isBlockMetal(player.worldObj.getBlockId(vec.X, vec.Y, vec.Z)))
								ModMain.instance.MPC.tryPushBlock(vec);
						}

					}
						
				}
					
			}
			
		}
		else
		{
			ModMain.MPC.particleTargets.clear();
		}
		
		if (data.MetalBurning[data.matZinc])
		{
			Entity entity;
			mop = Minecraft.getMinecraft().objectMouseOver;
			if (mop != null && mop.typeOfHit == EnumMovingObjectType.ENTITY && mop.entityHit instanceof EntityCreature && !(mop.entityHit instanceof EntityPlayer) && Minecraft.getMinecraft().gameSettings.keyBindAttack.pressed )
			{
				entity = (EntityLiving) mop.entityHit;
				
				player.sendQueue.addToSendQueue(PacketHandler.changeEmotions(entity.entityId, true));
				
			}
		}
		if (data.MetalBurning[data.matBrass])
		{
			Entity entity;
			mop = Minecraft.getMinecraft().objectMouseOver;
			if (mop != null && mop.typeOfHit == EnumMovingObjectType.ENTITY && mop.entityHit instanceof EntityLiving && !(mop.entityHit instanceof EntityPlayer) && Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed )
			{
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
				
				player.motionX = MathHelper.clamp_float((float) player.motionX, -2, 2);
				player.motionZ = MathHelper.clamp_float((float) player.motionZ, -2,2);
			}			
		}
		if (Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed())
		{
			player.motionY *=1.6;
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
				
				
				if (!data.MetalBurning[data.matPewter] && data.damageStored  >0)
				{
					data.damageStored--;
					curPlayer.attackEntityFrom(DamageSource.generic, 1);
				}

				
				updateBurnTime(data,curPlayer);
				
				if (data.MetalBurning[data.matTin])
				{
					
					if( !curPlayer.isPotionActive(Potion.nightVision.getId()))
						curPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 300));
					else
					{
						PotionEffect eff;
						eff =  curPlayer.getActivePotionEffect(Potion.nightVision);
						if (eff.getDuration() < 201)
						{
							curPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 300));
						}
					}
					
				}
				if (data.MetalBurning[data.matTin] == false && curPlayer.isPotionActive(Potion.nightVision.getId()))
				{
					if(curPlayer.getActivePotionEffect(Potion.nightVision).getDuration() < 201)
					{
						curPlayer.removePotionEffect(Potion.nightVision.getId());	
					}
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
	
	
	/*Ugly below.  Sorry*/
	 public void getMouseOver()
	    {
		 float par1 =0;
	        if (this.mc.renderViewEntity != null)
	        {
	            if (this.mc.theWorld != null)
	            {
	                this.mc.pointedEntityLiving = null;
	                double d0 = 10;
	                this.mc.objectMouseOver = this.mc.renderViewEntity.rayTrace(d0, par1);
	                double d1 = d0;
	                Vec3 vec3 = this.mc.renderViewEntity.getPosition(par1);


	                if (this.mc.objectMouseOver != null)
	                {
	                    d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
	                }

	                Vec3 vec31 = this.mc.renderViewEntity.getLook(par1);
	                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
	                this.pointedEntity = null;
	                float f1 = 1.0F;
	                List list = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(this.mc.renderViewEntity, this.mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
	                double d2 = d1;

	                for (int i = 0; i < list.size(); ++i)
	                {
	                    Entity entity = (Entity)list.get(i);

	                    if (true)
	                    {
	                        float f2 = entity.getCollisionBorderSize();
	                        AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
	                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

	                        if (axisalignedbb.isVecInside(vec3))
	                        {
	                            if (0.0D < d2 || d2 == 0.0D)
	                            {
	                                this.pointedEntity = entity;
	                                d2 = 0.0D;
	                                return;
	                            }
	                        }
	                        else if (movingobjectposition != null)
	                        {
	                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

	                            if (d3 < d2 || d2 == 0.0D)
	                            {
	                                if (entity == this.mc.renderViewEntity.ridingEntity && !entity.canRiderInteract())
	                                {
	                                    if (d2 == 0.0D)
	                                    {
	                                        this.pointedEntity = entity;
	                                        return;
	                                    }
	                                }
	                                else
	                                {
	                                    this.pointedEntity = entity;
	                                    d2 = d3;
	                                    return;
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }	
}
