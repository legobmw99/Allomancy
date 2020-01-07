package com.legobmw99.allomancy.modules.powers.client.gui;

import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MetalOverlay {
    private static final Point[] Frames = {new Point(55, 0), new Point(55, 4), new Point(55, 8), new Point(55, 12)};
    private static final ResourceLocation meterLoc = new ResourceLocation("allomancy", "textures/gui/overlay/meter.png");
    private static int animationCounter = 0;
    private static int currentFrame = 0;

    /**
     * Draws the overlay for the metals
     */
    public static void drawMetalOverlay() {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        MainWindow res = mc.func_228018_at_(); //getMainWindow

        if (!player.isAlive()) {
            return;
        }

        AllomancyCapability cap = AllomancyCapability.forPlayer(player);

        if (cap.getAllomancyPower() < 0) {
            return;
        }

        int renderX, renderY = 0;

        // Set the offsets of the overlay based on config
        switch (PowersConfig.overlay_position.get()) {
            case TOP_RIGHT:
                renderX = res.getScaledWidth() - 95;
                renderY = 10;
                break;
            case BOTTOM_RIGHT:
                renderX = res.getScaledWidth() - 95;
                renderY = res.getScaledHeight() - 40;
                break;
            case BOTTOM_LEFT:
                renderX = 5;
                renderY = res.getScaledHeight() - 40;
                break;
            default: // TOP_LEFT
                renderX = 5;
                renderY = 10;
                break;
        }

        ForgeIngameGui gui = new ForgeIngameGui(mc);
        mc.getTextureManager().bindTexture(meterLoc);
        Texture obj;
        obj = mc.getTextureManager().func_229267_b_(meterLoc); //getTexture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

        /*
         * Misting overlay
         */
        if (cap.getAllomancyPower() >= 0 && cap.getAllomancyPower() < 8) {
            int singleMetalY = 9 - cap.getMetalAmounts(cap.getAllomancyPower());
            blit(renderX + 1, renderY + 5 + singleMetalY, 7 + 6 * cap.getAllomancyPower(), 1 + singleMetalY, 3, 10 - singleMetalY);
            blit(renderX, renderY, 0, 0, 5, 20);
            if (cap.getMetalBurning(cap.getAllomancyPower())) {
                blit(renderX, renderY + 4 + singleMetalY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
        }

        /*
         * The rendering for a the overlay of a full Mistborn
         */
        if (cap.getAllomancyPower() == 8) {
            for (int i = 0; i < 8; i++) {
                int metalY = 9 - cap.getMetalAmounts(i);
                int offset = (i / 2) * 11; // Adding a gap between pairs
                // Draw the bars first
                blit(renderX + 1 + (7 * i) + offset, renderY + 5 + metalY, 7 + (6 * i), 1 + metalY, 3, 10 - metalY);
                // Draw the gauges second, so that highlights and decorations show over the bar.
                blit(renderX + (7 * i) + offset, renderY, 0, 0, 5, 20);
                // Draw the fire if it is burning
                if (cap.getMetalBurning(i)) {
                    blit(renderX  + (7 * i) + offset, renderY + 4 + metalY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
                }
            }
        }

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

    private static void blit(int x, int y, int p_blit_3, int p_blit_4, int p_blit_5, int p_blit_6) {
        ForgeIngameGui gui = new ForgeIngameGui(Minecraft.getInstance());
        ForgeIngameGui.blit(x, y, gui.getBlitOffset(), p_blit_3, p_blit_4, p_blit_5, p_blit_6, 64, 64);
    }
}
