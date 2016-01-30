package common.legobmw99.allomancy.handlers;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.network.packets.AllomancyDataPacket;
import common.legobmw99.allomancy.network.packets.BecomeMistbornPacket;
import common.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import common.legobmw99.allomancy.network.packets.SelectMetalPacket;
import common.legobmw99.allomancy.network.packets.UpdateBurnPacket;
import common.legobmw99.allomancy.particle.ParticleMetal;
import common.legobmw99.allomancy.particle.ParticleSound;
import common.legobmw99.allomancy.util.AllomancyConfig;
import common.legobmw99.allomancy.util.vector3;

public class AllomancyTickHandler {

	private Entity pointedEntity;
	private Minecraft mc;
	private ResourceLocation meterLoc;
	private AllomancyData data;
	private int animationCounter = 0;
	private int currentFrame = 0;


	private Point[] Frames = { new Point(72, 0), new Point(72, 4),
			new Point(72, 8), new Point(72, 12) };

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		if(Allomancy.XPC.isBlockMetal(event.state)){
			Allomancy.XPC.particleBlockTargets.clear();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		// Run once per tick, only if in game, and only if there is a player
		if (event.phase == TickEvent.Phase.END&& (!Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().thePlayer != null)) {

			AllomancyData data;
			EntityPlayerSP player;
			player = Minecraft.getMinecraft().thePlayer;
			data = AllomancyData.forPlayer(player);
			MovingObjectPosition ray;
			MovingObjectPosition mop;
			vector3 vec;
			
			if (data.isMistborn == true) {
				this.updateBurnTime(data, player);

				if (data.MetalBurning[AllomancyData.matIron]
						|| data.MetalBurning[AllomancyData.matSteel]) {
					List<Entity> eList;
					
					Entity target;
					AxisAlignedBB box;
					//Add entities to metal list
					box = AxisAlignedBB.fromBounds(player.posX - 10,
							player.posY - 10, player.posZ - 10,
							player.posX + 10, player.posY + 10,
							player.posZ + 10);
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
				if ((player.getCurrentEquippedItem() == null)
						&& (Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown() == true)) {
					//Ray trace 20 blocks
					MovingObjectPosition mov = getMouseOverExtended(20.0F);
					//All iron pulling powers
					if (data.MetalBurning[AllomancyData.matIron]) {
						if (mov != null) {
							if (mov.entityHit != null) {
								Allomancy.XPC.tryPullEntity(mov.entityHit);
							}
						}
						ray = player.rayTrace(20.0F, 0.0F);
						if (ray != null) {
							if (ray.typeOfHit == MovingObjectType.BLOCK || ray.typeOfHit == MovingObjectType.MISS) {
								vec = new vector3(ray.getBlockPos());
								if (Allomancy.XPC.isBlockMetal(Minecraft.getMinecraft().theWorld.getBlockState(vec.pos))) {
									Allomancy.XPC.tryPullBlock(vec);
								}
							}

						}

					}
					//All zinc powers
					if (data.MetalBurning[AllomancyData.matZinc]) {
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
				if ((player.getCurrentEquippedItem() == null)
						&& (Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown() == true)) {
					//Ray trace 20 blocks
					MovingObjectPosition mov = getMouseOverExtended(20.0F);
					//All steel pushing powers
					if (data.MetalBurning[AllomancyData.matSteel]) {
						if (mov != null) {
							if (mov.entityHit != null) {
								Allomancy.XPC.tryPushEntity(mov.entityHit);
							}
						}
						ray = player.rayTrace(20.0F, 0.0F);
						if (ray != null) {
							if (ray.typeOfHit == MovingObjectType.BLOCK
									|| ray.typeOfHit == MovingObjectType.MISS) {
								vec = new vector3(ray.getBlockPos());
								if (Allomancy.XPC.isBlockMetal(Minecraft.getMinecraft().theWorld.getBlockState(vec.pos))) {
									Allomancy.XPC.tryPushBlock(vec);
								}
							}

						}

					}
					//All brass powers
					if (data.MetalBurning[AllomancyData.matBrass]) {
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
				if (data.MetalBurning[AllomancyData.matPewter]) {
					if ((player.onGround == true)
							&& (player.isInWater() == false)
							&& (Minecraft.getMinecraft().gameSettings.keyBindForward
									.isKeyDown())) {
						player.motionX *= 1.2;
						player.motionZ *= 1.2;

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
	public void onDamage(LivingHurtEvent event) {
		//Increase outgoing damage for pewter burners
		if (event.source.getSourceOfDamage() instanceof EntityPlayerMP) {
			EntityPlayerMP source = (EntityPlayerMP) event.source
					.getSourceOfDamage();
			AllomancyData data;
			data = AllomancyData.forPlayer(source);
			if (data.MetalBurning[AllomancyData.matPewter]) {
				event.ammount += 2;
			}
		}
		//Reduce incoming damage for pewter burners
		if (event.entityLiving instanceof EntityPlayerMP) {
			AllomancyData data = AllomancyData.forPlayer(event.entityLiving);
			if (data.MetalBurning[AllomancyData.matPewter]) {
				event.ammount -= 2;
				data.damageStored++;
			}
		}
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer
				&& event.entity.getExtendedProperties(AllomancyData.IDENTIFIER) == null) {
			event.entity.registerExtendedProperties(AllomancyData.IDENTIFIER,
					new AllomancyData((EntityPlayer) event.entity));
		}
	}


	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (Registry.changeGroup.isPressed()) {

			EntityPlayerSP player;
			player = Minecraft.getMinecraft().thePlayer;
			Minecraft mc = FMLClientHandler.instance().getClient();
			if (mc.currentScreen == null) {
				if ((player == null)
						|| !Minecraft.getMinecraft().inGameHasFocus) {
					return;
				}
				AllomancyData data = AllomancyData.forPlayer(player);
				Registry.network.sendToServer(new SelectMetalPacket(data
						.getSelected() + 1));
				data.setSelected(data.getSelected() + 1);
			}
		}
		if (Registry.burnFirst.isPressed()) {
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
					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyData.matIron,
							!data.MetalBurning[AllomancyData.matIron]));

					if (data.MetalAmounts[AllomancyData.matIron] > 0) {
						data.MetalBurning[AllomancyData.matIron] = !data.MetalBurning[AllomancyData.matIron];
					}
					//play a sound effect
					if(data.MetalBurning[AllomancyData.matIron]){
						Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 2:
					// toggle Tin.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyData.matTin,
							!data.MetalBurning[AllomancyData.matTin]));
					if (data.MetalAmounts[AllomancyData.matTin] > 0) {
						data.MetalBurning[AllomancyData.matTin] = !data.MetalBurning[AllomancyData.matTin];
					}
					//play a sound effect
					if(data.MetalBurning[AllomancyData.matTin]){
						Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 3:
					// toggle Zinc.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyData.matZinc,
							!data.MetalBurning[AllomancyData.matZinc]));
					if (data.MetalAmounts[AllomancyData.matZinc] > 0) {
						data.MetalBurning[AllomancyData.matZinc] = !data.MetalBurning[AllomancyData.matZinc];
					}
					//play a sound effect
					if(data.MetalBurning[AllomancyData.matZinc]){
						Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 4:
					// toggle Copper.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyData.matCopper,
							!data.MetalBurning[AllomancyData.matCopper]));
					if (data.MetalAmounts[AllomancyData.matCopper] > 0) {
						data.MetalBurning[AllomancyData.matCopper] = !data.MetalBurning[AllomancyData.matCopper];
					}
					//play a sound effect
					if(data.MetalBurning[AllomancyData.matCopper]){
						Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				default:
					break;
				}
			}
		}
		if (Registry.burnSecond.isPressed()) {
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

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyData.matSteel,
							!data.MetalBurning[AllomancyData.matSteel]));
					if (data.MetalAmounts[AllomancyData.matSteel] > 0) {
						data.MetalBurning[AllomancyData.matSteel] = !data.MetalBurning[AllomancyData.matSteel];
					}
					//play a sound effect
					if(data.MetalBurning[AllomancyData.matSteel]){
						Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 2:
					// toggle Pewter.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyData.matPewter,
							!data.MetalBurning[AllomancyData.matPewter]));
					if (data.MetalAmounts[AllomancyData.matPewter] > 0) {
						data.MetalBurning[AllomancyData.matPewter] = !data.MetalBurning[AllomancyData.matPewter];
					}
					//play a sound effect
					if(data.MetalBurning[AllomancyData.matPewter]){
						Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 3:
					// toggle Brass.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyData.matBrass,
							!data.MetalBurning[AllomancyData.matBrass]));
					if (data.MetalAmounts[AllomancyData.matBrass] > 0) {
						data.MetalBurning[AllomancyData.matBrass] = !data.MetalBurning[AllomancyData.matBrass];
					}
					//play a sound effect
					if(data.MetalBurning[AllomancyData.matBrass]){
						Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 4:
					// toggle Bronze.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyData.matBronze,
							!data.MetalBurning[AllomancyData.matBronze]));
					if (data.MetalAmounts[AllomancyData.matBronze] > 0) {
						data.MetalBurning[AllomancyData.matBronze] = !data.MetalBurning[AllomancyData.matBronze];
					}
					//play a sound effect
					if(data.MetalBurning[AllomancyData.matBronze]){
						Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
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
			for (int i = 0; i < 7; i++) {
				data.MetalBurning[i] = false;
			}
			Registry.network.sendTo(new AllomancyDataPacket(data),
					(EntityPlayerMP) event.player);
			if (data.isMistborn == true) {
				Registry.network.sendTo(new BecomeMistbornPacket(),(EntityPlayerMP) event.player);

				if (event.player.worldObj.isRemote) {
					data.isMistborn = true;
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		AllomancyData data = AllomancyData.forPlayer(event.player);
		for (int i = 0; i < 8; i++) {
			data.MetalAmounts[i] = 0;
		}
		NBTTagCompound old = event.player.getEntityData();
		if (old.hasKey("Allomancy_Data")) {
			event.player.getEntityData().setTag("Allomancy_Data",
					old.getCompoundTag("Allomancy_Data"));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}

		this.mc = Minecraft.getMinecraft();
		this.meterLoc = new ResourceLocation("allomancy",
				"textures/overlay/meter.png");

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
		int renderX,renderY = 0;
	   	ScaledResolution res = new ScaledResolution(this.mc);

	   	//Set the offsets of the overlay based on config
		switch (AllomancyConfig.overlayPosition){
		case 0:
			 renderX = 5;
			 renderY = 10;
			 break;
		case 1:
			renderX = res.getScaledWidth() - 95;
			renderY = 10;
			break;
		case 2:
			renderX = res.getScaledWidth() - 95;
			renderY = res.getScaledHeight() - 30;
			break;
		case 3:
			renderX = 5;
			renderY = res.getScaledHeight() - 30;
			break;
		default:
			 renderX = 5;
			 renderY = 10;
			 break;
		
		}
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
			gig.drawTexturedModalRect(renderX-2, renderY-2, 54, 0, 16, 24);
			break;
		case 2:
			gig.drawTexturedModalRect(renderX+23, renderY-2, 54, 0, 16, 24);
			break;
		case 3:
			gig.drawTexturedModalRect(renderX+48, renderY-2, 54, 0, 16, 24);
			break;
		case 4:
			gig.drawTexturedModalRect(renderX+73, renderY-2, 54, 0, 16, 24);
			break;

		}

		ironY = 9 - this.data.MetalAmounts[AllomancyData.matIron];
		gig.drawTexturedModalRect(renderX+1, renderY + 5 + ironY, 7, 1 + ironY, 3, 10 - ironY);

		steelY = 9 - this.data.MetalAmounts[AllomancyData.matSteel];
		gig.drawTexturedModalRect(renderX+8, renderY + 5 + steelY, 13, 1 + steelY, 3,
				10 - steelY);

		tinY = 9 - this.data.MetalAmounts[AllomancyData.matTin];
		gig.drawTexturedModalRect(renderX+26, renderY + 5 + tinY, 19, 1 + tinY, 3, 10 - tinY);

		pewterY = 9 - this.data.MetalAmounts[AllomancyData.matPewter];
		gig.drawTexturedModalRect(renderX+33, renderY + 5 + pewterY, 25, 1 + pewterY, 3,
				10 - pewterY);
		
		zincY = 9 - this.data.MetalAmounts[AllomancyData.matZinc];
		gig.drawTexturedModalRect(renderX+51, renderY + 5 + zincY, 43, 1 + zincY, 3, 10 - zincY);

		brassY = 9 - this.data.MetalAmounts[AllomancyData.matBrass];
		gig.drawTexturedModalRect(renderX+58, renderY + 5 + brassY, 49, 1 + brassY, 3,
				10 - brassY);
		
		copperY = 9 - this.data.MetalAmounts[AllomancyData.matCopper];
		gig.drawTexturedModalRect(renderX+76, renderY + 5 + copperY, 31, 1 + copperY, 3,
				10 - copperY);

		bronzeY = 9 - this.data.MetalAmounts[AllomancyData.matBronze];
		gig.drawTexturedModalRect(renderX+83, renderY + 5 + bronzeY, 37, 1 + bronzeY, 3,
				10 - bronzeY);


		// Draw the gauges second, so that highlights and decorations show over the bar.
		gig.drawTexturedModalRect(renderX, renderY, 0, 0, 5, 20);
		gig.drawTexturedModalRect(renderX+7, renderY, 0, 0, 5, 20);

		gig.drawTexturedModalRect(renderX+25, renderY, 0, 0, 5, 20);
		gig.drawTexturedModalRect(renderX+32, renderY, 0, 0, 5, 20);

		gig.drawTexturedModalRect(renderX+50, renderY, 0, 0, 5, 20);
		gig.drawTexturedModalRect(renderX+57, renderY, 0, 0, 5, 20);

		gig.drawTexturedModalRect(renderX+75, renderY, 0, 0, 5, 20);
		gig.drawTexturedModalRect(renderX+82, renderY, 0, 0, 5, 20);

		if (this.data.MetalBurning[AllomancyData.matIron]) {
			gig.drawTexturedModalRect(renderX, renderY + 5 + ironY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matSteel]) {
			gig.drawTexturedModalRect(renderX+7, renderY + 5 + steelY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matTin]) {
			gig.drawTexturedModalRect(renderX+25, renderY + 5 + tinY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matPewter]) {
			gig.drawTexturedModalRect(renderX+32, renderY + 5 + pewterY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matZinc]) {
			gig.drawTexturedModalRect(renderX+50, renderY + 5 + zincY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matBrass]) {
			gig.drawTexturedModalRect(renderX+57, renderY + 5 + brassY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matCopper]) {
			gig.drawTexturedModalRect(renderX+75, renderY + 5 + copperY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.data.MetalBurning[AllomancyData.matBronze]) {
			gig.drawTexturedModalRect(renderX+82, renderY + 5 + bronzeY,
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
		//Spawn in metal particles
		if ((this.data.MetalBurning[AllomancyData.matIron] || this.data.MetalBurning[AllomancyData.matSteel]) && (event instanceof RenderGameOverlayEvent.Post)){
			for (Entity entity : Allomancy.XPC.particleTargets) {
				motionX = ((player.posX - entity.posX) * -1) * .03;
				motionY = (((player.posY - entity.posY + 1.2) * -1) * .03) + .021;
				motionZ = ((player.posZ - entity.posZ) * -1) * .03;
				particle = new ParticleMetal(player.worldObj,
						player.posX
								- (Math.sin(Math.toRadians(player
										.getRotationYawHead())) * .7d),
						player.posY - .2, player.posZ
								+ (Math.cos(Math.toRadians(player
										.getRotationYawHead())) * .7d),
						motionX, motionY, motionZ);
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}
			for (vector3 v : Allomancy.XPC.particleBlockTargets) {
				motionX = ((player.posX - (v.X + .5)) * -1) * .03;
				motionY = (((player.posY - (v.Y + .2)) * -1) * .03);
				motionZ = ((player.posZ - (v.Z + .5)) * -1) * .03;
				particle = new ParticleMetal(player.worldObj,
						player.posX
								- (Math.sin(Math.toRadians(player
										.getRotationYawHead())) * .7d),
						player.posY - .7, player.posZ
								+ (Math.cos(Math.toRadians(player
										.getRotationYawHead())) * .7d),
						motionX, motionY, motionZ);
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}
			Allomancy.XPC.particleBlockTargets.clear();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onSound(PlaySoundAtEntityEvent event) {
		double motionX, motionY, motionZ;
		EntityPlayerSP player;
		player = Minecraft.getMinecraft().thePlayer;
		if ((player == null) || (event.entity == null) || ((player.getDistanceToEntity(event.entity) > 20) || (player.getDistanceToEntity(event.entity) < .5))) {
			return;
		}
		AllomancyData data = AllomancyData.forPlayer(player);
		//Spawn sound particles
		if (data.MetalBurning[AllomancyData.matTin]) {
			if (event.name.contains("step") 
					|| event.name.contains("mob")
					|| event.name.contains("hostile")
					|| event.name.contains(".big")
					|| event.name.contains("scream")
					|| event.name.contains("bow")) {
				motionX = ((player.posX - (event.entity.posX + .5)) * -0.7)/ player.getDistanceToEntity(event.entity);
				motionY = (((player.posY - (event.entity.posY + .2)) * -0.7)/ player.getDistanceToEntity(event.entity));
				motionZ = ((player.posZ - (event.entity.posZ + .5)) * -0.7) /player.getDistanceToEntity(event.entity);
				EntityFX particle = new ParticleSound(player.worldObj,
						player.posX + (Math.sin(Math.toRadians(player.getRotationYawHead())) * -.7d),
						player.posY + .2, 
						player.posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .7d),
						motionX, motionY, motionZ, event);
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}

		}
	}
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {

			MinecraftServer mcs;
			mcs = MinecraftServer.getServer();
			World world;
			world = (World) mcs.getEntityWorld();

			List<EntityPlayer> list = world.playerEntities;
			for (EntityPlayer curPlayer : list) {
				data = AllomancyData.forPlayer(curPlayer);

				if (data.isMistborn == true) {
								
					if (!data.MetalBurning[AllomancyData.matPewter]
							&& (data.damageStored > 0)) {
						data.damageStored--;
						curPlayer.attackEntityFrom(DamageSource.generic, 2);
					}
					if (data.MetalBurning[AllomancyData.matTin]) {

						if (curPlayer
								.isPotionActive(Potion.nightVision.getId()) == false) {
							curPlayer.addPotionEffect(new PotionEffect(
									Potion.nightVision.getId(), 300, 0, false, false));
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
										Potion.nightVision.getId(), 300, 0, false, false));
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
					if(data.MetalBurning[AllomancyData.matCopper] == false){
						if(data.MetalBurning[AllomancyData.matIron] || data.MetalBurning[AllomancyData.matSteel] || data.MetalBurning[AllomancyData.matTin] || data.MetalBurning[AllomancyData.matPewter] || data.MetalBurning[AllomancyData.matZinc] || data.MetalBurning[AllomancyData.matBrass] || data.MetalBurning[AllomancyData.matBronze]){
						//TODO:bronze stuff here, probably a packet
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateBurnTime(AllomancyData data, EntityPlayerSP player) {
		data = AllomancyData.forPlayer(player);

		for (int i = 0; i < 8; i++) {
			if (data.MetalBurning[i]) {
				data.BurnTime[i]--;
				if (data.BurnTime[i] == 0) {
					data.BurnTime[i] = data.MaxBurnTime[i];
					data.MetalAmounts[i]--;
					Registry.network.sendToServer(new UpdateBurnPacket(i,
							data.MetalBurning[i]));
					if (data.MetalAmounts[i] == 0) {
						Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
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
	public static MovingObjectPosition getMouseOverExtended(float dist) {
		Minecraft mc = FMLClientHandler.instance().getClient();
		Entity theRenderViewEntity = mc.getRenderViewEntity();
		AxisAlignedBB theViewBoundingBox = new AxisAlignedBB(
				theRenderViewEntity.posX - 0.5D,
				theRenderViewEntity.posY - 0.0D,
				theRenderViewEntity.posZ - 0.5D,
				theRenderViewEntity.posX + 0.5D,
				theRenderViewEntity.posY + 1.5D,
				theRenderViewEntity.posZ + 0.5D);
		MovingObjectPosition returnMOP = null;
		if (mc.theWorld != null) {
			double var2 = dist;
			returnMOP = theRenderViewEntity.rayTrace(var2, 0);
			double calcdist = var2;
			Vec3 pos = theRenderViewEntity.getPositionEyes(0);
			var2 = calcdist;
			if (returnMOP != null) {
				calcdist = returnMOP.hitVec.distanceTo(pos);
			}

			Vec3 lookvec = theRenderViewEntity.getLook(0);
			Vec3 var8 = pos.addVector(lookvec.xCoord * var2, lookvec.yCoord
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
					MovingObjectPosition mop0 = aabb.calculateIntercept(pos,
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
				returnMOP = new MovingObjectPosition(pointedEntity);
			}
		}
		return returnMOP;
	}
}