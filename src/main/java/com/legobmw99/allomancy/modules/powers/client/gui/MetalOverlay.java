package com.legobmw99.allomancy.modules.powers.client.gui;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.awt.*;

public class MetalOverlay {
    private static final Point[] Frames = new Point[4];
    private static final ResourceLocation meterLoc = new ResourceLocation("allomancy", "textures/gui/overlay/meter.png");
    private static int animationCounter = 0;
    private static int currentFrame = 0;

    static {
        int x = 0;
        int firsty = 22;
        for (int i = 0; i < 4; i++) {
            Frames[i] = new Point(x, firsty + (4 * i));
        }
    }

    /**
     * Draws the overlay for the metals
     *
     * @param matrix
     */
    public static void drawMetalOverlay(PoseStack matrix) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Window res = mc.getWindow();

        if (!player.isAlive()) {
            return;
        }
        int renderX, renderY;

        // Set the offsets of the overlay based on config
        switch (PowersConfig.overlay_position.get()) {
            case TOP_RIGHT -> {
                renderX = res.getGuiScaledWidth() - 145;
                renderY = 10;
            }
            case BOTTOM_RIGHT -> {
                renderX = res.getGuiScaledWidth() - 145;
                renderY = res.getGuiScaledHeight() - 50;
            }
            case BOTTOM_LEFT -> {
                renderX = 5;
                renderY = res.getGuiScaledHeight() - 50;
            }
            default -> { // TOP_LEFT
                renderX = 5;
                renderY = 10;
            }
        }

        ForgeIngameGui gui = new ForgeIngameGui(mc);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, meterLoc);

        player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {

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
                    blit(matrix, gui, renderX + 1 + (7 * i) + offset, renderY + 5 + metalY, 7 + (6 * i), 1 + metalY, 3, 10 - metalY);
                    // Draw the gauges second, so that highlights and decorations show over the bar.
                    blit(matrix, gui, renderX + (7 * i) + offset, renderY, 0, 0, 5, 20);
                    // Draw the fire if it is burning
                    if (data.isBurning(mt)) {
                        blit(matrix, gui, renderX + (7 * i) + offset, renderY + 4 + metalY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
                    }
                }

            }
        });

        // Update the animation counters
        animationCounter++;
        if (animationCounter > 6) {
            animationCounter = 0;
            currentFrame++;
            if (currentFrame > 3) {
                currentFrame = 0;
            }
        }
    }

    private static void blit(PoseStack matrix, ForgeIngameGui gui, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight) {
        ForgeIngameGui.blit(matrix, x, y, gui.getBlitOffset(), uOffset, vOffset, uWidth, vHeight, 128, 128);
    }
}
