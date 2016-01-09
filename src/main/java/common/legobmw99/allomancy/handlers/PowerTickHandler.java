package common.legobmw99.allomancy.handlers;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import common.legobmw99.allomancy.Allomancy;
import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.network.packets.AllomancyBecomeMistbornPacket;
import common.legobmw99.allomancy.network.packets.AllomancyDataPacket;
import common.legobmw99.allomancy.network.packets.AllomancySelectMetalPacket;
import common.legobmw99.allomancy.network.packets.AllomancyStopFallPacket;
import common.legobmw99.allomancy.network.packets.AllomancyUpdateBurnPacket;
import common.legobmw99.allomancy.particle.ParticleMetal;
import common.legobmw99.allomancy.util.vector3;

public class PowerTickHandler {

	private Entity pointedEntity;
	private Minecraft mc;
	private ResourceLocation meterLoc;
	private AllomancyData data;
	private int animationCounter = 0;
	private int currentFrame = 0;
	private Point[] Frames = { new Point(72, 0), new Point(72, 4),
			new Point(72, 8), new Point(72, 12) };


	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		//Allomancy.MPC.particleBlockTargets.clear();
	}
	
	@SubscribeEvent
	public void onCrafting(ItemCraftedEvent event) {
		ItemStack cur;
		for (int x = 0; x < event.craftMatrix.getSizeInventory(); x++) {
			cur = event.craftMatrix.getStackInSlot(x);
			if (cur == null)
				continue;
			if (cur == new ItemStack(Registry.itemAllomancyGrinder)) {
				cur.damageItem(1, event.player);
			}

		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		NBTTagCompound old = event.player.getEntityData();
		if (old.hasKey("Allomancy_Data")) {
			event.player.getEntityData().setTag("Allomancy_Data",
					old.getCompoundTag("Allomancy_Data"));
		}

	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
	        if(Registry.changeGroup.isPressed()){
				System.out.println("key");

	        	EntityPlayerSP player;
	    		player = Minecraft.getMinecraft().thePlayer;
	    		Minecraft mc = FMLClientHandler.instance().getClient();
	    		if (mc.currentScreen == null) {
	    			if ((player == null) || !Minecraft.getMinecraft().inGameHasFocus) {
	    				return;
	    			}
	    				AllomancyData data = AllomancyData.forPlayer(player);
	    				data.setSelected(data.getSelected() + 1);
	    				Allomancy.packetPipeline.sendToServer(new AllomancySelectMetalPacket(data.getSelected()));
	    			}
	    		}	        
	        if(Registry.burnFirst.isPressed()){
	        	EntityPlayerSP player;
	    		player = Minecraft.getMinecraft().thePlayer;
	    		AllomancyData data;
	    		Minecraft mc = FMLClientHandler.instance().getClient();
	    		if (mc.currentScreen == null) {
	    			if (player == null) {
	    				return;
	    			}
	    				data = AllomancyData.forPlayer(player);
	    				switch (data.getSelected()) {
	    				case 1:
	    					// toggle iron.
	    					if (data.MetalAmounts[AllomancyData.matIron] > 0) {
	    						data.MetalBurning[AllomancyData.matIron] = !data.MetalBurning[AllomancyData.matIron];
	    					}
	    					Allomancy.packetPipeline.sendToServer(new AllomancyUpdateBurnPacket(AllomancyData.matIron,
	    							data.MetalBurning[AllomancyData.matIron]));

	    					break;
	    				case 2:
	    					// toggle Tin.
	    					if (data.MetalAmounts[AllomancyData.matTin] > 0) {
	    						data.MetalBurning[AllomancyData.matTin] = !data.MetalBurning[AllomancyData.matTin];
	    					}
	    					Allomancy.packetPipeline.sendToServer(new AllomancyUpdateBurnPacket(AllomancyData.matTin,
	    							data.MetalBurning[AllomancyData.matTin]));

	    					break;
	    				case 3:
	    					// toggle Copper.
	    					if (data.MetalAmounts[AllomancyData.matCopper] > 0) {
	    						data.MetalBurning[AllomancyData.matCopper] = !data.MetalBurning[AllomancyData.matCopper];
	    					}
	    					Allomancy.packetPipeline.sendToServer(new AllomancyUpdateBurnPacket(AllomancyData.matCopper,
	    							data.MetalBurning[AllomancyData.matCopper]));

	    					break;
	    				case 4:
	    					// toggle Zinc.
	    					if (data.MetalAmounts[AllomancyData.matZinc] > 0) {
	    						data.MetalBurning[AllomancyData.matZinc] = !data.MetalBurning[AllomancyData.matZinc];
	    					}
	    					Allomancy.packetPipeline.sendToServer(new AllomancyUpdateBurnPacket(AllomancyData.matZinc,
	    							data.MetalBurning[AllomancyData.matZinc]));

	    					break;
	    				default:
	    					break;
	    				}
	    			}
	    		}	        
	        if(Registry.burnSecond.isPressed()){
	        	EntityPlayerSP player;
	    		player = Minecraft.getMinecraft().thePlayer;
	    		AllomancyData data;
	    		Minecraft mc = FMLClientHandler.instance().getClient();
	    		if (mc.currentScreen == null) {
	    			if (player == null) {
	    				return;
	    			}

	    				data = AllomancyData.forPlayer(player);
	    				switch (data.getSelected()) {
	    				case 1:
	    					// toggle Steel.
	    					if (data.MetalAmounts[AllomancyData.matSteel] > 0) {
	    						data.MetalBurning[AllomancyData.matSteel] = !data.MetalBurning[AllomancyData.matSteel];
	    					}
	    					Allomancy.packetPipeline.sendToServer(new AllomancyUpdateBurnPacket(AllomancyData.matSteel,
	    							data.MetalBurning[AllomancyData.matSteel]));

	    					break;
	    				case 2:
	    					// toggle Pewter.
	    					if (data.MetalAmounts[AllomancyData.matPewter] > 0) {
	    						data.MetalBurning[AllomancyData.matPewter] = !data.MetalBurning[AllomancyData.matPewter];
	    					}
	    					Allomancy.packetPipeline.sendToServer(new AllomancyUpdateBurnPacket(AllomancyData.matPewter,
	    							data.MetalBurning[AllomancyData.matPewter]));
	    					break;
	    				case 3:
	    					// toggle Bronze.
	    					if (data.MetalAmounts[AllomancyData.matBronze] > 0) {
	    						data.MetalBurning[AllomancyData.matBronze] = !data.MetalBurning[AllomancyData.matBronze];
	    					}
	    					Allomancy.packetPipeline.sendToServer(new AllomancyUpdateBurnPacket(AllomancyData.matBronze,
	    							data.MetalBurning[AllomancyData.matBronze]));
	    					break;
	    				case 4:
	    					// toggle Brass.
	    					if (data.MetalAmounts[AllomancyData.matBrass] > 0) {
	    						data.MetalBurning[AllomancyData.matBrass] = !data.MetalBurning[AllomancyData.matBrass];
	    					}
	    					Allomancy.packetPipeline.sendToServer(new AllomancyUpdateBurnPacket(AllomancyData.matBrass,
	    							data.MetalBurning[AllomancyData.matBrass]));
	    					break;
	    				default:
	    					break;
	    				}
	    			}
	    		}	       
	    	}
	    
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			AllomancyData data = AllomancyData.forPlayer(event.player);
			Allomancy.packetPipeline.sendTo(new
			AllomancyDataPacket(AllomancyData.forPlayer(event.player)),
			(EntityPlayerMP) event.player);
			if (data.isMistborn == true) {
				Allomancy.packetPipeline.sendTo(
						new AllomancyBecomeMistbornPacket(),
						(EntityPlayerMP) event.player);

			}
		}
	}
	@SideOnly(Side.CLIENT)
	private void clientTick() {
		AllomancyData data;
		EntityPlayerSP player;
		player = Minecraft.getMinecraft().thePlayer;
		data = AllomancyData.forPlayer(player);
		MovingObjectPosition mop;
		vector3 vec;
		if (AllomancyData.isMistborn == true) {
			if (data.MetalBurning[AllomancyData.matIron]
					|| data.MetalBurning[AllomancyData.matSteel]) {
				List<Entity> eList;
				Entity target;
				AxisAlignedBB box;
				box = AxisAlignedBB.fromBounds(player.posX - 10,
						player.posY - 10, player.posZ - 10, player.posX + 10,
						player.posY + 10, player.posZ + 10);
				eList = player.worldObj
						.getEntitiesWithinAABB(Entity.class, box);
				for (Entity curEntity : eList) {
					Allomancy.MPC.tryAdd(curEntity);
				}

				int xLoc, zLoc, yLoc;
				xLoc = (int) player.posX;
				zLoc = (int) player.posZ;
				yLoc = (int) player.posY;

				for (int x = xLoc - 10; x < (xLoc + 10); x++) {
					for (int z = zLoc - 10; z < (zLoc + 10); z++) {
						for (int y = yLoc - 10; y < (yLoc + 10); y++) {
					        BlockPos pos1 = new BlockPos(x, y, z);
							if (Allomancy.MPC.isBlockMetal(player.worldObj
									.getBlockState(pos1).getBlock().getDefaultState())) {
								Allomancy.MPC.particleBlockTargets
										.add(new vector3(x, y, z));
							}
						}
					}
				}

				if ((player.getCurrentEquippedItem() == null)
						&& (Minecraft.getMinecraft().gameSettings.keyBindAttack.isPressed() == true)) {

					if (data.MetalBurning[AllomancyData.matIron]) {
						this.getMouseOver();
						if (this.pointedEntity != null) {
							target = this.pointedEntity;
							Allomancy.MPC.tryPullEntity(target);
						}
						if (Minecraft.getMinecraft().objectMouseOver != null) {
							if (Minecraft.getMinecraft().objectMouseOver.entityHit != null) {
								Allomancy.MPC
										.tryPullEntity(Minecraft.getMinecraft().objectMouseOver.entityHit);
							}
							if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
								mop = Minecraft.getMinecraft().objectMouseOver;
								vec = new vector3(mop.getBlockPos());
								if (Allomancy.MPC.isBlockMetal(player.worldObj
										.getBlockState(vec.pos))) {
									Allomancy.MPC.tryPullBlock(vec);
								}
							}
						}

					}

				}
				if ((player.getCurrentEquippedItem() == null)
						&& (Minecraft.getMinecraft().gameSettings.keyBindUseItem.isPressed() == true)) {
					if (data.MetalBurning[AllomancyData.matSteel]) {
						this.getMouseOver();
						if (this.pointedEntity != null) {
							target = this.pointedEntity;
							Allomancy.MPC.tryPushEntity(target);
						}

						if (Minecraft.getMinecraft().objectMouseOver != null) {
							if (Minecraft.getMinecraft().objectMouseOver.entityHit != null) {
								Allomancy.MPC
										.tryPushEntity(Minecraft.getMinecraft().objectMouseOver.entityHit);
							}
							if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
								mop = Minecraft.getMinecraft().objectMouseOver;
								vec = new vector3(mop.getBlockPos());
								if (Allomancy.MPC.isBlockMetal(player.worldObj
										.getBlockState(vec.pos))) {
									Allomancy.MPC.tryPushBlock(vec);
								}
							}

						}

					}

				}

			} else {
				//Allomancy.MPC.particleTargets.clear();
			}

			if (data.MetalBurning[AllomancyData.matZinc]) {
				Entity entity;
				mop = Minecraft.getMinecraft().objectMouseOver;
				if ((mop != null)
						&& (mop.typeOfHit == MovingObjectType.ENTITY)
						&& (mop.entityHit instanceof EntityCreature)
						&& !(mop.entityHit instanceof EntityPlayer)
						&& Minecraft.getMinecraft().gameSettings.keyBindAttack.isPressed()) {
					entity = mop.entityHit;

					//player.sendQueue.addToSendQueue(PacketHandler.changeEmotions(entity.entityId, true));

				}
			}
			if (data.MetalBurning[AllomancyData.matBrass]) {
				Entity entity;
				mop = Minecraft.getMinecraft().objectMouseOver;
				if ((mop != null)
						&& (mop.typeOfHit == MovingObjectType.ENTITY)
						&& (mop.entityHit instanceof EntityCreature)
						&& !(mop.entityHit instanceof EntityPlayer)
						&& Minecraft.getMinecraft().gameSettings.keyBindUseItem.isPressed()) {
					entity = mop.entityHit;

					//player.sendQueue.addToSendQueue(PacketHandler.changeEmotions(entity.entityId, false));

				}
			}

			if (data.MetalBurning[AllomancyData.matPewter]) {
				if ((player.onGround == true)
						&& (player.isInWater() == false)
						&& (Minecraft.getMinecraft().gameSettings.keyBindForward.isPressed())) {
					player.motionX *= 1.4;
					player.motionZ *= 1.4;

					player.motionX = MathHelper.clamp_float(
							(float) player.motionX, -2, 2);
					player.motionZ = MathHelper.clamp_float(
							(float) player.motionZ, -2, 2);
				}
				if (Minecraft.getMinecraft().gameSettings.keyBindJump
						.isPressed()) {
					if (player.motionY >= 0) {
						player.motionY *= 1.6;
					}
					player.motionX *= 1.4;
					player.motionZ *= 1.4;
				}

			}
		}
	}
	
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(AllomancyData.IDENTIFIER) == null){
				event.entity.registerExtendedProperties(AllomancyData.IDENTIFIER, new AllomancyData((EntityPlayer) event.entity));
		}
	}
	
	
	
	
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
		NBTTagCompound old = event.entity.getEntityData();
		if (old.hasKey("Allomancy_Data")) {
			event.entity.getEntityData().merge(old.getCompoundTag("Allomancy_Data"));
		}
		}
	}
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		AllomancyData data;

		//LinkedList<Entity> toRemove = new LinkedList<Entity>();

		/*for (Entity entity : Allomancy.MCP.particleTargets) {

			if (entity.isDead == true) {
				toRemove.add(entity);
			}

			if (player.getDistanceToEntity(entity) > 10) {
				toRemove.add(entity);
			}
		}
		for (Entity entity : toRemove) {
			Allomancy.MCP.particleTargets.remove(entity);
		}
		Allomancy.MCP.particleBlockTargets.clear();*/
		//toRemove.clear();
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			if (player == null) {
				return;
			
			}
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event){
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			if (side == Side.CLIENT) {
				this.clientTick();
			}
			MinecraftServer mcs;
			mcs = MinecraftServer.getServer();
			World world;
			world = (World) mcs.getEntityWorld();

			List<EntityPlayerMP> list = world.playerEntities;

			for (EntityPlayerMP curPlayer : list) {
				data = AllomancyData.forPlayer(curPlayer);

				if (AllomancyData.isMistborn == true) {
					if (!data.MetalBurning[AllomancyData.matPewter]
							&& (data.damageStored > 0)) {
						data.damageStored--;
						curPlayer.attackEntityFrom(DamageSource.generic, 2);
					}
					this.updateBurnTime(data, curPlayer);
					if (data.MetalBurning[AllomancyData.matTin]) {

						if (!curPlayer.isPotionActive(Potion.nightVision
								.getId())) {
							curPlayer.addPotionEffect(new PotionEffect(
									Potion.nightVision.getId(), 300, 0, true, false));
						}
						if (curPlayer.isPotionActive(Potion.blindness.getId())) {
							curPlayer.removePotionEffect(Potion.blindness
									.getId());

						} else {
							PotionEffect eff;
							eff = curPlayer
									.getActivePotionEffect(Potion.nightVision);
							if (eff.getDuration() < 210) {
								curPlayer.addPotionEffect(new PotionEffect(
										Potion.nightVision.getId(), 300, 0,
										true, false));
							}
						}

					}
					if ((data.MetalBurning[AllomancyData.matTin] == false)
							&& curPlayer.isPotionActive(Potion.nightVision
									.getId())) {
						if (curPlayer.getActivePotionEffect(Potion.nightVision)
								.getDuration() < 201) {
							curPlayer.removePotionEffect(Potion.nightVision
									.getId());
						}
					}
				}
			}
		}
			
	
	@SubscribeEvent
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

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
    	this.mc = Minecraft.getMinecraft();
    	this.meterLoc = new ResourceLocation("allomancy","textures/overlay/meter.png");
    	
    	ParticleMetal particle;
		if (!Minecraft.getMinecraft().inGameHasFocus) {
			return;
		}
		if (FMLClientHandler.instance().getClient().currentScreen != null) {
			return;
		}
		EntityPlayerSP player;
		player = this.mc.thePlayer;
		if (player == null) {
			return;
		}

		this.animationCounter++;

		this.data = AllomancyData.forPlayer(player);
		// left hand side.
		int ironY, steelY, tinY, pewterY;
		// right hand side
		int copperY, bronzeY, zincY, brassY;
		if (!data.isMistborn) {
			return;
		}
		GuiIngame gig = new GuiIngame(Minecraft.getMinecraft());
		Minecraft.getMinecraft().renderEngine.bindTexture(this.meterLoc);
		ITextureObject obj;
		obj = Minecraft.getMinecraft().renderEngine.getTexture(this.meterLoc);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

		switch (this.data.getSelected()) {
		case 0:
			break;
		case 1:
			gig.drawTexturedModalRect(3, 13, 54, 0, 16, 24);
			break;
		case 2:
			gig.drawTexturedModalRect(28, 13, 54, 0, 16, 24);
			break;
		case 3:
			gig.drawTexturedModalRect(53, 13, 54, 0, 16, 24);
			break;
		case 4:
			gig.drawTexturedModalRect(78, 13, 54, 0, 16, 24);
			break;

		}

		ironY = 9 - this.data.MetalAmounts[AllomancyData.matIron];
		gig.drawTexturedModalRect(6, 20 + ironY, 7, 1 + ironY, 3, 10 - ironY);

		steelY = 9 - this.data.MetalAmounts[AllomancyData.matSteel];
		gig.drawTexturedModalRect(13, 20 + steelY, 13, 1 + steelY, 3,
				10 - steelY);

		tinY = 9 - this.data.MetalAmounts[AllomancyData.matTin];
		gig.drawTexturedModalRect(31, 20 + tinY, 19, 1 + tinY, 3, 10 - tinY);

		pewterY = 9 - this.data.MetalAmounts[AllomancyData.matPewter];
		gig.drawTexturedModalRect(38, 20 + pewterY, 25, 1 + pewterY, 3,
				10 - pewterY);

		copperY = 9 - this.data.MetalAmounts[AllomancyData.matCopper];
		gig.drawTexturedModalRect(56, 20 + copperY, 31, 1 + copperY, 3,
				10 - copperY);

		bronzeY = 9 - this.data.MetalAmounts[AllomancyData.matBronze];
		gig.drawTexturedModalRect(63, 20 + bronzeY, 37, 1 + bronzeY, 3,
				10 - bronzeY);

		zincY = 9 - this.data.MetalAmounts[AllomancyData.matZinc];
		gig.drawTexturedModalRect(81, 20 + zincY, 43, 1 + zincY, 3, 10 - zincY);

		brassY = 9 - this.data.MetalAmounts[AllomancyData.matBrass];
		gig.drawTexturedModalRect(88, 20 + brassY, 49, 1 + brassY, 3,
				10 - brassY);

		// Draw the gauges second, so that highlights and decorations show
		// over
		// the bar.
		gig.drawTexturedModalRect(5, 15, 0, 0, 5, 20);
		gig.drawTexturedModalRect(12, 15, 0, 0, 5, 20);

		gig.drawTexturedModalRect(30, 15, 0, 0, 5, 20);
		gig.drawTexturedModalRect(37, 15, 0, 0, 5, 20);

		gig.drawTexturedModalRect(55, 15, 0, 0, 5, 20);
		gig.drawTexturedModalRect(62, 15, 0, 0, 5, 20);

		gig.drawTexturedModalRect(80, 15, 0, 0, 5, 20);
		gig.drawTexturedModalRect(87, 15, 0, 0, 5, 20);

		if (this.data.MetalBurning[AllomancyData.matIron]) {
			gig.drawTexturedModalRect(5, 20 + ironY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matSteel]) {
			gig.drawTexturedModalRect(12, 20 + steelY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matTin]) {
			gig.drawTexturedModalRect(30, 20 + tinY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matPewter]) {
			gig.drawTexturedModalRect(37, 20 + pewterY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matCopper]) {
			gig.drawTexturedModalRect(55, 20 + copperY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matBronze]) {
			gig.drawTexturedModalRect(62, 20 + bronzeY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matZinc]) {
			gig.drawTexturedModalRect(80, 20 + zincY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matBrass]) {
			gig.drawTexturedModalRect(87, 20 + brassY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}

		if (this.animationCounter > 6) // Draw the burning symbols...
		{
			this.animationCounter = 0;
			this.currentFrame++;
			if (this.currentFrame > 3) {
				this.currentFrame = 0;
			}
		}
		double motionX, motionY, motionZ;
/*		for (Entity entity : Allomancy.MPC.particleTargets) {
			motionX = ((player.posX - entity.posX) * -1) * .03;
			motionY = (((player.posY - entity.posY) * -1) * .03) + .021;
			motionZ = ((player.posZ - entity.posZ) * -1) * .03;
			particle = new ParticleMetal(player.worldObj,
					player.posX
							- (Math.sin(Math.toRadians(player
									.getRotationYawHead())) * .7d),
					player.posY - .7, player.posZ
							+ (Math.cos(Math.toRadians(player
									.getRotationYawHead())) * .7d), motionX,
					motionY, motionZ);
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
		for (vector3 v : Allomancy.MPC.particleBlockTargets) {
			motionX = ((player.posX - v.X - .5) * -1) * .03;
			motionY = (((player.posY - v.Y - .5) * -1) * .03) + .021;
			motionZ = ((player.posZ - v.Z - .5) * -1) * .03;
			particle = new ParticleMetal(player.worldObj,
					player.posX
							- (Math.sin(Math.toRadians(player
									.getRotationYawHead())) * .7d),
					player.posY - .7, player.posZ
							+ (Math.cos(Math.toRadians(player
									.getRotationYawHead())) * .7d), motionX,
					motionY, motionZ);
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
		Allomancy.MPC.particleBlockTargets.clear();*/
	}
    	

    


	private void updateBurnTime(AllomancyData data, EntityPlayerMP player) {
		data = AllomancyData.forPlayer(player);

		for (int i = 0; i < 8; i++) {
			if (data.MetalBurning[i]) {
				data.BurnTime[i]--;
				if (data.BurnTime[i] == 0) {
					data.BurnTime[i] = data.MaxBurnTime[i];
					data.MetalAmounts[i]--;
					Allomancy.packetPipeline
					.sendToServer(new AllomancyUpdateBurnPacket(i,data.MetalBurning[i]));
					if (data.MetalAmounts[i] == 0) {
						data.MetalBurning[i] = false;
						Allomancy.packetPipeline
						.sendTo((new AllomancyUpdateBurnPacket(i,data.MetalBurning[i])), player);
					}
				}

			}
		}

	}

	/* Ugly below. Sorry */
	@SideOnly(Side.CLIENT)
	public void getMouseOver() {
		Minecraft mc = Minecraft.getMinecraft();
		float par1 = 0;
		if (mc.getRenderViewEntity() != null) {
			if (mc.theWorld != null) {
				mc.pointedEntity = null;
				double d0 = 20;
				mc.objectMouseOver = mc.getRenderViewEntity().rayTrace(d0, par1);
				double d1 = d0;
				Vec3 vec3 = (mc.getRenderViewEntity().getPositionVector());

				if (mc.objectMouseOver != null) {
					d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
				}

				Vec3 vec31 = mc.getRenderViewEntity().getLook(par1);
				Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord
						* d0, vec31.zCoord * d0);
				this.pointedEntity = null;
				float f1 = 1.0F;
				List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
						mc.getRenderViewEntity(),
						mc.getRenderViewEntity().getBoundingBox().addCoord(
								vec31.xCoord * d0, vec31.yCoord * d0,
								vec31.zCoord * d0).expand(f1, f1, f1));
				double d2 = d1;

				for (int i = 0; i < list.size(); ++i) {
					Entity entity = (Entity) list.get(i);

					if (true) {
						float f2 = entity.getCollisionBorderSize();
						AxisAlignedBB axisalignedbb = entity.getBoundingBox()
								.expand(f2, f2, f2);
						MovingObjectPosition movingobjectposition = axisalignedbb
								.calculateIntercept(vec3, vec32);

						if (axisalignedbb.isVecInside(vec3)) {
							if ((0.0D < d2) || (d2 == 0.0D)) {
								this.pointedEntity = entity;
								d2 = 0.0D;
								return;
							}
						} else if (movingobjectposition != null) {
							double d3 = vec3
									.distanceTo(movingobjectposition.hitVec);

							if ((d3 < d2) || (d2 == 0.0D)) {
								if ((entity == mc.getRenderViewEntity().ridingEntity)
										&& !entity.canRiderInteract()) {
									if (d2 == 0.0D) {
										this.pointedEntity = entity;
										return;
									}
								} else {
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
