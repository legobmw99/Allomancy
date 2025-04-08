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

    // TODO: a custom shader that includes linewidth as a in rather than a uniform?
    private static final List<Rendering.Line> narrowLines = new ArrayList<>(); // 1.5 wide
    private static final List<Rendering.Line> mediumLines = new ArrayList<>(); // 3 wide
    private static final List<Rendering.Line> thickLines = new ArrayList<>(); // 5 wide

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
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
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

        narrowLines.clear();
        mediumLines.clear();
        thickLines.clear();

        /*********************************************
         * IRON AND STEEL LINES                      *
         *********************************************/
        if ((data.isBurning(Metal.IRON) || data.isBurning(Metal.STEEL))) {
            tracking.forEachMetallicEntity(
                    entity -> narrowLines.add(new Rendering.Line(entity.position(), 0.0F, 0.6F, 1.0F)));

            tracking.forEachMetalBlob(blob -> {
                Rendering.Line line = new Rendering.Line(blob.getCenter(), 0.0F, 0.6F, 1.0F);
                float perfectWidth = 0.3F + blob.size() * 0.4F;
                if (perfectWidth < 2.25f) {
                    narrowLines.add(line);
                } else if (perfectWidth < 4) {
                    mediumLines.add(line);
                } else {
                    thickLines.add(line);
                }
            });
        }

        /*********************************************
         * BRONZE LINES                              *
         *********************************************/
        GlobalPos seeking = data.getSpecialSeekingLoc();
        if (seeking != null && player.level().dimension() == seeking.dimension()) {
            thickLines.add(new Rendering.Line(seeking.pos().getCenter(), 0.7F, 0.15F, 0.15F));
        }
        if ((data.isBurning(Metal.BRONZE) && (data.isEnhanced() || !data.isBurning(Metal.COPPER)))) {
            tracking.forEachSeeked(
                    playerEntity -> thickLines.add(new Rendering.Line(playerEntity.position(), 0.7F, 0.15F, 0.15F)));
        }

        /*********************************************
         * GOLD AND ELECTRUM LINES                   *
         *********************************************/
        if (data.isBurning(Metal.GOLD)) {
            player.getLastDeathLocation().ifPresent(death -> {
                if (player.level().dimension() == death.dimension()) {
                    mediumLines.add(new Rendering.Line(Vec3.atCenterOf(death.pos()), 0.9F, 0.85F, 0.0F));
                }
            });
        }
        if (data.isBurning(Metal.ELECTRUM)) {
            GlobalPos spawn = data.getSpawnLoc();
            if (spawn == null &&
                player.level().dimension() == Level.OVERWORLD) { // overworld, no spawn --> use world spawn
                var levelData = player.level().getLevelData();
                mediumLines.add(new Rendering.Line(Vec3.atCenterOf(levelData.getSpawnPos()), 0.7F, 0.8F, 0.2F));
            } else if (spawn != null && player.level().dimension() == spawn.dimension()) {
                mediumLines.add(new Rendering.Line(Vec3.atCenterOf(spawn.pos()), 0.7F, 0.8F, 0.2F));
            }
        }

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        double rho = 1;
        float theta = (float) ((player.getViewYRot(partialTick) + 90) * Math.PI / 180);
        float phi = Mth.clamp((float) ((player.getViewXRot(partialTick) + 90) * Math.PI / 180), 0.0001F, 3.14F);

        Vec3 playervec = mc.player
                .getPosition(partialTick)
                .add(rho * Mth.sin(phi) * Mth.cos(theta), rho * Mth.cos(phi) - 0.35F,
                     rho * Mth.sin(phi) * Mth.sin(theta));

        Rendering.drawMetalLines(event.getPoseStack(), playervec, narrowLines, 1.5f);
        Rendering.drawMetalLines(event.getPoseStack(), playervec, mediumLines, 3.0f);
        Rendering.drawMetalLines(event.getPoseStack(), playervec, thickLines, 5.0f);
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
            storage.addToTooltip(event.getContext(), components::add, event.getFlags(),
                                 event.getItemStack().getComponents());
            int len = event.getToolTip().size();
            int insertLoc = event.getFlags().isAdvanced() ? len - 2 : len;
            event.getToolTip().addAll(insertLoc, components);
        }
    }
}

