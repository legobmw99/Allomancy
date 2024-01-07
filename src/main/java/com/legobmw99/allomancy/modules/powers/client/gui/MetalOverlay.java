package com.legobmw99.allomancy.modules.powers.client.gui;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

import java.awt.*;

public class MetalOverlay implements IGuiOverlay {

    private static final Point[] Frames = new Point[4];
    private static final ResourceLocation meterLoc = new ResourceLocation("allomancy", "textures/gui/overlay/meter.png");
    private static int currentFrame = 0;

    static {
        int x = 0;
        int firsty = 22;
        for (int i = 0; i < 4; i++) {
            Frames[i] = new Point(x, firsty + (4 * i));
        }
    }

    private MetalOverlay() {}

    public static void registerGUI(final RegisterGuiOverlaysEvent evt) {
        evt.registerAboveAll(new ResourceLocation(Allomancy.MODID, "metal_display"), new MetalOverlay());
    }

    private static void blit(GuiGraphics graphics, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight) {
        graphics.blit(meterLoc, x, y, 0, uOffset, vOffset, uWidth, vHeight, 128, 128);
    }

    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (mc.options.hideGui || !mc.isWindowActive() || !player.isAlive()) {
            return;
        }
        if (mc.screen != null && !(mc.screen instanceof ChatScreen) && !(mc.screen instanceof MetalSelectScreen)) {
            return;
        }
        if (!PowersConfig.enable_overlay.get() && !(mc.screen instanceof MetalSelectScreen)) {
            return;
        }


        int renderX, renderY;

        // Set the offsets of the overlay based on config
        switch (PowersConfig.overlay_position.get()) {
            case TOP_RIGHT -> {
                renderX = screenWidth - 145;
                renderY = 10;
            }
            case BOTTOM_RIGHT -> {
                renderX = screenWidth - 145;
                renderY = screenHeight - 50;
            }
            case BOTTOM_LEFT -> {
                renderX = 5;
                renderY = screenHeight - 50;
            }
            default -> { // TOP_LEFT
                renderX = 5;
                renderY = 10;
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, meterLoc);

        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);


        if (data.isUninvested()) {
            return;
        }

        /*
         * The rendering for the overlay
         */
        for (Metal mt : Metal.values()) {
            if (data.hasPower(mt)) {
                int metalY = 9 - data.getAmount(mt);
                int i = mt.getIndex();
                int offset = (i / 2) * 4; // Adding a gap between pairs
                // Draw the bars first
                blit(guiGraphics, renderX + 1 + (7 * i) + offset, renderY + 5 + metalY, 7 + (6 * i), 1 + metalY, 3, 10 - metalY);
                // Draw the gauges second, so that highlights and decorations show over the bar.
                blit(guiGraphics, renderX + (7 * i) + offset, renderY, 0, 0, 5, 20);
                // Draw the fire if it is burning
                if (data.isBurning(mt)) {
                    int frameCount = (currentFrame + i) % 4;
                    var frame = Frames[frameCount];
                    blit(guiGraphics, renderX + (7 * i) + offset, renderY + 4 + metalY, frame.x, frame.y, 5, 3);
                }
            }

        }

        // Update the animation counters

        if (gui.getGuiTicks() % 3 == 0) {
            currentFrame++;
            if (currentFrame > 3) {
                currentFrame = 0;
            }
        }
    }

}
