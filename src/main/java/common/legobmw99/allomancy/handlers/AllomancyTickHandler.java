package common.legobmw99.allomancy.handlers;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
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
import common.legobmw99.allomancy.common.AllomancyCapabilites;
import common.legobmw99.allomancy.common.AllomancyCapabilites;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.network.packets.AllomancyCapabiltiesPacket;
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
	private AllomancyCapabilites cap;
	private int animationCounter = 0;
	private int currentFrame = 0;


	private Point[] Frames = { new Point(72, 0), new Point(72, 4),
			new Point(72, 8), new Point(72, 12) };

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		if(Allomancy.XPC.isBlockMetal(event.getState())){
			Allomancy.XPC.particleBlockTargets.clear();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		// Run once per tick, only if in game, and only if there is a player
		if (event.phase == TickEvent.Phase.END&& (!Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().thePlayer != null)) {

			EntityPlayerSP player;
			player = Minecraft.getMinecraft().thePlayer;
        	AllomancyCapabilites cap = AllomancyCapabilites.forPlayer(player);
			RayTraceResult ray;
			RayTraceResult mop;
			vector3 vec;
			
			if (cap.isMistborn == true) {
				this.updateBurnTime(cap, player);

				if (cap.MetalBurning[AllomancyCapabilites.matIron]
						|| cap.MetalBurning[AllomancyCapabilites.matSteel]) {
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
					if (cap.MetalBurning[AllomancyCapabilites.matIron]) {
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
					if (cap.MetalBurning[AllomancyCapabilites.matZinc]) {
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
					if (cap.MetalBurning[AllomancyCapabilites.matSteel]) {
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
					if (cap.MetalBurning[AllomancyCapabilites.matBrass]) {
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
				if (cap.MetalBurning[AllomancyCapabilites.matPewter]) {
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
	public void onDamage(LivingHurtEvent event) {
		//Increase outgoing damage for pewter burners
		if (event.getSource().getSourceOfDamage() instanceof EntityPlayerMP) {
			EntityPlayerMP source = (EntityPlayerMP) event.getSource()
					.getSourceOfDamage();
        	AllomancyCapabilites cap = AllomancyCapabilites.forPlayer(source);

			if (cap.MetalBurning[AllomancyCapabilites.matPewter]) {
				event.setAmount(event.getAmount() + 2);
			}
		}
		//Reduce incoming damage for pewter burners
		if (event.getEntityLiving() instanceof EntityPlayerMP) {
			AllomancyCapabilites cap = AllomancyCapabilites.forPlayer(event.getEntityLiving());
			if (cap.MetalBurning[AllomancyCapabilites.matPewter]) {
				event.setAmount(event.getAmount() - 2);
				//Note that they took damage, will come in to play if they stop burning
				cap.damageStored++;
			}
		}
	}

	/* ded -- use AttachCapabilityEvent and ICapabilitySerializable
	 * @SubscribeEvent
	 * public void onEntityConstructing(EntityConstructing event) {
	 * 		if (event.getEntity() instanceof EntityPlayer
	 * 			&& event.getEntity().getExtendedProperties(AllomancyCapabilites.IDENTIFIER) == null) {
	 * 			event.getEntity().registerExtendedProperties(AllomancyCapabilites.IDENTIFIER,
	 * 				new AllomancyCapabilites((EntityPlayer) event.getEntity()));
	 * }
	 * }
	 */
	


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
				AllomancyCapabilites cap = AllomancyCapabilites.forPlayer(player);
				Registry.network.sendToServer(new SelectMetalPacket(cap
						.getSelected() + 1));
				cap.setSelected(cap.getSelected() + 1);
			}
		}
		if (Registry.burnFirst.isPressed()) {
			EntityPlayerSP player;
			player = Minecraft.getMinecraft().thePlayer;
			AllomancyCapabilites cap;
			Minecraft mc = FMLClientHandler.instance().getClient();
			if (mc.currentScreen == null) {
				if (player == null) {
					return;
				}
				cap = AllomancyCapabilites.forPlayer(player);
				switch (cap.getSelected()) {
				case 1:
					// toggle iron.
					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyCapabilites.matIron,
							!cap.MetalBurning[AllomancyCapabilites.matIron]));

					if (cap.MetalAmounts[AllomancyCapabilites.matIron] > 0) {
						cap.MetalBurning[AllomancyCapabilites.matIron] = !cap.MetalBurning[AllomancyCapabilites.matIron];
					}
					//play a sound effect
					if(cap.MetalBurning[AllomancyCapabilites.matIron]){
						//Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						//Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 2:
					// toggle Tin.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyCapabilites.matTin,
							!cap.MetalBurning[AllomancyCapabilites.matTin]));
					if (cap.MetalAmounts[AllomancyCapabilites.matTin] > 0) {
						cap.MetalBurning[AllomancyCapabilites.matTin] = !cap.MetalBurning[AllomancyCapabilites.matTin];
					}
					//play a sound effect
					if(cap.MetalBurning[AllomancyCapabilites.matTin]){
						//Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						//Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 3:
					// toggle Zinc.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyCapabilites.matZinc,
							!cap.MetalBurning[AllomancyCapabilites.matZinc]));
					if (cap.MetalAmounts[AllomancyCapabilites.matZinc] > 0) {
						cap.MetalBurning[AllomancyCapabilites.matZinc] = !cap.MetalBurning[AllomancyCapabilites.matZinc];
					}
					//play a sound effect
					if(cap.MetalBurning[AllomancyCapabilites.matZinc]){
						//Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						//Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 4:
					// toggle Copper.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyCapabilites.matCopper,
							!cap.MetalBurning[AllomancyCapabilites.matCopper]));
					if (cap.MetalAmounts[AllomancyCapabilites.matCopper] > 0) {
						cap.MetalBurning[AllomancyCapabilites.matCopper] = !cap.MetalBurning[AllomancyCapabilites.matCopper];
					}
					//play a sound effect
					if(cap.MetalBurning[AllomancyCapabilites.matCopper]){
						//Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						//Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
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
			AllomancyCapabilites cap;
			Minecraft mc = FMLClientHandler.instance().getClient();
			if (mc.currentScreen == null) {
				if (player == null) {
					return;
				}

				cap = AllomancyCapabilites.forPlayer(player);
				switch (cap.getSelected()) {
				case 1:
					// toggle Steel.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyCapabilites.matSteel,
							!cap.MetalBurning[AllomancyCapabilites.matSteel]));
					if (cap.MetalAmounts[AllomancyCapabilites.matSteel] > 0) {
						cap.MetalBurning[AllomancyCapabilites.matSteel] = !cap.MetalBurning[AllomancyCapabilites.matSteel];
					}
					//play a sound effect
					if(cap.MetalBurning[AllomancyCapabilites.matSteel]){
						//Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						//Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 2:
					// toggle Pewter.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyCapabilites.matPewter,
							!cap.MetalBurning[AllomancyCapabilites.matPewter]));
					if (cap.MetalAmounts[AllomancyCapabilites.matPewter] > 0) {
						cap.MetalBurning[AllomancyCapabilites.matPewter] = !cap.MetalBurning[AllomancyCapabilites.matPewter];
					}
					//play a sound effect
					if(cap.MetalBurning[AllomancyCapabilites.matPewter]){
						//Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						//Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 3:
					// toggle Brass.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyCapabilites.matBrass,
							!cap.MetalBurning[AllomancyCapabilites.matBrass]));
					if (cap.MetalAmounts[AllomancyCapabilites.matBrass] > 0) {
						cap.MetalBurning[AllomancyCapabilites.matBrass] = !cap.MetalBurning[AllomancyCapabilites.matBrass];
					}
					//play a sound effect
					if(cap.MetalBurning[AllomancyCapabilites.matBrass]){
						//Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						//Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
					}
					break;
				case 4:
					// toggle Bronze.

					Registry.network.sendToServer(new UpdateBurnPacket(
							AllomancyCapabilites.matBronze,
							!cap.MetalBurning[AllomancyCapabilites.matBronze]));
					if (cap.MetalAmounts[AllomancyCapabilites.matBronze] > 0) {
						cap.MetalBurning[AllomancyCapabilites.matBronze] = !cap.MetalBurning[AllomancyCapabilites.matBronze];
					}
					//play a sound effect
					if(cap.MetalBurning[AllomancyCapabilites.matBronze]){
						//Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
					}else{
						//.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
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
			AllomancyCapabilites cap = AllomancyCapabilites.forPlayer(event.player);
			for (int i = 0; i < 7; i++) {
				cap.MetalBurning[i] = false;
			}
			Registry.network.sendTo(new AllomancyCapabiltiesPacket(cap),
					(EntityPlayerMP) event.player);
			if (cap.isMistborn == true) {
				Registry.network.sendTo(new BecomeMistbornPacket(),(EntityPlayerMP) event.player);

				if (event.player.worldObj.isRemote) {
					cap.isMistborn = true;
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		AllomancyCapabilites cap = AllomancyCapabilites.forPlayer(event.player);
		for (int i = 0; i < 8; i++) {
			cap.MetalAmounts[i] = 0;
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
		if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
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

		this.cap = AllomancyCapabilites.forPlayer(player);
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
		if (!cap.isMistborn) {
			return;
		}
		GuiIngame gig = new GuiIngame(Minecraft.getMinecraft());
		Minecraft.getMinecraft().renderEngine.bindTexture(this.meterLoc);
		ITextureObject obj;
		obj = Minecraft.getMinecraft().renderEngine.getTexture(this.meterLoc);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

		switch (this.cap.getSelected()) {
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

		ironY = 9 - this.cap.MetalAmounts[AllomancyCapabilites.matIron];
		gig.drawTexturedModalRect(renderX+1, renderY + 5 + ironY, 7, 1 + ironY, 3, 10 - ironY);

		steelY = 9 - this.cap.MetalAmounts[AllomancyCapabilites.matSteel];
		gig.drawTexturedModalRect(renderX+8, renderY + 5 + steelY, 13, 1 + steelY, 3,
				10 - steelY);

		tinY = 9 - this.cap.MetalAmounts[AllomancyCapabilites.matTin];
		gig.drawTexturedModalRect(renderX+26, renderY + 5 + tinY, 19, 1 + tinY, 3, 10 - tinY);

		pewterY = 9 - this.cap.MetalAmounts[AllomancyCapabilites.matPewter];
		gig.drawTexturedModalRect(renderX+33, renderY + 5 + pewterY, 25, 1 + pewterY, 3,
				10 - pewterY);
		
		zincY = 9 - this.cap.MetalAmounts[AllomancyCapabilites.matZinc];
		gig.drawTexturedModalRect(renderX+51, renderY + 5 + zincY, 43, 1 + zincY, 3, 10 - zincY);

		brassY = 9 - this.cap.MetalAmounts[AllomancyCapabilites.matBrass];
		gig.drawTexturedModalRect(renderX+58, renderY + 5 + brassY, 49, 1 + brassY, 3,
				10 - brassY);
		
		copperY = 9 - this.cap.MetalAmounts[AllomancyCapabilites.matCopper];
		gig.drawTexturedModalRect(renderX+76, renderY + 5 + copperY, 31, 1 + copperY, 3,
				10 - copperY);

		bronzeY = 9 - this.cap.MetalAmounts[AllomancyCapabilites.matBronze];
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

		if (this.cap.MetalBurning[AllomancyCapabilites.matIron]) {
			gig.drawTexturedModalRect(renderX, renderY + 5 + ironY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.MetalBurning[AllomancyCapabilites.matSteel]) {
			gig.drawTexturedModalRect(renderX+7, renderY + 5 + steelY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.MetalBurning[AllomancyCapabilites.matTin]) {
			gig.drawTexturedModalRect(renderX+25, renderY + 5 + tinY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.MetalBurning[AllomancyCapabilites.matPewter]) {
			gig.drawTexturedModalRect(renderX+32, renderY + 5 + pewterY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.MetalBurning[AllomancyCapabilites.matZinc]) {
			gig.drawTexturedModalRect(renderX+50, renderY + 5 + zincY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.MetalBurning[AllomancyCapabilites.matBrass]) {
			gig.drawTexturedModalRect(renderX+57, renderY + 5 + brassY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.MetalBurning[AllomancyCapabilites.matCopper]) {
			gig.drawTexturedModalRect(renderX+75, renderY + 5 + copperY,
					this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.MetalBurning[AllomancyCapabilites.matBronze]) {
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
		if ((this.cap.MetalBurning[AllomancyCapabilites.matIron] || this.cap.MetalBurning[AllomancyCapabilites.matSteel]) && (event instanceof RenderGameOverlayEvent.Post)){
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
		if ((player == null) || (event.getEntity() == null) || ((player.getDistanceToEntity(event.getEntity()) > 20) || (player.getDistanceToEntity(event.getEntity()) < .5))) {
			return;
		}
		AllomancyCapabilites cap = AllomancyCapabilites.forPlayer(player);
		//Spawn sound particles
				if (cap.MetalBurning[AllomancyCapabilites.matTin]) {
					if (event.getSound().toString().contains("step") 
							|| event.getSound().toString().contains("mob")
							|| event.getSound().toString().contains("hostile")
							|| event.getSound().toString().contains(".big")
							|| event.getSound().toString().contains("scream")
							|| event.getSound().toString().contains("bow")) {
						motionX = ((player.posX - (event.getEntity().posX + .5)) * -0.7)/ player.getDistanceToEntity(event.getEntity());
						motionY = (((player.posY - (event.getEntity().posY + .2)) * -0.7)/ player.getDistanceToEntity(event.getEntity()));
						motionZ = ((player.posZ - (event.getEntity().posZ + .5)) * -0.7) /player.getDistanceToEntity(event.getEntity());
						Particle particle = new ParticleSound(player.worldObj,
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

			World world;
			world = (World) event.world;

			List<EntityPlayer> list = world.playerEntities;
			for (EntityPlayer curPlayer : list) {
				cap = AllomancyCapabilites.forPlayer(curPlayer);

				if (cap.isMistborn == true) {
					//Damage the player if they have stored damage and pewter cuts out
					if (!cap.MetalBurning[AllomancyCapabilites.matPewter]
							&& (cap.damageStored > 0)) {
						cap.damageStored--;
						curPlayer.attackEntityFrom(DamageSource.generic, 2);
					}
					if (cap.MetalBurning[AllomancyCapabilites.matTin]) {
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
					if ((cap.MetalBurning[AllomancyCapabilites.matTin] == false)
							&& curPlayer.isPotionActive(Potion.getPotionById(16))) {
						if (curPlayer.getActivePotionEffect(Potion.getPotionById(16))
								.getDuration() < 201) {
							curPlayer.removePotionEffect(Potion.getPotionById(16));
						}
					}
					if(cap.MetalBurning[AllomancyCapabilites.matCopper] == false){
						if(cap.MetalBurning[AllomancyCapabilites.matIron] || cap.MetalBurning[AllomancyCapabilites.matSteel] || cap.MetalBurning[AllomancyCapabilites.matTin] || cap.MetalBurning[AllomancyCapabilites.matPewter] || cap.MetalBurning[AllomancyCapabilites.matZinc] || cap.MetalBurning[AllomancyCapabilites.matBrass] || cap.MetalBurning[AllomancyCapabilites.matBronze]){
						//TODO:bronze stuff here, probably a packet
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateBurnTime(AllomancyCapabilites data, EntityPlayerSP player) {
		cap = AllomancyCapabilites.forPlayer(player);
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