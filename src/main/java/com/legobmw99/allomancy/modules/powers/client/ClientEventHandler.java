package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.PowerUtils;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalSelectScreen;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import com.legobmw99.allomancy.modules.powers.client.util.ClientUtils;
import com.legobmw99.allomancy.modules.powers.client.util.MetalBlockBlob;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.modules.powers.network.ChangeEmotionPacket;
import com.legobmw99.allomancy.modules.powers.network.TryPushPullBlock;
import com.legobmw99.allomancy.modules.powers.network.TryPushPullEntity;
import com.legobmw99.allomancy.modules.powers.network.UpdateEnhancedPacket;
import com.legobmw99.allomancy.network.Network;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientEventHandler {


    private final Minecraft mc = Minecraft.getInstance();

    private final Set<Entity> metal_entities = new HashSet<>();
    private final Set<MetalBlockBlob> metal_blobs = new HashSet<>();
    private final Set<Player> nearby_allomancers = new HashSet<>();

    private int tickOffset = 0;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        // Run once per tick, only if in game, and only if there is a player
        if (event.phase == TickEvent.Phase.END && !this.mc.isPaused() && this.mc.player != null && this.mc.player.isAlive()) {

            Player player = this.mc.player;
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                if (!data.isUninvested()) {
                    // Duralumin makes you move much quicker and reach much further
                    int force_multiplier = data.isEnhanced() ? 4 : 1;
                    int dist_modifier = data.isEnhanced() ? 2 : 1;

                    // Handle our input-based powers
                    if (this.mc.options.keyAttack.isDown()) {
                        // Ray trace 20 blocks (or 40 if enhanced)
                        var trace = ClientUtils.getMouseOverExtended(20F * dist_modifier);
                        // All iron pulling powers
                        if (data.isBurning(Metal.IRON)) {
                            if (trace != null) {
                                if (trace.getType() == HitResult.Type.ENTITY && PowerUtils.isEntityMetal(((EntityHitResult) trace).getEntity())) {
                                    Network.sendToServer(new TryPushPullEntity(((EntityHitResult) trace).getEntity().getId(), PowerUtils.PULL * force_multiplier));
                                }

                                if (trace.getType() == HitResult.Type.BLOCK) {
                                    BlockPos bp = ((BlockHitResult) trace).getBlockPos();
                                    if (PowerUtils.isBlockStateMetal(this.mc.level.getBlockState(bp)) ||
                                        (player.getMainHandItem().getItem() == CombatSetup.COIN_BAG.get() && player.isCrouching())) {
                                        Network.sendToServer(new TryPushPullBlock(bp, PowerUtils.PULL * force_multiplier));
                                    }
                                }
                            }
                        }
                        // All zinc powers
                        if (data.isBurning(Metal.ZINC)) {
                            Entity entity;
                            if ((trace != null) && (trace.getType() == HitResult.Type.ENTITY)) {
                                entity = ((EntityHitResult) trace).getEntity();
                                if (entity instanceof PathfinderMob) {
                                    Network.sendToServer(new ChangeEmotionPacket(entity.getId(), true));
                                }
                            }
                        }
                    }
                    if (this.mc.options.keyUse.isDown()) {
                        // Ray trace 20 blocks (or 40 if enhanced)
                        var trace = ClientUtils.getMouseOverExtended(20F * dist_modifier);
                        // All steel pushing powers
                        if (data.isBurning(Metal.STEEL)) {

                            if (trace != null) {
                                if (trace.getType() == HitResult.Type.ENTITY && PowerUtils.isEntityMetal(((EntityHitResult) trace).getEntity())) {
                                    Network.sendToServer(new TryPushPullEntity(((EntityHitResult) trace).getEntity().getId(), PowerUtils.PUSH * force_multiplier));
                                }

                                if (trace.getType() == HitResult.Type.BLOCK) {
                                    BlockPos bp = ((BlockHitResult) trace).getBlockPos();
                                    if (PowerUtils.isBlockStateMetal(this.mc.level.getBlockState(bp)) ||
                                        (player.getMainHandItem().getItem() == CombatSetup.COIN_BAG.get() && player.isCrouching())) {
                                        Network.sendToServer(new TryPushPullBlock(bp, PowerUtils.PUSH * force_multiplier));
                                    }
                                }
                            }
                        }
                        // All brass powers
                        if (data.isBurning(Metal.BRASS)) {
                            Entity entity;
                            if ((trace != null) && (trace.getType() == HitResult.Type.ENTITY)) {
                                entity = ((EntityHitResult) trace).getEntity();
                                if (entity instanceof PathfinderMob) {
                                    Network.sendToServer(new ChangeEmotionPacket(entity.getId(), false));
                                }
                            }
                        }

                        if (data.isBurning(Metal.NICROSIL)) {
                            if ((trace != null) && (trace.getType() == HitResult.Type.ENTITY)) {
                                Entity entity = ((EntityHitResult) trace).getEntity();
                                if (entity instanceof Player) {
                                    Network.sendToServer(new UpdateEnhancedPacket(true, entity.getId()));
                                }
                            }
                        }
                    }

                    this.tickOffset = (this.tickOffset + 1) % 2;

                    if (this.tickOffset == 0) {
                        // Populate the metal lists
                        this.metal_blobs.clear();
                        this.metal_entities.clear();
                        if (data.isBurning(Metal.IRON) || data.isBurning(Metal.STEEL)) {
                            int max = PowersConfig.max_metal_detection.get();
                            BlockPos negative = player.blockPosition().offset(-max, -max, -max);
                            BlockPos positive = player.blockPosition().offset(max, max, max);

                            // Add metal entities to metal list
                            this.metal_entities.addAll(
                                    player.level.getEntitiesOfClass(Entity.class, new AABB(negative, positive), e -> PowerUtils.isEntityMetal(e) && !e.equals(player)));

                            // Add metal blobs to metal list
                            var blocks = BlockPos.betweenClosedStream(negative, positive);
                            blocks.filter(bp -> PowerUtils.isBlockStateMetal(player.level.getBlockState(bp))).forEach(bp -> {
                                var matches = this.metal_blobs.stream().filter(mbl -> mbl.isMatch(bp)).collect(Collectors.toSet());
                                switch (matches.size()) {
                                    case 0 -> // new blob
                                            this.metal_blobs.add(new MetalBlockBlob(bp));
                                    case 1 -> // add to existing blob
                                            matches.stream().findAny().get().add(bp);
                                    default -> { // this block serves as a bridge between (possibly many) existing blobs
                                        this.metal_blobs.removeAll(matches);
                                        MetalBlockBlob mbb = matches.stream().reduce(null, MetalBlockBlob::merge);
                                        mbb.add(bp);
                                        this.metal_blobs.add(mbb);
                                    }
                                }

                            });

                        }
                        // Populate our list of nearby allomancy users
                        this.nearby_allomancers.clear();
                        if (data.isBurning(Metal.BRONZE) && (data.isEnhanced() || !data.isBurning(Metal.COPPER))) {
                            // Add metal burners to a list
                            BlockPos negative = player.blockPosition().offset(-30, -30, -30);
                            BlockPos positive = player.blockPosition().offset(30, 30, 30);
                            var nearby_players = player.level.getEntitiesOfClass(Player.class, new AABB(negative, positive), entity -> entity != null && entity != player);


                            for (Player otherPlayer : nearby_players) {

                                boolean cont = otherPlayer.getCapability(AllomancerCapability.PLAYER_CAP).map(otherData -> {
                                    if (otherData.isBurning(Metal.COPPER) && (!data.isEnhanced() || otherData.isEnhanced())) {
                                        return false;
                                    }
                                    for (Metal mt : Metal.values()) {
                                        if (otherData.isBurning(mt)) {
                                            this.nearby_allomancers.add(otherPlayer);
                                            break;
                                        }
                                    }
                                    return true;
                                }).orElse(true);

                                if (!cont) {
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            acceptInput();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseInput(final InputEvent.MouseInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            acceptInput();
        }
    }

    /**
     * Handles either mouse or button presses for the mod's keybinds
     */
    private void acceptInput() {

        boolean extras = PowersClientSetup.enable_more_keybinds && Arrays.stream(PowersClientSetup.powers).anyMatch(KeyMapping::isDown);

        if (PowersClientSetup.burn.isDown() || extras) {
            Player player = this.mc.player;
            if (this.mc.screen == null) {
                if (player == null || !this.mc.isWindowActive()) {
                    return;
                }

                player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                    if (extras) { // try one of the extra keybinds
                        for (int i = 0; i < PowersClientSetup.powers.length; i++) {
                            if (PowersClientSetup.powers[i].isDown()) {
                                ClientUtils.toggleBurn(Metal.getMetal(i), data);
                            }
                        }

                    } else { // normal keypress

                        int num_powers = data.getPowerCount();

                        if (num_powers == 0) {
                            return;
                        } else if (num_powers == 1) {
                            ClientUtils.toggleBurn(data.getPowers()[0], data);
                        } else {
                            this.mc.setScreen(new MetalSelectScreen());
                        }
                    }
                });
            }

        }

        if (PowersClientSetup.hud.isDown()) {
            PowersConfig.enable_overlay.set(!PowersConfig.enable_overlay.get());
        }

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {

        if (!PowersConfig.enable_overlay.get() && !(this.mc.screen instanceof MetalSelectScreen)) {
            return;
        }
        if (event.isCancelable() || event.getType() != ElementType.LAYER) {
            return;
        }
        if (!this.mc.isWindowActive() || !this.mc.player.isAlive()) {
            return;
        }
        if (this.mc.screen != null && !(this.mc.screen instanceof ChatScreen) && !(this.mc.screen instanceof MetalSelectScreen)) {
            return;
        }

        MetalOverlay.drawMetalOverlay(event.getMatrixStack());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {

        Player player = this.mc.player;
        if (player == null || !player.isAlive() || this.mc.options.getCameraType().isMirrored()) {
            return;
        }

        player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {

            if (data.isUninvested()) {
                return;
            }
            RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableTexture();


            PoseStack stack = RenderSystem.getModelViewStack();
            stack.pushPose();
            Vec3 view = this.mc.cameraEntity.getEyePosition(event.getPartialTicks());
            stack.translate(-view.x, -view.y, -view.z);

            //            stack.mulPoseMatrix(stack.last().pose());
            //            System.out.println(stack.last().);
            RenderSystem.applyModelViewMatrix();

            double rho = 1;
            double theta = (this.mc.player.getViewYRot(event.getPartialTicks())) * Math.PI / 180;
            double phi = (this.mc.player.getViewXRot(event.getPartialTicks()) + 90) * Math.PI / 180;
            //            System.out.println(theta);

            Vec3 playervec = view.add(rho * Math.sin((float) phi) * Math.cos((float) theta), rho * Math.cos((float) phi), rho * Math.sin((float) phi) * Math.sin((float) theta));

            //            double yaw = ((this.mc.player.getYRot() + 180) * Math.PI) / 180;
            //            double pitch = ((this.mc.player.getXRot() + 90) * Math.PI) / 180;
            //            // TODO broken
            //            //            Vec3 playervec = view.add(Mth.sin((float) pitch) * Mth.cos((float) yaw) * dist, Mth.cos((float) pitch) * dist - 0.35,
            //            //                                      Mth.sin((float) pitch) * Mth.sin((float) yaw) * dist);
            //            Vec3 playervec = view.add(this.mc.gameRenderer.getMainCamera().getNearPlane().getBottomRight());
            //            System.out.println();
            /*********************************************
             * IRON AND STEEL LINES                      *
             *********************************************/

            ClientUtils.drawMetalLine(new Vec3(6, 4, 3), new Vec3(7, 6, 2), 1.5F, 0F, 0.6F, 1F);

            if ((data.isBurning(Metal.IRON) || data.isBurning(Metal.STEEL))) {
                for (Entity entity : this.metal_entities) {
                    ClientUtils.drawMetalLine(playervec, entity.position(), 1.5F, 0F, 0.6F, 1F);
                }

                for (MetalBlockBlob mb : this.metal_blobs) {
                    ClientUtils.drawMetalLine(playervec, mb.getCenter(), Mth.clamp(0.3F + mb.size() * 0.4F, 0.5F, 7.5F), 0F, 0.6F, 1F);
                }
            }

            /*********************************************
             * BRONZE LINES                              *
             *********************************************/
            if ((data.isBurning(Metal.BRONZE) && (data.isEnhanced() || !data.isBurning(Metal.COPPER)))) {
                for (Player playerEntity : this.nearby_allomancers) {
                    ClientUtils.drawMetalLine(playervec, playerEntity.position(), 5.0F, 0.7F, 0.15F, 0.15F);
                }
            }

            /*********************************************
             * GOLD AND ELECTRUM LINES                   *
             *********************************************/
            if (data.isBurning(Metal.GOLD)) {
                ResourceKey<Level> deathDim = data.getDeathDim();
                if (deathDim != null && player.level.dimension() == deathDim) { //world .getDim (look for return type matches)
                    ClientUtils.drawMetalLine(playervec, Vec3.atCenterOf(data.getDeathLoc()), 3.0F, 0.9F, 0.85F, 0.0F);
                }
            }
            if (data.isBurning(Metal.ELECTRUM)) {
                ResourceKey<Level> spawnDim = data.getSpawnDim();
                if (spawnDim == null && player.level.dimension() == Level.OVERWORLD) { // overworld, no spawn --> use world spawn
                    BlockPos spawnLoc = new BlockPos(player.level.getLevelData().getXSpawn(), player.level.getLevelData().getYSpawn(), player.level.getLevelData().getZSpawn());
                    ClientUtils.drawMetalLine(playervec, Vec3.atCenterOf(spawnLoc), 3.0F, 0.7F, 0.8F, 0.2F);

                } else if (spawnDim != null && player.level.dimension() == spawnDim) {
                    ClientUtils.drawMetalLine(playervec, Vec3.atCenterOf(data.getSpawnLoc()), 3.0F, 0.7F, 0.8F, 0.2F);
                }
            }

            stack.popPose();
            RenderSystem.applyModelViewMatrix();

            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();

        });
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {

        Player player = this.mc.player;
        SoundInstance sound = event.getSound();
        if ((player == null) || (sound == null) || !player.isAlive()) {
            return;
        }

        player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
            double motionX, motionY, motionZ, magnitude;

            if (data.isBurning(Metal.TIN)) {

                magnitude = Math.sqrt(player.position().distanceToSqr(sound.getX(), sound.getY(), sound.getZ()));

                if (((magnitude) > 25) || ((magnitude) < 3)) {
                    return;
                }
                Vec3 vec = player.position();
                double posX = vec.x(), posY = vec.y(), posZ = vec.z();
                // Spawn sound particles
                String soundName = sound.getLocation().toString();
                if (soundName.contains("entity") || soundName.contains("step")) {
                    motionX = ((posX - (event.getSound().getX() + .5)) * -0.7) / magnitude;
                    motionY = ((posY - (event.getSound().getY() + .2)) * -0.7) / magnitude;
                    motionZ = ((posZ - (event.getSound().getZ() + .5)) * -0.7) / magnitude;
                    this.mc.particleEngine.createParticle(new SoundParticleData(sound.getSource()), posX + (Math.sin(Math.toRadians(player.getYHeadRot())) * -.7d), posY + .2,
                                                          posZ + (Math.cos(Math.toRadians(player.getYHeadRot())) * .7d), motionX, motionY, motionZ);
                }
            }
        });
    }
}
