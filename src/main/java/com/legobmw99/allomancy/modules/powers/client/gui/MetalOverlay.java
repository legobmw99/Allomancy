package com.legobmw99.allomancy.modules.powers.client.gui;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.awt.*;

public final class MetalOverlay implements LayeredDraw.Layer {

    private static final Point[] Frames = new Point[4];
    private static final ResourceLocation meterLoc =
            ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, "textures/gui/overlay/meter.png");
    private int currentFrame = 0;

    static {
        int x = 0;
        int firsty = 22;
        for (int i = 0; i < 4; i++) {
            Frames[i] = new Point(x, firsty + (4 * i));
        }
    }

    private MetalOverlay() {}

    public static void registerGUI(final RegisterGuiLayersEvent evt) {
        evt.registerBelowAll(ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, "metal_display"),
                             new MetalOverlay());
    }

    private static void blit(GuiGraphics graphics,
                             int x,
                             int y,
                             float uOffset,
                             float vOffset,
                             int uWidth,
                             int vHeight) {
        graphics.blit(meterLoc, x, y, 0, uOffset, vOffset, uWidth, vHeight, 128, 128);
    }

    @Override
    public void render(GuiGraphics gui, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (mc.options.hideGui || !player.isAlive()) {
            return;
        }

        if (!PowersConfig.enable_overlay.get() && !(mc.screen instanceof MetalSelectScreen)) {
            return;
        }

        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        if (data.isUninvested()) {
            return;
        }


        int renderX = PowersConfig.overlay_position.get().getX(gui.guiWidth());
        int renderY = PowersConfig.overlay_position.get().getY(gui.guiHeight());

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, meterLoc);


        /*
         * The rendering for the overlay
         */
        for (Metal mt : Metal.values()) {
            if (data.hasPower(mt)) {
                int metalY = 9 - data.getStored(mt);
                int i = mt.getIndex();
                int offset = (i / 2) * 4; // Adding a gap between pairs
                // Draw the bars first
                blit(gui, renderX + 1 + (7 * i) + offset, renderY + 5 + metalY, 7 + (6 * i), 1 + metalY, 3,
                     10 - metalY);
                // Draw the gauges second, so that highlights and decorations show over the bar.
                blit(gui, renderX + (7 * i) + offset, renderY, 0, 0, 5, 20);
                // Draw the fire if it is burning
                if (data.isBurning(mt)) {
                    int frameCount = (currentFrame + i) % 4;
                    var frame = Frames[frameCount];
                    blit(gui, renderX + (7 * i) + offset, renderY + 4 + metalY, frame.x, frame.y, 5, 3);
                }
            }

        }

        // Update the animation counters
        if (mc.gui.getGuiTicks() % 6 == 0) {
            currentFrame++;
            if (currentFrame > 3) {
                currentFrame = 0;
            }
        }
    }


    public enum SCREEN_LOC {
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT;

        private int getX(int screenWidth) {
            return switch (this) {
                case TOP_RIGHT, BOTTOM_RIGHT -> screenWidth - 145;
                default -> 5;
            };
        }

        private int getY(int screenHeight) {
            return switch (this) {
                case BOTTOM_RIGHT, BOTTOM_LEFT -> screenHeight - 50;
                default -> 10;
            };
        }
    }
}
