package com.legobmw99.allomancy.modules.powers.client.gui;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;

import java.awt.*;

public final class MetalOverlay implements GuiLayer {

    private static final Identifier METER_TEXTURE = Allomancy.id("textures/gui/overlay/meter.png");
    private static final int OUTLINE_COLOR = ARGB.color(0x55 + 0x10, 0x55 + 0x10, 0x55 + 0x10);
    private static final Point[] BURNING_FRAMES = new Point[4];

    static {
        int x = 0;
        int firsty = 22;
        for (int i = 0; i < 4; i++) {
            BURNING_FRAMES[i] = new Point(x, firsty + (4 * i));
        }
    }


    private int currentFrame = 0;

    private MetalOverlay() {}

    public static void registerGUI(final RegisterGuiLayersEvent evt) {
        evt.registerAboveAll(Allomancy.id("metal_display"), new MetalOverlay());
    }

    private static void blit(GuiGraphics graphics,
                             int x,
                             int y,
                             float uOffset,
                             float vOffset,
                             int uWidth,
                             int vHeight) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, METER_TEXTURE, x, y, uOffset, vOffset, uWidth, vHeight, uWidth,
                      vHeight, 128, 128);
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

        var data = AllomancerAttachment.get(player);

        if (data.isUninvested()) {
            return;
        }

        Metal highlight = null;
        if (mc.screen instanceof MetalSelectScreen select) {
            highlight = select.selectedMetal;
        }

        int renderX = PowersConfig.overlay_position.get().getX(gui.guiWidth());
        int renderY = PowersConfig.overlay_position.get().getY(gui.guiHeight());

        /*
         * The rendering for the overlay
         */
        for (Metal mt : Metal.values()) {
            if (data.hasPower(mt)) {
                int metalY = 9 - data.getStored(mt);
                int i = mt.getIndex();
                int offset = (i / 2) * 4; // Adding a gap between pairs

                int xCorner = renderX + (7 * i) + offset;
                // Draw the bars first
                blit(gui, xCorner + 1, renderY + 5 + metalY, 7 + (6 * i), 1 + metalY, 3, 10 - metalY);
                // Draw the gauges second, so that highlights and decorations show over the bar.
                blit(gui, xCorner, renderY, 0, 0, 5, 20);
                // Draw the fire if it is burning
                if (data.isBurning(mt)) {
                    int frameCount = (currentFrame + i) % 4;
                    var frame = BURNING_FRAMES[frameCount];
                    blit(gui, xCorner, renderY + 4 + metalY, frame.x, frame.y, 5, 3);
                }

                if (highlight == mt) {
                    gui.renderOutline(xCorner - 1, renderY - 1, 7, 21, OUTLINE_COLOR);
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
