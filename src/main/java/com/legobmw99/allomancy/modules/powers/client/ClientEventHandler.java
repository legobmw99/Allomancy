package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalSelectScreen;
import com.legobmw99.allomancy.modules.powers.client.network.PowerRequests;
import com.legobmw99.allomancy.modules.powers.client.util.Inputs;
import com.legobmw99.allomancy.modules.powers.client.util.Rendering;
import com.legobmw99.allomancy.modules.powers.client.util.Sounds;
import com.legobmw99.allomancy.modules.powers.client.util.Tracking;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static com.legobmw99.allomancy.modules.consumables.ConsumeSetup.FLAKE_STORAGE;

public final class ClientEventHandler {
    private static final Tracking tracking = new Tracking();

    private ClientEventHandler() {}

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        // Run once per tick, only if in game, and only if there is a player
        if (mc.isPaused() || mc.player == null || !mc.player.isAlive()) {
            return;
        }

        Player player = mc.player;
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        if (data.isUninvested()) {
            return;
        }
        // Duralumin makes you move much quicker and reach much further
        int dist_modifier = data.isEnhanced() ? 2 : 1;

        // Handle our input-based powers
        if (mc.options.keyAttack.isDown()) {
            // Ray trace 20 blocks (or 40 if enhanced)
            var trace = Inputs.getMouseOverExtended(20.0F * dist_modifier);
            PowerRequests.metallicPushPull(data, trace, Metal.IRON);
            PowerRequests.emotionPushPull(data, trace, Metal.ZINC);
        }

        if (mc.options.keyUse.isDown()) {
            // Ray trace 20 blocks (or 40 if enhanced)
            var trace = Inputs.getMouseOverExtended(20.0F * dist_modifier);
            PowerRequests.metallicPushPull(data, trace, Metal.STEEL);
            PowerRequests.emotionPushPull(data, trace, Metal.BRASS);
            PowerRequests.nicrosilEnhance(data, trace);
        }

        tracking.tick();
    }


    @SubscribeEvent
    public static void onKeyInput(final InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            Inputs.acceptAllomancyKeybinds();
        }
    }

    @SubscribeEvent
    public static void onMouseInput(final InputEvent.MouseButton.Pre event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            Inputs.acceptAllomancyKeybinds();
        }
    }


    @SubscribeEvent
    public static void onRenderLevelStage(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.isAlive()) {
            return;
        }
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);


        if (data.isUninvested()) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        PoseStack stack = Rendering.prepareToDrawLines(event.getPoseStack());

        double rho = 1;
        float theta = (float) ((player.getViewYRot(partialTick) + 90) * Math.PI / 180);
        float phi = Mth.clamp((float) ((player.getViewXRot(partialTick) + 90) * Math.PI / 180), 0.0001F, 3.14F);

        Vec3 playervec = mc.player
                .getPosition(partialTick)
                .add(rho * Mth.sin(phi) * Mth.cos(theta), rho * Mth.cos(phi) - 0.35F,
                     rho * Mth.sin(phi) * Mth.sin(theta));

        /*********************************************
         * IRON AND STEEL LINES                      *
         *********************************************/


        if ((data.isBurning(Metal.IRON) || data.isBurning(Metal.STEEL))) {
            tracking.forEachMetallicEntity(
                    entity -> Rendering.drawMetalLine(stack, playervec, entity.position(), 1.5F, 0.0F, 0.6F, 1.0F));

            tracking.forEachMetalBlob(blob -> Rendering.drawMetalLine(stack, playervec, blob.getCenter(),
                                                                      Mth.clamp(0.3F + blob.size() * 0.4F, 0.5F,
                                                                                7.5F), 0.0F, 0.6F, 1.0F));
        }

        /*********************************************
         * BRONZE LINES                              *
         *********************************************/
        GlobalPos seeking = data.getSpecialSeekingLoc();
        if (seeking != null && player.level().dimension() == seeking.dimension()) {

            Rendering.drawMetalLine(stack, playervec, seeking.pos().getCenter(), 5.0F, 0.7F, 0.15F, 0.15F);
        }
        if ((data.isBurning(Metal.BRONZE) && (data.isEnhanced() || !data.isBurning(Metal.COPPER)))) {
            tracking.forEachSeeked(
                    playerEntity -> Rendering.drawMetalLine(stack, playervec, playerEntity.position(), 5.0F, 0.7F,
                                                            0.15F, 0.15F));
        }

        /*********************************************
         * GOLD AND ELECTRUM LINES                   *
         *********************************************/
        if (data.isBurning(Metal.GOLD)) {
            player.getLastDeathLocation().ifPresent(death -> {
                if (player.level().dimension() == death.dimension()) {
                    Rendering.drawMetalLine(stack, playervec, Vec3.atCenterOf(death.pos()), 3.0F, 0.9F, 0.85F, 0.0F);
                }
            });
        }
        if (data.isBurning(Metal.ELECTRUM)) {
            GlobalPos spawn = data.getSpawnLoc();
            if (spawn == null &&
                player.level().dimension() == Level.OVERWORLD) { // overworld, no spawn --> use world spawn
                var levelData = player.level().getLevelData();
                Rendering.drawMetalLine(stack, playervec, Vec3.atCenterOf(levelData.getSpawnPos()), 3.0F, 0.7F, 0.8F,
                                        0.2F);
            } else if (spawn != null && player.level().dimension() == spawn.dimension()) {
                Rendering.drawMetalLine(stack, playervec, Vec3.atCenterOf(spawn.pos()), 3.0F, 0.7F, 0.8F, 0.2F);
            }
        }

        Rendering.doneDrawingLines(stack);

    }

    @SubscribeEvent
    public static void onFovCompute(final ComputeFovModifierEvent event) {
        var data = event.getPlayer().getData(AllomancerAttachment.ALLOMANCY_DATA);
        // tin and duralumin give a zoom effect
        if (data.isBurning(Metal.TIN) && data.isEnhanced()) {
            event.setNewFovModifier(0.2F);
        }
    }

    @SubscribeEvent
    public static void onSound(final PlaySoundEvent event) {

        Player player = Minecraft.getInstance().player;
        SoundInstance sound = event.getSound();
        if ((player == null) || (sound == null) || !player.isAlive()) {
            return;
        }
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        if (data.isBurning(Metal.TIN)) {
            Sounds.spawnParticleForSound(player, sound);
        }
    }

    /**
     * Used to enable movement while the MetalSelectScreen is open
     */
    @SubscribeEvent
    public static void updateInputEvent(final MovementInputUpdateEvent event) {
        if (Minecraft.getInstance().screen instanceof MetalSelectScreen) {
            Inputs.fakeMovement(event.getInput());
        }
    }

    @SubscribeEvent
    public static void onTooltip(final ItemTooltipEvent event) {
        FlakeStorage storage = event.getItemStack().get(FLAKE_STORAGE);
        if (storage != null) {
            List<Component> components = new ArrayList<>(16);
            storage.addToTooltip(event.getContext(), components::add, event.getFlags());
            int len = event.getToolTip().size();
            int insertLoc = event.getFlags().isAdvanced() ? len - 2 : len;
            event.getToolTip().addAll(insertLoc, components);
        }
    }
}

