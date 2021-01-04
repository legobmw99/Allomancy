package com.legobmw99.allomancy.modules.powers.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalSelectScreen;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import com.legobmw99.allomancy.modules.powers.network.ChangeEmotionPacket;
import com.legobmw99.allomancy.modules.powers.network.TryPushPullBlock;
import com.legobmw99.allomancy.modules.powers.network.TryPushPullEntity;
import com.legobmw99.allomancy.modules.powers.network.UpdateEnhancedPacket;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.modules.powers.util.PowerUtils;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.setup.Metal;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {


    private final Minecraft mc = Minecraft.getInstance();

    private final Set<Entity> metal_entities = new HashSet<>();
    private final Set<BlockPos> metal_blocks = new HashSet<>();
    private final Set<PlayerEntity> nearby_allomancers = new HashSet<>();

    private static boolean hud;
    
    public static void setHud() {
	hud = PowersConfig.enable_overlay.get();
    }
    
    private static Vector3d blockVec(BlockPos b) {
        return new Vector3d(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        // Run once per tick, only if in game, and only if there is a player
        if (event.phase == TickEvent.Phase.END && !this.mc.isGamePaused() && this.mc.player != null && this.mc.player.isAlive()) {

            PlayerEntity player = mc.player;
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);

            if (!cap.isUninvested()) {
                // Duralumin makes you move much quicker and reach much further
                int force_multiplier = cap.isEnhanced() ? 4 : 1;
                int dist_modifier = cap.isEnhanced() ? 2 : 1;

                // Handle our input-based powers
                if (this.mc.gameSettings.keyBindAttack.isKeyDown()) {
                    // Ray trace 20 blocks (or 40 if enhanced)
                    RayTraceResult trace = ClientUtils.getMouseOverExtended(20F * dist_modifier);
                    // All iron pulling powers
                    if (cap.isBurning(Metal.IRON)) {
                        if (trace != null) {
                            if (trace.getType() == RayTraceResult.Type.ENTITY && PowerUtils.isEntityMetal(((EntityRayTraceResult) trace).getEntity())) {
                                Network.sendToServer(new TryPushPullEntity(((EntityRayTraceResult) trace).getEntity().getEntityId(), PowerUtils.PULL * force_multiplier));
                            }

                            if (trace.getType() == RayTraceResult.Type.BLOCK) {
                                BlockPos bp = ((BlockRayTraceResult) trace).getPos();
                                if (PowerUtils.isBlockStateMetal(this.mc.world.getBlockState(bp)) || (player.getHeldItemMainhand().getItem() == CombatSetup.COIN_BAG.get() && player.isCrouching())) {
                                    Network.sendToServer(new TryPushPullBlock(bp, PowerUtils.PULL * force_multiplier));
                                }
                            }
                        }
                    }
                    // All zinc powers
                    if (cap.isBurning(Metal.ZINC)) {
                        Entity entity;
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof CreatureEntity) {
                                Network.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), true));
                            }
                        }
                    }
                }
                if (this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    // Ray trace 20 blocks (or 40 if enhanced)
                    RayTraceResult trace = ClientUtils.getMouseOverExtended(20F * dist_modifier);
                    // All steel pushing powers
                    if (cap.isBurning(Metal.STEEL)) {

                        if (trace != null) {
                            if (trace.getType() == RayTraceResult.Type.ENTITY && PowerUtils.isEntityMetal(((EntityRayTraceResult) trace).getEntity())) {
                                Network.sendToServer(new TryPushPullEntity(((EntityRayTraceResult) trace).getEntity().getEntityId(), PowerUtils.PUSH * force_multiplier));
                            }

                            if (trace.getType() == RayTraceResult.Type.BLOCK) {
                                BlockPos bp = ((BlockRayTraceResult) trace).getPos();
                                if (PowerUtils.isBlockStateMetal(this.mc.world.getBlockState(bp)) || (player.getHeldItemMainhand().getItem() == CombatSetup.COIN_BAG.get() && player.isCrouching())) {
                                    Network.sendToServer(new TryPushPullBlock(bp, PowerUtils.PUSH * force_multiplier));
                                }
                            }
                        }
                    }
                    // All brass powers
                    if (cap.isBurning(Metal.BRASS)) {
                        Entity entity;
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof CreatureEntity) {
                                Network.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), false));
                            }
                        }
                    }

                    if (cap.isBurning(Metal.NICROSIL)) {
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            Entity entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof PlayerEntity) {
                                Network.sendToServer(new UpdateEnhancedPacket(true, entity.getEntityId()));
                            }
                        }
                    }
                }


                // Populate the metal lists
                metal_blocks.clear();
                metal_entities.clear();
                if (cap.isBurning(Metal.IRON) || cap.isBurning(Metal.STEEL)) {
                    List<Entity> entities;
                    Stream<BlockPos> blocks;
                    int max = PowersConfig.max_metal_detection.get();
                    BlockPos negative = new BlockPos(player.getPositionVec()).add(-max, -max, -max);
                    BlockPos positive = new BlockPos(player.getPositionVec()).add(max, max, max);

                    // Add metal entities to metal list
                    entities = player.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(negative, positive));
                    entities.forEach(entity -> {
                        if (PowerUtils.isEntityMetal(entity)) {
                            metal_entities.add(entity);
                        }
                    });

                    // Add metal blocks to metal list
                    blocks = BlockPos.getAllInBox(negative, positive);
                    blocks.forEach(bp -> {
                        BlockPos imBlock = bp.toImmutable();
                        if (PowerUtils.isBlockStateMetal(player.world.getBlockState(imBlock))) {
                            metal_blocks.add(imBlock);
                        }
                    });

                }
                // Populate our list of nearby allomancy users
                nearby_allomancers.clear();
                if (cap.isBurning(Metal.BRONZE) && !cap.isBurning(Metal.COPPER)) {
                    List<PlayerEntity> nearby_players;
                    // Add metal burners to a list
                    BlockPos negative = new BlockPos(player.getPositionVec()).add(-30, -30, -30);
                    BlockPos positive = new BlockPos(player.getPositionVec()).add(30, 30, 30);
                    // Add entities to metal list
                    nearby_players = player.world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(negative, positive), entity -> entity != null && entity != player);

                    for (PlayerEntity otherPlayer : nearby_players) {
                        AllomancyCapability capOther = AllomancyCapability.forPlayer(otherPlayer);
                        if (capOther.isBurning(Metal.COPPER) && (!cap.isEnhanced() || capOther.isEnhanced())) {
                            // player is inside a smoker cloud, should not detect
                            nearby_allomancers.clear();
                            return;
                        } else {
                            boolean isBurning = false;
                            for (Metal mt : Metal.values()) {
                                if (capOther.isBurning(mt)) {
                                    isBurning = true;
                                    break;
                                }
                            }
                            if (isBurning) {
                                nearby_allomancers.add(otherPlayer);
                            }
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (PowersClientSetup.burn.isPressed()) {
            PlayerEntity player = mc.player;
            AllomancyCapability cap;
            if (mc.currentScreen == null) {
                if (player == null || !this.mc.isGameFocused()) {
                    return;
                }
                cap = AllomancyCapability.forPlayer(player);

                if (!cap.isUninvested()) {
                    this.mc.displayGuiScreen(new MetalSelectScreen());
                }
            }
        }
        
        if (PowersClientSetup.hud.isPressed()) {
            PowersConfig.enable_overlay.set(!PowersConfig.enable_overlay.get());
            hud = PowersConfig.enable_overlay.get();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {

	if(!hud) {
            return;
        }
        if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
            return;
        }
        if (!this.mc.isGameFocused() || !this.mc.player.isAlive()) {
            return;
        }
        if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof ChatScreen) && !(this.mc.currentScreen instanceof MetalSelectScreen)) {
            return;
        }

        MetalOverlay.drawMetalOverlay();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        PlayerEntity player = mc.player;
        if (player == null || !player.isAlive()) {
            return;
        }

        AllomancyCapability cap = AllomancyCapability.forPlayer(player);

        if (cap.isUninvested()) {
            return;
        }


        Vector3d view = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
        MatrixStack stack = event.getMatrixStack();
        stack.translate(-view.x, -view.y, -view.z);

        // TODO investigate depreciation
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(stack.getLast().getMatrix());
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableBlend();


        Vector3d playervec = view.add(0, -.1, 0);
        /*********************************************
         * IRON AND STEEL LINES                      *
         *********************************************/
        if ((cap.isBurning(Metal.IRON) || cap.isBurning(Metal.STEEL))) {

            for (Entity entity : metal_entities) {
                ClientUtils.drawMetalLine(playervec, entity.getPositionVec(), 1.5F, 0F, 0.6F, 1F);
            }

            for (BlockPos b : metal_blocks) {
                ClientUtils.drawMetalLine(playervec, blockVec(b), 1.5F, 0F, 0.6F, 1F);
            }
        }

        /*********************************************
         * BRONZE LINES                              *
         *********************************************/
        if ((cap.isBurning(Metal.BRONZE) && !cap.isBurning(Metal.COPPER))) {
            for (PlayerEntity playerEntity : nearby_allomancers) {
                ClientUtils.drawMetalLine(playervec, playerEntity.getPositionVec(), 5.0F, 0.7F, 0.15F, 0.15F);
            }
        }

        /*********************************************
         * GOLD AND ELECTRUM LINES                   *
         *********************************************/
        if (cap.isBurning(Metal.GOLD)) {
            RegistryKey<World> deathDim = cap.getDeathDim();
            if (deathDim != null && player.world.getDimensionKey() == deathDim) { //world .getDim (look for return type matches)
                ClientUtils.drawMetalLine(playervec, blockVec(cap.getDeathLoc()), 3.0F, 0.9F, 0.85F, 0.0F);
            }
        }
        if (cap.isBurning(Metal.ELECTRUM)) {
            RegistryKey<World> spawnDim = cap.getSpawnDim();
            if (spawnDim == null && player.world.getDimensionKey() == World.OVERWORLD) { // overworld, no spawn --> use world spawn
                BlockPos spawnLoc = new BlockPos(player.world.getWorldInfo().getSpawnX(),
                        player.world.getWorldInfo().getSpawnY(), player.world.getWorldInfo().getSpawnZ());
                ClientUtils.drawMetalLine(playervec, blockVec(spawnLoc), 3.0F, 0.7F, 0.8F, 0.2F);

            } else if (spawnDim != null && player.world.getDimensionKey() == spawnDim) {
                ClientUtils.drawMetalLine(playervec, blockVec(cap.getSpawnLoc()), 3.0F, 0.7F, 0.8F, 0.2F);
            }
        }

        RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
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
        if (cap.isBurning(Metal.TIN)) {

            magnitude = Math.sqrt(player.getDistanceSq(sound.getX(), sound.getY(), sound.getZ()));

            if (((magnitude) > 25) || ((magnitude) < 3)) {
                return;
            }
            Vector3d vec = player.getPositionVec();
            double posX = vec.getX(), posY = vec.getY(), posZ = vec.getZ();
            // Spawn sound particles
            String soundName = sound.getSoundLocation().toString();
            if (soundName.contains("entity") || soundName.contains("step")) {
                motionX = ((posX - (event.getSound().getX() + .5)) * -0.7) / magnitude;
                motionY = ((posY - (event.getSound().getY() + .2)) * -0.7) / magnitude;
                motionZ = ((posZ - (event.getSound().getZ() + .5)) * -0.7) / magnitude;
                this.mc.particles.addParticle(new SoundParticleData(sound.getCategory()), posX + (Math.sin(Math.toRadians(player.getRotationYawHead())) * -.7d), posY + .2, posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .7d), motionX,
                        motionY, motionZ);
            }
        }
    }
}
