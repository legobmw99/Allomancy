package common.legobmw99.allomancy.handlers;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import common.legobmw99.allomancy.Allomancy;
import common.legobmw99.allomancy.common.AllomancyCapabilities;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import common.legobmw99.allomancy.network.packets.UpdateBurnPacket;
import common.legobmw99.allomancy.util.vector3;

public class AllomancyTickHandler {
	
	private AllomancyCapabilities cap;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		// Run once per tick, only if in game, and only if there is a player
		if (event.phase == TickEvent.Phase.END&& (!Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().thePlayer != null)) {

			EntityPlayerSP player;
			player = Minecraft.getMinecraft().thePlayer;
        	AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
			RayTraceResult ray;
			RayTraceResult mop;
			vector3 vec;

			if (cap.isMistborn == true) {
				this.updateBurnTime(cap, player);

				if (cap.MetalBurning[AllomancyCapabilities.matIron]
						|| cap.MetalBurning[AllomancyCapabilities.matSteel]) {
					List<Entity> eList;
					
					Entity target;
					AxisAlignedBB box;
					//Add entities to metal list
					box = new AxisAlignedBB((player.posX - 10),(player.posY - 10), (player.posZ - 10), (player.posX + 10), (player.posY + 10), (player.posZ + 10));
					eList = player.worldObj.getEntitiesWithinAABB(Entity.class,
							box);
					for (Entity curEntity : eList) {
						if (curEntity != null
								&& (curEntity instanceof EntityItem || curEntity instanceof EntityLiving || curEntity instanceof EntityGoldNugget))
							Allomancy.XPC.tryAddMetalEntity(curEntity);
					}
					int xLoc, zLoc, yLoc;
					xLoc = (int) player.posX;
					zLoc = (int) player.posZ;
					yLoc = (int) player.posY;
					//Add blocks to metal list
					for (int x = xLoc - 10; x < (xLoc + 10); x++) {
						for (int z = zLoc - 10; z < (zLoc + 10); z++) {
							for (int y = yLoc - 10; y < (yLoc + 10); y++) {
								BlockPos pos1 = new BlockPos(x, y, z);
								vec = new vector3(pos1);
								if (Allomancy.XPC.isBlockMetal(Minecraft
										.getMinecraft().theWorld
										.getBlockState(vec.pos))) {
									Allomancy.XPC.particleBlockTargets.add(vec);
								}
							}
						}
					}
				}	else {
						Allomancy.XPC.particleTargets.clear();
					}
				if ((player.getHeldItemMainhand() == null)
						&& (Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown() == true)) {
					//Ray trace 20 blocks
					RayTraceResult mov = getMouseOverExtended(20.0F);
					//All iron pulling powers
					if (cap.MetalBurning[AllomancyCapabilities.matIron]) {
						if (mov != null) {
							if (mov.entityHit != null) {
								Allomancy.XPC.tryPullEntity(mov.entityHit);
							}
						}
						ray = player.rayTrace(20.0F, 0.0F);
						if (ray != null) {
							if (ray.typeOfHit == RayTraceResult.Type.BLOCK || ray.typeOfHit == RayTraceResult.Type.MISS) {
								vec = new vector3(ray.getBlockPos());
								if (Allomancy.XPC.isBlockMetal(Minecraft.getMinecraft().theWorld.getBlockState(vec.pos))) {
									Allomancy.XPC.tryPullBlock(vec);
								}
							}

						}

					}
					//All zinc powers
					if (cap.MetalBurning[AllomancyCapabilities.matZinc]) {
						Entity entity;
						if ((mov != null)
								&& (mov.entityHit != null)
								&& (mov.entityHit instanceof EntityCreature)
								&& !(mov.entityHit instanceof EntityPlayer)) {

							entity = mov.entityHit;
							Registry.network.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), true));

							}
						}

					}
				if ((player.getHeldItemOffhand() == null)
						&& (Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown() == true)) {
					//Ray trace 20 blocks
					RayTraceResult mov = getMouseOverExtended(20.0F);
					//All steel pushing powers
					if (cap.MetalBurning[AllomancyCapabilities.matSteel]) {
						if (mov != null) {
							if (mov.entityHit != null) {
								Allomancy.XPC.tryPushEntity(mov.entityHit);
							}
						}
						ray = player.rayTrace(20.0F, 0.0F);
						if (ray != null) {
							if (ray.typeOfHit == RayTraceResult.Type.BLOCK
									|| ray.typeOfHit == RayTraceResult.Type.MISS) {
								vec = new vector3(ray.getBlockPos());
								if (Allomancy.XPC.isBlockMetal(Minecraft.getMinecraft().theWorld.getBlockState(vec.pos))) {
									Allomancy.XPC.tryPushBlock(vec);
								}
							}

						}

					}
					//All brass powers
					if (cap.MetalBurning[AllomancyCapabilities.matBrass]) {
						Entity entity;
						if ((mov != null)
								&& (mov.entityHit != null)
								&& (mov.entityHit instanceof EntityCreature)
								&& !(mov.entityHit instanceof EntityPlayer)) {
							entity = mov.entityHit;
							Registry.network.sendToServer(new ChangeEmotionPacket(
												entity.getEntityId(), false));

							}
						}

					}
				
				//Pewter's speed powers
				if (cap.MetalBurning[AllomancyCapabilities.matPewter]) {
					if ((player.onGround == true)
							&& (player.isInWater() == false)
							&& (Minecraft.getMinecraft().gameSettings.keyBindForward
									.isKeyDown())) {
						player.motionX *= 1.2;
						player.motionZ *= 1.2;

						//Don't allow motion values to get too out of the norm
						player.motionX = MathHelper.clamp_float((float) player.motionX, -2, 2);
						player.motionZ = MathHelper.clamp_float((float) player.motionZ, -2, 2);
					}
					if (Minecraft.getMinecraft().gameSettings.keyBindJump
							.isPressed()) {
						if (player.motionY >= 0) {
							player.motionY *= 1.6;
							//Don't allow motion values to get too out of the norm
							player.motionY = MathHelper.clamp_float((float) player.motionY, -2, 2);
						}
						player.motionX *= 1.4;
						player.motionZ *= 1.4;
						//Don't allow motion values to get too out of the norm
						player.motionX = MathHelper.clamp_float((float) player.motionX, -2, 2);
						player.motionZ = MathHelper.clamp_float((float) player.motionZ, -2, 2);
					}

				}
			}

			LinkedList<Entity> toRemove = new LinkedList<Entity>();

			for (Entity entity : Allomancy.XPC.particleTargets) {

				if (entity.isDead == true) {
					toRemove.add(entity);
				}
				if (player == null) {
					return;
				}
				if (player.getDistanceToEntity(entity) > 10) {
					toRemove.add(entity);
				}
			}

			for (Entity entity : toRemove) {
				Allomancy.XPC.particleTargets.remove(entity);
			}
			toRemove.clear();
		}
	}
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {

			World world;
			world = (World) event.world;
			
			List<EntityPlayer> list = world.playerEntities;
			for (EntityPlayer curPlayer : list) {
				cap = AllomancyCapabilities.forPlayer(curPlayer);
	   
	            		
				if (cap.isMistborn == true) {
					//Damage the player if they have stored damage and pewter cuts out
					if (!cap.MetalBurning[AllomancyCapabilities.matPewter]
							&& (cap.damageStored > 0)) {
						cap.damageStored--;
						curPlayer.attackEntityFrom(DamageSource.generic, 2);
					}
					if (cap.MetalBurning[AllomancyCapabilities.matTin]) {
						//Add night vision to tin-burners
						if (curPlayer.isPotionActive(Potion.getPotionById(16)) == false) { //Potion 16 = night vision
							curPlayer.addPotionEffect(new PotionEffect(
									Potion.getPotionById(16), 300, 0, false, false));
						}
						//Remove blindness for tin burners
						if (curPlayer.isPotionActive(Potion.getPotionById(15))) { //Potion 15 is blindness
							curPlayer.removePotionEffect(Potion.getPotionById(15));

						} else {
							PotionEffect eff;
							eff = curPlayer
									.getActivePotionEffect(Potion.getPotionById(16));
							//Fix for the flashing that occurs when night vision effect is about to run out
							if (eff.getDuration() < 210) {
								curPlayer.addPotionEffect(new PotionEffect(
										Potion.getPotionById(16), 300, 0, false, false));
							}
						}

					}
					//Remove night vision from non-tin burners if duration < 10 seconds. Related to the above issue with flashing
					if ((cap.MetalBurning[AllomancyCapabilities.matTin] == false)
							&& curPlayer.isPotionActive(Potion.getPotionById(16))) {
						if (curPlayer.getActivePotionEffect(Potion.getPotionById(16))
								.getDuration() < 201) {
							curPlayer.removePotionEffect(Potion.getPotionById(16));
						}
					}
					if(cap.MetalBurning[AllomancyCapabilities.matCopper] == false){
						if(cap.MetalBurning[AllomancyCapabilities.matIron] || cap.MetalBurning[AllomancyCapabilities.matSteel] || cap.MetalBurning[AllomancyCapabilities.matTin] || cap.MetalBurning[AllomancyCapabilities.matPewter] || cap.MetalBurning[AllomancyCapabilities.matZinc] || cap.MetalBurning[AllomancyCapabilities.matBrass] || cap.MetalBurning[AllomancyCapabilities.matBronze]){
						//TODO:bronze stuff here, probably a packet
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateBurnTime(AllomancyCapabilities data, EntityPlayerSP player) {
		cap = AllomancyCapabilities.forPlayer(player);
		//Checks each metal, reduces MetalAmounts by 1 each time BurnTime ticks to 0 
		for (int i = 0; i < 8; i++) {

			if (cap.MetalBurning[i]) {
				cap.BurnTime[i]--;
				if (cap.BurnTime[i] == 0) {
					cap.BurnTime[i] = data.MaxBurnTime[i];
					cap.MetalAmounts[i]--;
					Registry.network.sendToServer(new UpdateBurnPacket(i,
							data.MetalBurning[i]));
					if (cap.MetalAmounts[i] == 0) {
						//Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
						data.MetalBurning[i] = false;
						Registry.network.sendToServer(new UpdateBurnPacket(i,
								data.MetalBurning[i]));
					}
				}

			}
		}
	}
	//This code is based almost entirely on the vanilla code. It's not super well documented, but basically it just runs a ray-trace. Edit at your own peril
	@SideOnly(Side.CLIENT)
	public static RayTraceResult getMouseOverExtended(float dist) {
		Minecraft mc = FMLClientHandler.instance().getClient();
		Entity theRenderViewEntity = mc.getRenderViewEntity();
		AxisAlignedBB theViewBoundingBox = new AxisAlignedBB(
				theRenderViewEntity.posX - 0.5D,
				theRenderViewEntity.posY - 0.0D,
				theRenderViewEntity.posZ - 0.5D,
				theRenderViewEntity.posX + 0.5D,
				theRenderViewEntity.posY + 1.5D,
				theRenderViewEntity.posZ + 0.5D);
		RayTraceResult returnMOP = null;
		if (mc.theWorld != null) {
			double var2 = dist;
			returnMOP = theRenderViewEntity.rayTrace(var2, 0);
			double calcdist = var2;
			Vec3d pos = theRenderViewEntity.getPositionEyes(0);
			var2 = calcdist;
			if (returnMOP != null) {
				calcdist = returnMOP.hitVec.distanceTo(pos);
			}

			Vec3d lookvec = theRenderViewEntity.getLook(0);
			Vec3d var8 = pos.addVector(lookvec.xCoord * var2, lookvec.yCoord
					* var2, lookvec.zCoord * var2);
			Entity pointedEntity = null;
			float var9 = 1.0F;
			@SuppressWarnings("unchecked")
			List<Entity> list = mc.theWorld
					.getEntitiesWithinAABBExcludingEntity(
							theRenderViewEntity,
							theViewBoundingBox.addCoord(lookvec.xCoord * var2,
									lookvec.yCoord * var2,
									lookvec.zCoord * var2).expand(var9, var9,
									var9));
			double d = calcdist;

			for (Entity entity : list) {
				{
					float bordersize = entity.getCollisionBorderSize();
					AxisAlignedBB aabb = new AxisAlignedBB(entity.posX
							- entity.width / 2, entity.posY, entity.posZ
							- entity.width / 2, entity.posX + entity.width / 2,
							entity.posY + entity.height, entity.posZ
									+ entity.width / 2);
					aabb.expand(bordersize, bordersize, bordersize);
					RayTraceResult mop0 = aabb.calculateIntercept(pos,
							var8);

					if (aabb.isVecInside(pos)) {
						if (0.0D < d || d == 0.0D) {
							pointedEntity = entity;
							d = 0.0D;
						}
					} else if (mop0 != null) {
						double d1 = pos.distanceTo(mop0.hitVec);

						if (d1 < d || d == 0.0D) {
							pointedEntity = entity;
							d = d1;
						}
					}
				}
			}

			if (pointedEntity != null && (d < calcdist || returnMOP == null)) {
				returnMOP = new RayTraceResult(pointedEntity);
			}
		}
		return returnMOP;
	}
}

