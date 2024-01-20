package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.PowerUtils;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalSelectScreen;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import com.legobmw99.allomancy.modules.powers.client.util.ClientUtils;
import com.legobmw99.allomancy.modules.powers.client.util.SensoryTracking;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.network.BlockPushPullPayload;
import com.legobmw99.allomancy.modules.powers.network.EmotionPayload;
import com.legobmw99.allomancy.modules.powers.network.EnhanceTimePayload;
import com.legobmw99.allomancy.modules.powers.network.EntityPushPullPayload;
import com.legobmw99.allomancy.network.Network;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

public class ClientEventHandler {
    private final Minecraft mc = Minecraft.getInstance();
    private final SensoryTracking tracking = new SensoryTracking();


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        // Run once per tick, only if in game, and only if there is a player
        if (event.phase != TickEvent.Phase.END || this.mc.isPaused() || this.mc.player == null || !this.mc.player.isAlive()) {
            return;
        }

        Player player = this.mc.player;
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        if (data.isUninvested()) {
            return;
        }
        // Duralumin makes you move much quicker and reach much further
        int dist_modifier = data.isEnhanced() ? 2 : 1;

        // Handle our input-based powers
        if (this.mc.options.keyAttack.isDown()) {
            // Ray trace 20 blocks (or 40 if enhanced)
            var trace = ClientUtils.getMouseOverExtended(20F * dist_modifier);
            // All iron pulling powers
            metalPushPull(player, data, trace, Metal.IRON, PowerUtils.PULL);
            // All zinc powers
            emotionPushPull(data, trace, Metal.ZINC, true);
        }

        if (this.mc.options.keyUse.isDown()) {
            // Ray trace 20 blocks (or 40 if enhanced)
            var trace = ClientUtils.getMouseOverExtended(20F * dist_modifier);
            // All steel pushing powers
            metalPushPull(player, data, trace, Metal.STEEL, PowerUtils.PUSH);
            // All brass powers
            emotionPushPull(data, trace, Metal.BRASS, false);

            if (data.isBurning(Metal.NICROSIL)) {
                if ((trace != null) && (trace.getType() == HitResult.Type.ENTITY)) {
                    Entity entity = ((EntityHitResult) trace).getEntity();
                    if (entity instanceof Player) {
                        Network.sendToServer(new EnhanceTimePayload(true, entity.getId()));
                    }
                }
            }
        }

