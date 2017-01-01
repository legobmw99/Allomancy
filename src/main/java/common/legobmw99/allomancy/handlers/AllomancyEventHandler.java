package common.legobmw99.allomancy.handlers;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import common.legobmw99.allomancy.Allomancy;
import common.legobmw99.allomancy.common.AllomancyCapabilities;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.network.packets.AllomancyCapabiltiesPacket;
import common.legobmw99.allomancy.network.packets.BecomeMistbornPacket;
import common.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import common.legobmw99.allomancy.network.packets.GetCapabilitiesPacket;
import common.legobmw99.allomancy.network.packets.SelectMetalPacket;
import common.legobmw99.allomancy.network.packets.UpdateBurnPacket;
import common.legobmw99.allomancy.particle.ParticlePointer;
import common.legobmw99.allomancy.util.AllomancyConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AllomancyEventHandler {

	private Entity pointedEntity;
	private Minecraft mc;
	private ResourceLocation meterLoc;
	private AllomancyCapabilities cap;
	private int animationCounter = 0;

	private int currentFrame = 0;

	private Point[] Frames = { new Point(72, 0), new Point(72, 4), new Point(72, 8), new Point(72, 12) };

	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer && !event.getObject().hasCapability(Allomancy.PLAYER_CAP, null)) {
			event.addCapability(new ResourceLocation(Allomancy.MODID, "Allomancy_Data"),
					new AllomancyCapabilities(((EntityPlayer) event.getObject())));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		if (Allomancy.XPC.isBlockMetal(event.getState().getBlock())) {
			Allomancy.XPC.particleBlockTargets.clear();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		// Run once per tick, only if in game, and only if there is a player
		if (event.phase == TickEvent.Phase.END
				&& (!Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().player != null)) {

			EntityPlayerSP player;
			player = Minecraft.getMinecraft().player;
			AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
			RayTraceResult ray;
			RayTraceResult mop;
			BlockPos bp;

			if (cap.isMistborn()) {
				this.updateBurnTime(cap);

				if (cap.getMetalBurning(AllomancyCapabilities.matIron)
						|| cap.getMetalBurning(AllomancyCapabilities.matSteel)) {
					List<Entity> eListMetal;

					Entity target;
					AxisAlignedBB boxMetal;

					// Add entities to metal list
					boxMetal = new AxisAlignedBB((player.posX - 10), (player.posY - 10), (player.posZ - 10),
							(player.posX + 10), (player.posY + 10), (player.posZ + 10));
					eListMetal = player.world.getEntitiesWithinAABB(Entity.class, boxMetal);
					for (Entity curEntity : eListMetal) {
						if (curEntity != null && (curEntity instanceof EntityItem || curEntity instanceof EntityLiving
								|| curEntity instanceof EntityGoldNugget))
							Allomancy.XPC.tryAddMetalEntity(curEntity);
					}

					int xLoc, zLoc, yLoc;
					xLoc = (int) player.posX;
					zLoc = (int) player.posZ;
					yLoc = (int) player.posY;
					// Add blocks to metal list
					for (int x = xLoc - 10; x < (xLoc + 10); x++) {
						for (int z = zLoc - 10; z < (zLoc + 10); z++) {
							for (int y = yLoc - 10; y < (yLoc + 10); y++) {
								bp = new BlockPos(x, y, z);
								if (Allomancy.XPC
										.isBlockMetal(Minecraft.getMinecraft().world.getBlockState(bp).getBlock())) {
									Allomancy.XPC.particleBlockTargets.add(bp);
								}
							}
						}
					}
				} else {
					Allomancy.XPC.particleTargets.clear();
				}
				if ((player.getHeldItemMainhand().isEmpty())
						&& (Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown())) {
					// Ray trace 20 blocks
					RayTraceResult mov = getMouseOverExtended(20.0F);
					// All iron pulling powers
					if (cap.getMetalBurning(AllomancyCapabilities.matIron)) {
						if (mov != null) {
							if (mov.entityHit != null) {
								Allomancy.XPC.tryPullEntity(mov.entityHit);
							}
						}
						ray = player.rayTrace(20.0F, 0.0F);
						if (ray != null) {
							if (ray.typeOfHit == RayTraceResult.Type.BLOCK
									|| ray.typeOfHit == RayTraceResult.Type.MISS) {
								bp = ray.getBlockPos();
								if (Allomancy.XPC
										.isBlockMetal(Minecraft.getMinecraft().world.getBlockState(bp).getBlock())) {
									Allomancy.XPC.tryPullBlock(bp);
								}
							}

						}

					}
					// All zinc powers
					if (cap.getMetalBurning(AllomancyCapabilities.matZinc)) {
						Entity entity;
						if ((mov != null) && (mov.entityHit != null) && (mov.entityHit instanceof EntityCreature)
								&& !(mov.entityHit instanceof EntityPlayer)) {

							entity = mov.entityHit;
							Registry.network.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), true));

						}
					}

				}
				if ((player.getHeldItemMainhand()).isEmpty()
						&& (Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown())) {
					// Ray trace 20 blocks
					RayTraceResult mov = getMouseOverExtended(20.0F);
					// All steel pushing powers
					if (cap.getMetalBurning(AllomancyCapabilities.matSteel)) {
						if (mov != null) {
							if (mov.entityHit != null) {
								Allomancy.XPC.tryPushEntity(mov.entityHit);
							}
						}
						ray = player.rayTrace(20.0F, 0.0F);
						if (ray != null) {

							if (ray.typeOfHit == RayTraceResult.Type.BLOCK
									|| ray.typeOfHit == RayTraceResult.Type.MISS) {

								bp = ray.getBlockPos();
								if (Allomancy.XPC
										.isBlockMetal(Minecraft.getMinecraft().world.getBlockState(bp).getBlock())) {
									Allomancy.XPC.tryPushBlock(bp);
								}
							}

						}

					}
					// All brass powers
					if (cap.getMetalBurning(AllomancyCapabilities.matBrass)) {
						Entity entity;
						if ((mov != null) && (mov.entityHit != null) && (mov.entityHit instanceof EntityCreature)
								&& !(mov.entityHit instanceof EntityPlayer)) {
							entity = mov.entityHit;
							Registry.network.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), false));

						}
					}

				}

				// Pewter's speed powers
				if (cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
					if ((player.onGround) && (!player.isInWater())
							&& (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown())) {
						player.motionX *= 1.2;
						player.motionZ *= 1.2;

						// Don't allow motion values to get too out of the norm
						player.motionX = MathHelper.clamp((float) player.motionX, -2, 2);
						player.motionZ = MathHelper.clamp((float) player.motionZ, -2, 2);
					}
					if (Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed()) {
						if (player.motionY >= 0) {
							player.motionY *= 1.6;
							// Don't allow motion values to get too out of the norm
							player.motionY = MathHelper.clamp((float) player.motionY, -2, 2);
						}
						player.motionX *= 1.4;
						player.motionZ *= 1.4;
						// Don't allow motion values to get too out of the norm
						player.motionX = MathHelper.clamp((float) player.motionX, -2, 2);
						player.motionZ = MathHelper.clamp((float) player.motionZ, -2, 2);
					}

				}

				if (cap.getMetalBurning(AllomancyCapabilities.matBronze)) {
					AxisAlignedBB boxBurners;
					List<Entity> eListBurners;
					// Add metal burners to a list
					boxBurners = new AxisAlignedBB((player.posX - 30), (player.posY - 30), (player.posZ - 30),
							(player.posX + 30), (player.posY + 30), (player.posZ + 30));
					eListBurners = player.world.getEntitiesWithinAABB(Entity.class, boxBurners);
					for (Entity curEntity : eListBurners) {
						if (curEntity != null && (curEntity instanceof EntityPlayer) && curEntity != player) {
							AllomancyCapabilities capOther = AllomancyCapabilities.forPlayer(curEntity);
							Registry.network.sendToServer(
									new GetCapabilitiesPacket(curEntity.getEntityId(), player.getEntityId()));
							if (!capOther.getMetalBurning(AllomancyCapabilities.matCopper)) {
								if (capOther.getMetalBurning(AllomancyCapabilities.matIron)
										|| capOther.getMetalBurning(AllomancyCapabilities.matSteel)
										|| capOther.getMetalBurning(AllomancyCapabilities.matTin)
										|| capOther.getMetalBurning(AllomancyCapabilities.matPewter)
										|| capOther.getMetalBurning(AllomancyCapabilities.matZinc)
										|| capOther.getMetalBurning(AllomancyCapabilities.matBrass)
										|| capOther.getMetalBurning(AllomancyCapabilities.matBronze)) {
									Allomancy.XPC.addBurningPlayer((EntityPlayer) curEntity);
								}
							}
						}
					}
				} else {
					Allomancy.XPC.metalBurners.clear();
				}

				// Remove items from the metal list
				LinkedList<Entity> toRemoveMetal = new LinkedList<Entity>();

				for (Entity entity : Allomancy.XPC.particleTargets) {

					if (entity.isDead) {
						toRemoveMetal.add(entity);
					}
					if (player == null) {
						return;
					}
					if (player.getDistanceToEntity(entity) > 10) {
						toRemoveMetal.add(entity);
					}
				}

				for (Entity entity : toRemoveMetal) {
					Allomancy.XPC.particleTargets.remove(entity);
				}
				toRemoveMetal.clear();

				// Remove items from burners
				LinkedList<EntityPlayer> toRemoveBurners = new LinkedList<EntityPlayer>();

				for (EntityPlayer entity : Allomancy.XPC.metalBurners) {
					AllomancyCapabilities capOther = AllomancyCapabilities.forPlayer(entity);
					Registry.network
							.sendToServer(new GetCapabilitiesPacket(entity.getEntityId(), player.getEntityId()));
					if (entity.isDead) {
						toRemoveBurners.add(entity);
					}

					if (player != null && player.getDistanceToEntity(entity) > 10) {
						toRemoveBurners.add(entity);
					}
					if (capOther.getMetalBurning(AllomancyCapabilities.matCopper)
							|| !(capOther.getMetalBurning(AllomancyCapabilities.matIron)
									|| capOther.getMetalBurning(AllomancyCapabilities.matSteel)
									|| capOther.getMetalBurning(AllomancyCapabilities.matTin)
									|| capOther.getMetalBurning(AllomancyCapabilities.matPewter)
									|| capOther.getMetalBurning(AllomancyCapabilities.matZinc)
									|| capOther.getMetalBurning(AllomancyCapabilities.matBrass)
									|| capOther.getMetalBurning(AllomancyCapabilities.matBronze))) {
						toRemoveBurners.add(entity);
					}
				}

				for (Entity entity : toRemoveBurners) {
					Allomancy.XPC.metalBurners.remove(entity);
				}
				toRemoveBurners.clear();
			}
		}
	}

	@SubscribeEvent
	public void onDamage(LivingHurtEvent event) {
		// Increase outgoing damage for pewter burners
		if (event.getSource().getSourceOfDamage() instanceof EntityPlayerMP) {
			EntityPlayerMP source = (EntityPlayerMP) event.getSource().getSourceOfDamage();
			AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(source);

			if (cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
				event.setAmount(event.getAmount() + 2);
			}
		}
		// Reduce incoming damage for pewter burners
		if (event.getEntityLiving() instanceof EntityPlayerMP) {
			AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(event.getEntityLiving());
			if (cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
				event.setAmount(event.getAmount() - 2);
				// Note that they took damage, will come in to play if they stop
				// burning
				cap.setDamageStored(cap.getDamageStored() + 1);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (Registry.changeGroup.isPressed()) {

			EntityPlayerSP player;
			player = Minecraft.getMinecraft().player;
			Minecraft mc = FMLClientHandler.instance().getClient();
			if (mc.currentScreen == null) {
				if ((player == null) || !Minecraft.getMinecraft().inGameHasFocus) {
					return;
				}
				AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
				Registry.network.sendToServer(new SelectMetalPacket(cap.getSelected() + 1));
				cap.setSelected(cap.getSelected() + 1);
			}
		}
		if (Registry.burnFirst.isPressed()) {
			EntityPlayerSP player;
			player = Minecraft.getMinecraft().player;
			AllomancyCapabilities cap;
			Minecraft mc = FMLClientHandler.instance().getClient();
			if (mc.currentScreen == null) {
				if (player == null) {
					return;
				}
				cap = AllomancyCapabilities.forPlayer(player);
				switch (cap.getSelected()) {
				case 1:
					// toggle iron.
					Registry.network.sendToServer(new UpdateBurnPacket(AllomancyCapabilities.matIron,
							!cap.getMetalBurning(AllomancyCapabilities.matIron)));

					if (cap.getMetalAmounts(AllomancyCapabilities.matIron) > 0) {
						cap.setMetalBurning(AllomancyCapabilities.matIron,
								!cap.getMetalBurning(AllomancyCapabilities.matIron));
					}
					// play a sound effect
					if (cap.getMetalBurning(AllomancyCapabilities.matIron)) {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1, 5);
					} else {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
					}
					break;
				case 2:
					// toggle Tin.

					Registry.network.sendToServer(new UpdateBurnPacket(AllomancyCapabilities.matTin,
							!cap.getMetalBurning(AllomancyCapabilities.matTin)));
					if (cap.getMetalAmounts(AllomancyCapabilities.matTin) > 0) {
						cap.setMetalBurning(AllomancyCapabilities.matTin,
								!cap.getMetalBurning(AllomancyCapabilities.matTin));
					}
					// play a sound effect
					if (cap.getMetalBurning(AllomancyCapabilities.matTin)) {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1, 5);
					} else {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
					}
					break;
				case 3:
					// toggle Zinc.

					Registry.network.sendToServer(new UpdateBurnPacket(AllomancyCapabilities.matZinc,
							!cap.getMetalBurning(AllomancyCapabilities.matZinc)));
					if (cap.getMetalAmounts(AllomancyCapabilities.matZinc) > 0) {
						cap.setMetalBurning(AllomancyCapabilities.matZinc,
								!cap.getMetalBurning(AllomancyCapabilities.matZinc));
					}
					// play a sound effect
					if (cap.getMetalBurning(AllomancyCapabilities.matZinc)) {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1, 5);
					} else {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
					}
					break;
				case 4:
					// toggle Copper.

					Registry.network.sendToServer(new UpdateBurnPacket(AllomancyCapabilities.matCopper,
							!cap.getMetalBurning(AllomancyCapabilities.matCopper)));
					if (cap.getMetalAmounts(AllomancyCapabilities.matCopper) > 0) {
						cap.setMetalBurning(AllomancyCapabilities.matCopper,
								!cap.getMetalBurning(AllomancyCapabilities.matCopper));
					}
					// play a sound effect
					if (cap.getMetalBurning(AllomancyCapabilities.matCopper)) {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1, 5);
					} else {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
					}
					break;
				default:
					break;
				}
			}
		}
		if (Registry.burnSecond.isPressed()) {
			EntityPlayerSP player;
			player = Minecraft.getMinecraft().player;
			AllomancyCapabilities cap;
			Minecraft mc = FMLClientHandler.instance().getClient();
			if (mc.currentScreen == null) {
				if (player == null) {
					return;
				}

				cap = AllomancyCapabilities.forPlayer(player);
				switch (cap.getSelected()) {
				case 1:
					// toggle Steel.

					Registry.network.sendToServer(new UpdateBurnPacket(AllomancyCapabilities.matSteel,
							!cap.getMetalBurning(AllomancyCapabilities.matSteel)));
					if (cap.getMetalAmounts(AllomancyCapabilities.matSteel) > 0) {
						cap.setMetalBurning(AllomancyCapabilities.matSteel,
								!cap.getMetalBurning(AllomancyCapabilities.matSteel));
					}
					// play a sound effect
					if (cap.getMetalBurning(AllomancyCapabilities.matSteel)) {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1, 5);
					} else {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
					}
					break;
				case 2:
					// toggle Pewter.

					Registry.network.sendToServer(new UpdateBurnPacket(AllomancyCapabilities.matPewter,
							!cap.getMetalBurning(AllomancyCapabilities.matPewter)));
					if (cap.getMetalAmounts(AllomancyCapabilities.matPewter) > 0) {
						cap.setMetalBurning(AllomancyCapabilities.matPewter,
								!cap.getMetalBurning(AllomancyCapabilities.matPewter));
					}
					// play a sound effect
					if (cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1, 5);
					} else {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
					}
					break;
				case 3:
					// toggle Brass.

					Registry.network.sendToServer(new UpdateBurnPacket(AllomancyCapabilities.matBrass,
							!cap.getMetalBurning(AllomancyCapabilities.matBrass)));
					if (cap.getMetalAmounts(AllomancyCapabilities.matBrass) > 0) {
						cap.setMetalBurning(AllomancyCapabilities.matBrass,
								!cap.getMetalBurning(AllomancyCapabilities.matBrass));
					}
					// play a sound effect
					if (cap.getMetalBurning(AllomancyCapabilities.matBrass)) {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1, 5);
					} else {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
					}
					break;
				case 4:
					// toggle Bronze.

					Registry.network.sendToServer(new UpdateBurnPacket(AllomancyCapabilities.matBronze,
							!cap.getMetalBurning(AllomancyCapabilities.matBronze)));
					if (cap.getMetalAmounts(AllomancyCapabilities.matBronze) > 0) {
						cap.setMetalBurning(AllomancyCapabilities.matBronze,
								!cap.getMetalBurning(AllomancyCapabilities.matBronze));
					}
					// play a sound effect
					if (cap.getMetalBurning(AllomancyCapabilities.matBronze)) {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1, 5);
					} else {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
					}
					break;
				default:
					break;
				}
			}
		}
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		String name = event.getName().toString();
		if (name.startsWith("minecraft:chests/simple_dungeon") || name.startsWith("minecraft:chests/desert_pyramid")
				|| name.startsWith("minecraft:chests/jungle_temple")) {
			event.getTable().addPool(new LootPool(
					new LootEntry[] { new LootEntryTable(new ResourceLocation(Allomancy.MODID, "inject/lerasium"), 1, 0,
							new LootCondition[0], "allomancy_inject_entry") },
					new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1),
					"allomancy_inject_pool"));
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			AllomancyCapabilities oldCap = AllomancyCapabilities.forPlayer(event.getOriginal()); // the dead player's cap
			AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(event.getEntityPlayer()); // the clone's cap
			if (oldCap.isMistborn()) {
				cap.setMistborn(true); // make sure the new player has the same mistborn status
				if (event.getEntityPlayer().world.isRemote) {
					cap.setMistborn(true);
				}
				Registry.network.sendTo(new BecomeMistbornPacket(), (EntityPlayerMP) event.getEntityPlayer());
			}
			if (event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory")) { // if keepInventory is true, allow them to keep their metals, too
				for (int i = 0; i < 8; i++) {
					cap.setMetalAmounts(i, oldCap.getMetalAmounts(i));
				}
			}
		}

	}

	@SubscribeEvent
	public void onPlayerLogin(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayerMP) {
			AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(event.getEntity());
			Registry.network.sendTo(new AllomancyCapabiltiesPacket(cap, event.getEntity().getEntityId()),
					(EntityPlayerMP) event.getEntity());
			if (cap.isMistborn()) {
				Registry.network.sendTo(new BecomeMistbornPacket(), (EntityPlayerMP) event.getEntity());
				cap.setMistborn(true);
				if (event.getWorld().isRemote) {
					cap.setMistborn(true);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent event) {
		if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
			return;
		}

		this.mc = Minecraft.getMinecraft();
		this.meterLoc = new ResourceLocation("allomancy", "textures/overlay/meter.png");

		ParticlePointer particle;
		if (!Minecraft.getMinecraft().inGameHasFocus) {
			return;
		}
		if (FMLClientHandler.instance().getClient().currentScreen != null) {
			return;
		}
		EntityPlayerSP player;
		player = this.mc.player;
		if (player == null) {
			return;
		}

		this.animationCounter++;

		cap = AllomancyCapabilities.forPlayer(player);
		// left hand side.
		int ironY, steelY, tinY, pewterY;
		// right hand side
		int copperY, bronzeY, zincY, brassY;
		int renderX, renderY = 0;
		ScaledResolution res = new ScaledResolution(this.mc);

		// Set the offsets of the overlay based on config
		switch (AllomancyConfig.overlayPosition) {
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

		if (!cap.isMistborn()) {
			return;
		}
		GuiIngame gig = new GuiIngame(Minecraft.getMinecraft());
		Minecraft.getMinecraft().renderEngine.bindTexture(this.meterLoc);
		ITextureObject obj;
		obj = Minecraft.getMinecraft().renderEngine.getTexture(this.meterLoc);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

		switch (cap.getSelected()) {
		case 0:
			break;
		case 1:
			gig.drawTexturedModalRect(renderX - 2, renderY - 2, 54, 0, 16, 24);
			break;
		case 2:
			gig.drawTexturedModalRect(renderX + 23, renderY - 2, 54, 0, 16, 24);
			break;
		case 3:
			gig.drawTexturedModalRect(renderX + 48, renderY - 2, 54, 0, 16, 24);
			break;
		case 4:
			gig.drawTexturedModalRect(renderX + 73, renderY - 2, 54, 0, 16, 24);
			break;

		}

		ironY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matIron);
		gig.drawTexturedModalRect(renderX + 1, renderY + 5 + ironY, 7, 1 + ironY, 3, 10 - ironY);

		steelY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matSteel);
		gig.drawTexturedModalRect(renderX + 8, renderY + 5 + steelY, 13, 1 + steelY, 3, 10 - steelY);

		tinY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matTin);
		gig.drawTexturedModalRect(renderX + 26, renderY + 5 + tinY, 19, 1 + tinY, 3, 10 - tinY);

		pewterY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matPewter);
		gig.drawTexturedModalRect(renderX + 33, renderY + 5 + pewterY, 25, 1 + pewterY, 3, 10 - pewterY);

		zincY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matZinc);
		gig.drawTexturedModalRect(renderX + 51, renderY + 5 + zincY, 43, 1 + zincY, 3, 10 - zincY);

		brassY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matBrass);
		gig.drawTexturedModalRect(renderX + 58, renderY + 5 + brassY, 49, 1 + brassY, 3, 10 - brassY);

		copperY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matCopper);
		gig.drawTexturedModalRect(renderX + 76, renderY + 5 + copperY, 31, 1 + copperY, 3, 10 - copperY);

		bronzeY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matBronze);
		gig.drawTexturedModalRect(renderX + 83, renderY + 5 + bronzeY, 37, 1 + bronzeY, 3, 10 - bronzeY);

		// Draw the gauges second, so that highlights and decorations show over
		// the bar.
		gig.drawTexturedModalRect(renderX, renderY, 0, 0, 5, 20);
		gig.drawTexturedModalRect(renderX + 7, renderY, 0, 0, 5, 20);

		gig.drawTexturedModalRect(renderX + 25, renderY, 0, 0, 5, 20);
		gig.drawTexturedModalRect(renderX + 32, renderY, 0, 0, 5, 20);

		gig.drawTexturedModalRect(renderX + 50, renderY, 0, 0, 5, 20);
		gig.drawTexturedModalRect(renderX + 57, renderY, 0, 0, 5, 20);

		gig.drawTexturedModalRect(renderX + 75, renderY, 0, 0, 5, 20);
		gig.drawTexturedModalRect(renderX + 82, renderY, 0, 0, 5, 20);

		if (this.cap.getMetalBurning(AllomancyCapabilities.matIron)) {
			gig.drawTexturedModalRect(renderX, renderY + 5 + ironY, this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.getMetalBurning(AllomancyCapabilities.matSteel)) {
			gig.drawTexturedModalRect(renderX + 7, renderY + 5 + steelY, this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.getMetalBurning(AllomancyCapabilities.matTin)) {
			gig.drawTexturedModalRect(renderX + 25, renderY + 5 + tinY, this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
			gig.drawTexturedModalRect(renderX + 32, renderY + 5 + pewterY, this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.getMetalBurning(AllomancyCapabilities.matZinc)) {
			gig.drawTexturedModalRect(renderX + 50, renderY + 5 + zincY, this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.getMetalBurning(AllomancyCapabilities.matBrass)) {
			gig.drawTexturedModalRect(renderX + 57, renderY + 5 + brassY, this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.getMetalBurning(AllomancyCapabilities.matCopper)) {
			gig.drawTexturedModalRect(renderX + 75, renderY + 5 + copperY, this.Frames[this.currentFrame].getX(),
					this.Frames[this.currentFrame].getY(), 5, 3);
		}
		if (this.cap.getMetalBurning(AllomancyCapabilities.matBronze)) {
			gig.drawTexturedModalRect(renderX + 82, renderY + 5 + bronzeY, this.Frames[this.currentFrame].getX(),
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
		// Spawn in metal particles
		if ((this.cap.getMetalBurning(AllomancyCapabilities.matIron)
				|| this.cap.getMetalBurning(AllomancyCapabilities.matSteel))
				&& (event instanceof RenderGameOverlayEvent.Post)) {
			for (Entity entity : Allomancy.XPC.particleTargets) {
				motionX = ((player.posX - entity.posX) * -1) * .03;
				motionY = (((player.posY - entity.posY + 1.2) * -1) * .03) + .021;
				motionZ = ((player.posZ - entity.posZ) * -1) * .03;
				particle = new ParticlePointer(player.world,
						player.posX - (Math.sin(Math.toRadians(player.getRotationYawHead())) * .3d), player.posY - .2,
						player.posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .3d), motionX, motionY,
						motionZ, 0);
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}
			for (BlockPos v : Allomancy.XPC.particleBlockTargets) {
				motionX = ((player.posX - (v.getX() + .5)) * -1) * .03;
				motionY = (((player.posY - (v.getY() + .2)) * -1) * .03);
				motionZ = ((player.posZ - (v.getZ() + .5)) * -1) * .03;
				particle = new ParticlePointer(player.world,
						player.posX - (Math.sin(Math.toRadians(player.getRotationYawHead())) * .3d), player.posY - .2,
						player.posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .3d), motionX, motionY,
						motionZ, 0);
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}
			Allomancy.XPC.particleBlockTargets.clear();
		}
		if ((this.cap.getMetalBurning(AllomancyCapabilities.matBronze)
				&& (event instanceof RenderGameOverlayEvent.Post))) {
			for (EntityPlayer entityplayer : Allomancy.XPC.metalBurners) {
				motionX = ((player.posX - entityplayer.posX) * -1) * .03;
				motionY = (((player.posY - entityplayer.posY + 1.2) * -1) * .03) + .021;
				motionZ = ((player.posZ - entityplayer.posZ) * -1) * .03;
				particle = new ParticlePointer(player.world,
						player.posX - (Math.sin(Math.toRadians(player.getRotationYawHead())) * .3d), player.posY - .2,
						player.posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .3d), motionX, motionY,
						motionZ, 1);
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}

		}
	}

	// TODO: Check up on if this is fixed later
	/*
	 * @SubscribeEvent public void onSound(PlaySoundEvent event) { double
	 * motionX, motionY, motionZ, magnitude; EntityPlayerSP player; player =
	 * Minecraft.getMinecraft().thePlayer; if ((player == null) ||
	 * (event.getSound() == null) ) { return; } magnitude =
	 * Math.sqrt(Math.pow((player.posX - (event.getSound().getXPosF())),2) +
	 * Math.pow((player.posY - (event.getSound().getYPosF())),2) +
	 * Math.pow((player.posZ - (event.getSound().getYPosF())),2) );
	 * if(((magnitude) > 20) || ((magnitude) < .5)){ return; }
	 * AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
	 * //Spawn sound particles
	 * 
	 * if (cap.getMetalBurning(AllomancyCapabilities.matTin)) { if
	 * (event.getSound().getSoundLocation().toString().contains("step") ||
	 * event.getSound().getSoundLocation().toString().contains("entity") ||
	 * event.getSound().getSoundLocation().toString().contains("hostile") ||
	 * event.getSound().getSoundLocation().toString().contains(".big") ||
	 * event.getSound().getSoundLocation().toString().contains("scream") ||
	 * event.getSound().getSoundLocation().toString().contains("bow")) {
	 * System.out.println("working 2"); motionX = ((player.posX -
	 * (event.getSound().getXPosF() + .5)) * -0.7) / magnitude; motionY =
	 * ((player.posY - (event.getSound().getYPosF() + .2)) * -0.7) / magnitude;
	 * motionZ = ((player.posZ - (event.getSound().getZPosF() + .5)) * -0.7)
	 * /magnitude; Particle particle = new ParticleSound(player.worldObj,
	 * player.posX + (Math.sin(Math.toRadians(player.getRotationYawHead())) *
	 * -.7d), player.posY + .2, player.posZ +
	 * (Math.cos(Math.toRadians(player.getRotationYawHead())) * .7d), motionX,
	 * motionY, motionZ, event);
	 * Minecraft.getMinecraft().effectRenderer.addEffect(particle); }
	 * 
	 * } }
	 */

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {

			World world;
			world = (World) event.world;

			List<EntityPlayer> list = world.playerEntities;
			for (EntityPlayer curPlayer : list) {
				cap = AllomancyCapabilities.forPlayer(curPlayer);

				if (cap.isMistborn()) {
					// Damage the player if they have stored damage and pewter
					// cuts out
					if (!cap.getMetalBurning(AllomancyCapabilities.matPewter) && (cap.getDamageStored() > 0)) {
						cap.setDamageStored(cap.getDamageStored() - 1);
						curPlayer.attackEntityFrom(DamageSource.GENERIC, 2);
					}
					if (cap.getMetalBurning(AllomancyCapabilities.matTin)) {
						// Add night vision to tin-burners
						if (!curPlayer.isPotionActive(Potion.getPotionById(16))) { // Potion 16 = night vision
							curPlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(16), 300, 0, false, false));
						}
						// Remove blindness for tin burners
						if (curPlayer.isPotionActive(Potion.getPotionById(15))) { // Potion 15 is blindness
							curPlayer.removePotionEffect(Potion.getPotionById(15));

						} else {
							PotionEffect eff;
							eff = curPlayer.getActivePotionEffect(Potion.getPotionById(16));
							// Fix for the flashing that occurs when night
							// vision effect is about to run out
							if (eff.getDuration() < 210) {
								curPlayer.addPotionEffect(
										new PotionEffect(Potion.getPotionById(16), 300, 0, false, false));
							}
						}

					}
					// Remove night vision from non-tin burners if duration < 10
					// seconds. Related to the above issue with flashing
					if ((!cap.getMetalBurning(AllomancyCapabilities.matTin))
							&& curPlayer.isPotionActive(Potion.getPotionById(16))) {
						if (curPlayer.getActivePotionEffect(Potion.getPotionById(16)).getDuration() < 201) {
							curPlayer.removePotionEffect(Potion.getPotionById(16));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Ticks down BurnTime and then decrements MetalAmounts
	 * 
	 * @param 
	 * 			data the AllomancyCapability data for the player 
	 * 
	 */
	@SideOnly(Side.CLIENT)
	private void updateBurnTime(AllomancyCapabilities data) {
		for (int i = 0; i < 8; i++) {

			if (data.getMetalBurning(i)) {
				data.setBurnTime(i, data.getBurnTime(i) - 1);
				if (data.getBurnTime(i) == 0) {
					data.setBurnTime(i, data.MaxBurnTime[i]);
					data.setMetalAmounts(i, data.getMetalAmounts(i) - 1);
					Registry.network.sendToServer(new UpdateBurnPacket(i, data.getMetalBurning(i)));
					if (data.getMetalAmounts(i) == 0) {
						Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
						data.setMetalBurning(i, false);
						Registry.network.sendToServer(new UpdateBurnPacket(i, data.getMetalBurning(i)));
					}
				}

			}
		}
	}

	/* 
	 * This code is based almost entirely on the vanilla code. It's not super
	 * well documented, but basically it just runs a ray-trace. Edit at your own peril
	 */
	@SideOnly(Side.CLIENT)
	public static RayTraceResult getMouseOverExtended(float dist) {
		Minecraft mc = FMLClientHandler.instance().getClient();
		Entity theRenderViewEntity = mc.getRenderViewEntity();
		AxisAlignedBB theViewBoundingBox = new AxisAlignedBB(theRenderViewEntity.posX - 0.5D,
				theRenderViewEntity.posY - 0.0D, theRenderViewEntity.posZ - 0.5D, theRenderViewEntity.posX + 0.5D,
				theRenderViewEntity.posY + 1.5D, theRenderViewEntity.posZ + 0.5D);
		RayTraceResult returnMOP = null;
		if (mc.world != null) {
			double var2 = dist;
			returnMOP = theRenderViewEntity.rayTrace(var2, 0);
			double calcdist = var2;
			Vec3d pos = theRenderViewEntity.getPositionEyes(0);
			var2 = calcdist;
			if (returnMOP != null) {
				calcdist = returnMOP.hitVec.distanceTo(pos);
			}
			Vec3d lookvec = theRenderViewEntity.getLook(0);
			Vec3d var8 = pos.addVector(lookvec.xCoord * var2, lookvec.yCoord * var2, lookvec.zCoord * var2);
			Entity pointedEntity = null;
			float var9 = 1.0F;
			@SuppressWarnings("unchecked")
			List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(theRenderViewEntity,
					theViewBoundingBox.addCoord(lookvec.xCoord * var2, lookvec.yCoord * var2, lookvec.zCoord * var2)
							.expand(var9, var9, var9));
			double d = calcdist;
			for (Entity entity : list) {
				float bordersize = entity.getCollisionBorderSize();
				AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - entity.width / 2, entity.posY,
						entity.posZ - entity.width / 2, entity.posX + entity.width / 2, entity.posY + entity.height,
						entity.posZ + entity.width / 2);
				aabb.expand(bordersize, bordersize, bordersize);
				RayTraceResult mop0 = aabb.calculateIntercept(pos, var8);
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
			if (pointedEntity != null && (d < calcdist || returnMOP == null)) {
				returnMOP = new RayTraceResult(pointedEntity);
			}
		}
		return returnMOP;
	}
}
