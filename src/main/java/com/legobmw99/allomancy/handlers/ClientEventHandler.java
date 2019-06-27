package com.legobmw99.allomancy.handlers;

import com.legobmw99.allomancy.entities.particles.SoundParticle;
import com.legobmw99.allomancy.gui.MetalSelectScreen;
import com.legobmw99.allomancy.network.NetworkHelper;
import com.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import com.legobmw99.allomancy.network.packets.TryPushPullBlock;
import com.legobmw99.allomancy.network.packets.TryPushPullEntity;
import com.legobmw99.allomancy.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ClientEventHandler {


    private final Minecraft mc = Minecraft.getInstance();

    private Set<Entity> metal_entities = new HashSet<>();
    private Set<BlockPos> metal_blocks = new HashSet<>();
    private Set<PlayerEntity> nearby_allomancers = new HashSet<>();


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        // Run once per tick, only if in game, and only if there is a player
        if (event.phase == TickEvent.Phase.END && !this.mc.isGamePaused() && this.mc.player != null && this.mc.player.isAlive()) {

            PlayerEntity player = mc.player;
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);

            if (cap.getAllomancyPower() >= 0) {
                // Handle our input-based powers
                if (this.mc.gameSettings.keyBindAttack.isKeyDown()) {
                    // Ray trace 20 blocks
                    RayTraceResult trace = ClientUtils.getMouseOverExtended(20F);
                    // All iron pulling powers
                    if (cap.getMetalBurning(AllomancyCapability.IRON)) {
                        if (trace != null) {
                            if (trace.getType() == RayTraceResult.Type.ENTITY && AllomancyUtils.isEntityMetal(((EntityRayTraceResult) trace).getEntity())) {
                                NetworkHelper.sendToServer(new TryPushPullEntity(((EntityRayTraceResult) trace).getEntity().getEntityId(), AllomancyUtils.PULL));
                            }

                            if (trace.getType() == RayTraceResult.Type.BLOCK) {
                                BlockPos bp = ((BlockRayTraceResult) trace).getPos();
                                if (AllomancyUtils.isBlockMetal(this.mc.world.getBlockState(bp).getBlock()) || (player.getHeldItemMainhand().getItem() == Registry.coin_bag && player.isSneaking())) {
                                    NetworkHelper.sendToServer(new TryPushPullBlock(bp, AllomancyUtils.PULL));
                                }
                            }
                        }
                    }
                    // All zinc powers
                    if (cap.getMetalBurning(AllomancyCapability.ZINC)) {
                        Entity entity;
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof CreatureEntity) {
                                NetworkHelper.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), true));
                            }
                        }
                    }
                }
                if (this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    // Ray trace 20 blocks
                    RayTraceResult trace = ClientUtils.getMouseOverExtended(20F);
                    // All steel pushing powers
                    if (cap.getMetalBurning(AllomancyCapability.STEEL)) {
                        if (trace != null) {
                            if (trace.getType() == RayTraceResult.Type.ENTITY && AllomancyUtils.isEntityMetal(((EntityRayTraceResult) trace).getEntity())) {
                                NetworkHelper.sendToServer(new TryPushPullEntity(((EntityRayTraceResult) trace).getEntity().getEntityId(), AllomancyUtils.PUSH));
                            }

                            if (trace.getType() == RayTraceResult.Type.BLOCK) {
                                BlockPos bp = ((BlockRayTraceResult) trace).getPos();
                                if (AllomancyUtils.isBlockMetal(this.mc.world.getBlockState(bp).getBlock()) || (player.getHeldItemMainhand().getItem() == Registry.coin_bag && player.isSneaking())) {
                                    NetworkHelper.sendToServer(new TryPushPullBlock(bp, AllomancyUtils.PUSH));
                                }
                            }
                        }
                    }
                    // All brass powers
                    if (cap.getMetalBurning(AllomancyCapability.BRASS)) {
                        Entity entity;
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof CreatureEntity) {
                                NetworkHelper.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), false));
                            }
                        }
                    }
                }


                // Populate the metal lists
                metal_blocks.clear();
                metal_entities.clear();
                if (cap.getMetalBurning(AllomancyCapability.IRON) || cap.getMetalBurning(AllomancyCapability.STEEL)) {
                    List<Entity> entities;
                    Stream<BlockPos> blocks;
                    int xLoc = (int) player.posX, yLoc = (int) player.posY, zLoc = (int) player.posZ;
                    int max = AllomancyConfig.max_metal_detection;
                    BlockPos negative = new BlockPos(xLoc - max, yLoc - max, zLoc - max);
                    BlockPos positive = new BlockPos(xLoc + max, yLoc + max, zLoc + max);

                    // Add metal entities to metal list
                    entities = player.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(negative, positive));
                    entities.forEach(entity -> {
                        if (AllomancyUtils.isEntityMetal(entity)) {
                            metal_entities.add(entity);
                        }
                    });

                    // Add metal blocks to metal list
                    blocks = BlockPos.getAllInBox(negative, positive);
                    blocks.forEach(bp -> {
                        BlockPos imBlock = bp.toImmutable();
                        if (AllomancyUtils.isBlockMetal(player.world.getBlockState(imBlock).getBlock())) {
                            metal_blocks.add(imBlock);
                        }
                    });

                }
                // Populate our list of nearby allomancy users
                nearby_allomancers.clear();
                if (cap.getMetalBurning(AllomancyCapability.BRONZE) && !cap.getMetalBurning(AllomancyCapability.COPPER)) {
                    List<PlayerEntity> nearby_players;
                    // Add metal burners to a list
                    int xLoc = (int) player.posX, yLoc = (int) player.posY, zLoc = (int) player.posZ;
                    BlockPos negative = new BlockPos(xLoc - 30, yLoc - 30, zLoc - 30);
                    BlockPos positive = new BlockPos(xLoc + 30, yLoc + 30, zLoc + 30);
                    // Add entities to metal list
                    nearby_players = player.world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(negative, positive), entity -> entity != null && entity != player);

                    for (PlayerEntity otherPlayer : nearby_players) {
                        AllomancyCapability capOther = AllomancyCapability.forPlayer(otherPlayer);
                        if (capOther.getMetalBurning(AllomancyCapability.COPPER)) { // player is inside a smoker cloud, should not detect
                            nearby_allomancers.clear();
                            return;
                        } else if (capOther.getMetalBurning(AllomancyCapability.IRON) || capOther.getMetalBurning(AllomancyCapability.STEEL) || capOther.getMetalBurning(AllomancyCapability.TIN)
                                || capOther.getMetalBurning(AllomancyCapability.PEWTER) || capOther.getMetalBurning(AllomancyCapability.ZINC) || capOther.getMetalBurning(AllomancyCapability.BRASS)
                                || capOther.getMetalBurning(AllomancyCapability.BRONZE)) {
                            nearby_allomancers.add(otherPlayer);
                        }
                    }
                }
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Registry.burn.isPressed()) {
            PlayerEntity player = mc.player;
            AllomancyCapability cap;
            if (mc.currentScreen == null) {
                if (player == null || !this.mc.isGameFocused()) {
                    return;
                }
                cap = AllomancyCapability.forPlayer(player);
                /*
                 * Mistings only have one metal, so toggle that one
                 */
                if (cap.getAllomancyPower() >= 0 && cap.getAllomancyPower() < 8) {
                    ClientUtils.toggleMetalBurn(cap.getAllomancyPower(), cap);
                }

                /*
                 * If the player is a full Mistborn, display the GUI
                 */
                if (cap.getAllomancyPower() == 8) {
                    this.mc.displayGuiScreen(new MetalSelectScreen());
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {

        if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
            return;
        }
        if (!this.mc.isGameFocused() || !this.mc.player.isAlive()) {
            return;
        }
        if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof ChatScreen) && !(this.mc.currentScreen instanceof MetalSelectScreen)) {
            return;
        }

        ClientUtils.drawMetalOverlay();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGUIScreen(GuiScreenEvent.DrawScreenEvent event) {
        if (event.getGui() instanceof MetalSelectScreen && !event.isCancelable()) {
            ClientUtils.drawMetalOverlay();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        PlayerEntity player = mc.player;
        if (player == null || !player.isAlive()) {
            return;
        }

        AllomancyCapability cap = AllomancyCapability.forPlayer(player);

        if (cap.getAllomancyPower() < 0) {
            return;
        }

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks();
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();

        // Iron and Steel lines
        if ((cap.getMetalBurning(AllomancyCapability.IRON) || cap.getMetalBurning(AllomancyCapability.STEEL))) {

            for (Entity entity : metal_entities) {
                ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 1.25 + entity.getHeight() / 2.0, entity.posZ, 1.5F, 0F, 0.6F, 1F);
            }

            for (BlockPos v : metal_blocks) {
                ClientUtils.drawMetalLine(playerX, playerY, playerZ, v.getX() + 0.5, v.getY() - 1.0, v.getZ() + 0.5, 1.5F, 0F, 0.6F, 1F);
            }
        }

        if ((cap.getMetalBurning(AllomancyCapability.BRONZE) && !cap.getMetalBurning(AllomancyCapability.COPPER))) {
            for (PlayerEntity playerEntity : nearby_allomancers) {
                ClientUtils.drawMetalLine(playerX, playerY, playerZ, playerEntity.posX, playerEntity.posY - 0.5, playerEntity.posZ, 3.0F, 0.7F, 0.15F, 0.15F);
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {
        double motionX, motionY, motionZ, magnitude;

        PlayerEntity player = mc.player;
        ISound sound = event.getSound();
        if ((player == null) || (sound == null) || !player.isAlive()) {
            return;
        }

        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        if (cap.getMetalBurning(AllomancyCapability.TIN)) {

            magnitude = Math.sqrt(Math.pow((player.posX - sound.getX()), 2) + Math.pow((player.posY - sound.getY()), 2) + Math.pow((player.posZ - sound.getZ()), 2));

            if (((magnitude) > 25) || ((magnitude) < 3)) {
                return;
            }

            // Spawn sound particles
            String soundName = sound.getSoundLocation().toString();
            if (soundName.contains("entity") || soundName.contains("step")) {
                motionX = ((player.posX - (event.getSound().getX() + .5)) * -0.7) / magnitude;
                motionY = ((player.posY - (event.getSound().getY() + .2)) * -0.7) / magnitude;
                motionZ = ((player.posZ - (event.getSound().getZ() + .5)) * -0.7) / magnitude;
                Particle particle = new SoundParticle(player.world, player.posX + (Math.sin(Math.toRadians(player.getRotationYawHead())) * -.7d), player.posY + .2, player.posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .7d), motionX,
                        motionY, motionZ, soundName);
                this.mc.particles.addEffect(particle);
            }
        }
    }

}