        this.tracking.tick();
    }


    private static void emotionPushPull(IAllomancerData data, HitResult trace, Metal metal, boolean make_aggressive) {
        if (!data.isBurning(metal) || trace == null) {
            return;
        }

        if (trace.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) trace).getEntity();
            if (entity instanceof PathfinderMob) {
                Network.sendToServer(new EmotionPayload(entity.getId(), make_aggressive));
            }
        }
    }

    private void metalPushPull(Player player, IAllomancerData data, HitResult trace, Metal metal, byte direction) {
        if (!data.isBurning(metal) || trace == null) {
            return;
        }

        int force_multiplier = data.isEnhanced() ? 4 : 1;

        if (trace.getType() == HitResult.Type.ENTITY && PowerUtils.isEntityMetal(((EntityHitResult) trace).getEntity())) {
            Network.sendToServer(new EntityPushPullPayload(((EntityHitResult) trace).getEntity().getId(), direction * force_multiplier));
        } else if (trace.getType() == HitResult.Type.BLOCK) {
            BlockPos bp = ((BlockHitResult) trace).getBlockPos();
            if (PowerUtils.isBlockStateMetal(this.mc.level.getBlockState(bp)) || (player.getMainHandItem().getItem() == CombatSetup.COIN_BAG.get() && player.isCrouching())) {
                Network.sendToServer(new BlockPushPullPayload(bp, direction * force_multiplier));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(final InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            acceptInput();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onMouseInput(final InputEvent.MouseButton.Pre event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            acceptInput();
        }
    }

    /**
     * Handles either mouse or button presses for the mod's keybinds
     */
    private void acceptInput() {
        if (this.mc.screen != null) {
            return;
        }
        Player player = this.mc.player;
        if (player == null || !this.mc.isWindowActive()) {
            return;
        }

        if (PowersClientSetup.hud.isDown()) {
            PowersConfig.enable_overlay.set(!PowersConfig.enable_overlay.get());
            return;
        }
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        for (int i = 0; i < PowersClientSetup.powers.length; i++) {
            if (PowersClientSetup.powers[i].isDown()) {
                ClientUtils.toggleBurn(Metal.getMetal(i), data);
            }
        }
        if (PowersClientSetup.burn.isDown()) {
            switch (data.getPowerCount()) {
                case 0:
                    break;
                case 1:
                    ClientUtils.toggleBurn(data.getPowers()[0], data);
                    break;
                default:
                    this.mc.setScreen(new MetalSelectScreen());
                    break;
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderLevelStage(final RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Player player = this.mc.player;
        if (player == null || !player.isAlive() || this.mc.options.getCameraType().isMirrored()) {
            return;
        }
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);


        if (data.isUninvested()) {
            return;
        }
        PoseStack stack = setupPoseStack(event);

        double rho = 1;
        float theta = (float) ((this.mc.player.getViewYRot(event.getPartialTick()) + 90) * Math.PI / 180);
        float phi = Mth.clamp((float) ((this.mc.player.getViewXRot(event.getPartialTick()) + 90) * Math.PI / 180), 0.0001F, 3.14F);

        Vec3 playervec = this.mc.cameraEntity
                .getEyePosition(event.getPartialTick())
                .add(rho * Mth.sin(phi) * Mth.cos(theta), rho * Mth.cos(phi) - 0.35F, rho * Mth.sin(phi) * Mth.sin(theta));

        /*********************************************
         * IRON AND STEEL LINES                      *
         *********************************************/


        if ((data.isBurning(Metal.IRON) || data.isBurning(Metal.STEEL))) {
            this.tracking.forEachMetallicEntity(entity -> ClientUtils.drawMetalLine(stack, playervec, entity.position(), 1.5F, 0F, 0.6F, 1F));

            this.tracking.forEachMetalBlob(blob -> ClientUtils.drawMetalLine(stack, playervec, blob.getCenter(), Mth.clamp(0.3F + blob.size() * 0.4F, 0.5F, 7.5F), 0F, 0.6F, 1F));
        }

        /*********************************************
         * BRONZE LINES                              *
         *********************************************/
        if ((data.isBurning(Metal.BRONZE) && (data.isEnhanced() || !data.isBurning(Metal.COPPER)))) {
            this.tracking.forEachSeeked(playerEntity -> ClientUtils.drawMetalLine(stack, playervec, playerEntity.position(), 5.0F, 0.7F, 0.15F, 0.15F));
        }

        /*********************************************
         * GOLD AND ELECTRUM LINES                   *
         *********************************************/
        if (data.isBurning(Metal.GOLD)) {
            ResourceKey<Level> deathDim = data.getDeathDim();
            if (deathDim != null && player.level().dimension() == deathDim) {
                ClientUtils.drawMetalLine(stack, playervec, Vec3.atCenterOf(data.getDeathLoc()), 3.0F, 0.9F, 0.85F, 0.0F);
            }
        }
        if (data.isBurning(Metal.ELECTRUM)) {
            ResourceKey<Level> spawnDim = data.getSpawnDim();
            if (spawnDim == null && player.level().dimension() == Level.OVERWORLD) { // overworld, no spawn --> use world spawn
                var levelData = player.level().getLevelData();
                BlockPos spawnLoc = new BlockPos(levelData.getXSpawn(), levelData.getYSpawn(), levelData.getZSpawn());
                ClientUtils.drawMetalLine(stack, playervec, Vec3.atCenterOf(spawnLoc), 3.0F, 0.7F, 0.8F, 0.2F);
            } else if (spawnDim != null && player.level().dimension() == spawnDim) {
                ClientUtils.drawMetalLine(stack, playervec, Vec3.atCenterOf(data.getSpawnLoc()), 3.0F, 0.7F, 0.8F, 0.2F);
            }
        }

        teardownPoseStack(stack);

    }

    private static void teardownPoseStack(PoseStack stack) {
        stack.popPose();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.disableBlend();
        RenderSystem.enablePolygonOffset();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();

    }

    private PoseStack setupPoseStack(final RenderLevelStageEvent event) {
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.disablePolygonOffset();
        RenderSystem.defaultBlendFunc();

        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        Vec3 view = this.mc.cameraEntity.getEyePosition(event.getPartialTick());
        stack.translate(-view.x, -view.y, -view.z);
        RenderSystem.applyModelViewMatrix();
        return stack;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onFovCompute(final ComputeFovModifierEvent event) {
        var data = event.getPlayer().getData(AllomancerAttachment.ALLOMANCY_DATA);
        // tin and duralumin give a zoom effect
        if (data.isBurning(Metal.TIN) && data.isEnhanced()) {
            event.setNewFovModifier(0.2F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSound(final PlaySoundEvent event) {

        Player player = this.mc.player;
        SoundInstance sound = event.getSound();
        if ((player == null) || (sound == null) || !player.isAlive()) {
            return;
        }
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

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
    }

    /**
     * Used to enable movement while the MetalSelectScreen is open
     */
    @SubscribeEvent
    public void updateInputEvent(final MovementInputUpdateEvent event) {
        if (this.mc.screen instanceof MetalSelectScreen) {
            Options options = this.mc.options;
            Input eInput = event.getInput();
            float f = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(this.mc.player), 0.0F, 1.0F);
            // from KeyboardInput#tick
            eInput.up = InputConstants.isKeyDown(this.mc.getWindow().getWindow(), options.keyUp.getKey().getValue());
            eInput.down = InputConstants.isKeyDown(this.mc.getWindow().getWindow(), options.keyDown.getKey().getValue());
            eInput.left = InputConstants.isKeyDown(this.mc.getWindow().getWindow(), options.keyLeft.getKey().getValue());
            eInput.right = InputConstants.isKeyDown(this.mc.getWindow().getWindow(), options.keyRight.getKey().getValue());
            eInput.forwardImpulse = eInput.up == eInput.down ? 0.0f : (eInput.up ? 1.0f : -1.0f);
            eInput.leftImpulse = eInput.left == eInput.right ? 0.0f : (eInput.left ? 1.0f : -1.0f);
            eInput.jumping = InputConstants.isKeyDown(this.mc.getWindow().getWindow(), options.keyJump.getKey().getValue());
            eInput.shiftKeyDown = InputConstants.isKeyDown(this.mc.getWindow().getWindow(), options.keyShift.getKey().getValue());
            if (this.mc.player.isMovingSlowly()) {
                eInput.leftImpulse *= f;
                eInput.forwardImpulse *= f;
            }

            // from LocalPlayer#aiStep
            if (!this.mc.player.isSprinting() &&
                (!(this.mc.player.isInWater() || this.mc.player.isInFluidType((fluidType, height) -> this.mc.player.canSwimInFluidType(fluidType))) ||
                 (this.mc.player.isUnderWater() || this.mc.player.canStartSwimming())) && eInput.forwardImpulse >= 0.8 && !this.mc.player.isUsingItem() &&
                (this.mc.player.getFoodData().getFoodLevel() > 6.0F || this.mc.player.getAbilities().mayfly) && !this.mc.player.hasEffect(MobEffects.BLINDNESS) &&
                InputConstants.isKeyDown(this.mc.getWindow().getWindow(), options.keySprint.getKey().getValue())) {
                this.mc.player.setSprinting(true);
            }
        }
    }

}
