package com.legobmw99.allomancy.client.gui;

import com.legobmw99.allomancy.setup.AllomancyConfig;
import com.legobmw99.allomancy.util.AllomancyCapability;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeIngameGui;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MetalOverlay {
    private static Minecraft mc = Minecraft.getInstance();
    private static ClientPlayerEntity player = mc.player;

    private static final Point[] Frames = {new Point(72, 0), new Point(72, 4), new Point(72, 8), new Point(72, 12)};
    private static final ResourceLocation meterLoc = new ResourceLocation("allomancy", "textures/gui/overlay/meter.png");
    private static int animationCounter = 0;
    private static int currentFrame = 0;

    /**
     * Draws the overlay for the metals
     */
    public static void drawMetalOverlay() {
        if (!player.isAlive()) {
            return;
        }
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);

        if (cap.getAllomancyPower() < 0) {
            return;
        }


        animationCounter++;
        // left hand side.
        int ironY, steelY, tinY, pewterY;
        // right hand side
        int copperY, bronzeY, zincY, brassY;
        // single metal
        int singleMetalY;
        int renderX, renderY = 0;
        MainWindow res = mc.mainWindow;

        // Set the offsets of the overlay based on config
        switch (AllomancyConfig.overlay_position) {
            case TOP_LEFT:
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
            default: //TOP_RIGHT
                renderX = 5;
                renderY = 10;
                break;
        }

        ForgeIngameGui gui = new ForgeIngameGui(mc);
        mc.getRenderManager().textureManager.bindTexture(meterLoc);
        ITextureObject obj;
        obj = mc.getRenderManager().textureManager.getTexture(meterLoc);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

        /*
         * Misting overlay
         */
        if (cap.getAllomancyPower() >= 0 && cap.getAllomancyPower() < 8) {

            singleMetalY = 9 - cap.getMetalAmounts(cap.getAllomancyPower());
            gui.blit(renderX + 1, renderY + 5 + singleMetalY, 7 + 6 * cap.getAllomancyPower(), 1 + singleMetalY, 3, 10 - singleMetalY);
            gui.blit(renderX, renderY, 0, 0, 5, 20);
            if (cap.getMetalBurning(cap.getAllomancyPower())) {
                gui.blit(renderX, renderY + 5 + singleMetalY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (animationCounter > 6) // Draw the burning symbols...
            {
                animationCounter = 0;
                currentFrame++;
                if (currentFrame > 3) {
                    currentFrame = 0;
                }
            }

        }

        /*
         * The rendering for a the overlay of a full Mistborn
         */
        if (cap.getAllomancyPower() == 8) {

            ironY = 9 - cap.getMetalAmounts(AllomancyCapability.IRON);
            gui.blit(renderX + 1, renderY + 5 + ironY, 7, 1 + ironY, 3, 10 - ironY);

            steelY = 9 - cap.getMetalAmounts(AllomancyCapability.STEEL);
            gui.blit(renderX + 8, renderY + 5 + steelY, 13, 1 + steelY, 3, 10 - steelY);

            tinY = 9 - cap.getMetalAmounts(AllomancyCapability.TIN);
            gui.blit(renderX + 26, renderY + 5 + tinY, 19, 1 + tinY, 3, 10 - tinY);

            pewterY = 9 - cap.getMetalAmounts(AllomancyCapability.PEWTER);
            gui.blit(renderX + 33, renderY + 5 + pewterY, 25, 1 + pewterY, 3, 10 - pewterY);

            zincY = 9 - cap.getMetalAmounts(AllomancyCapability.ZINC);
            gui.blit(renderX + 51, renderY + 5 + zincY, 31, 1 + zincY, 3, 10 - zincY);

            brassY = 9 - cap.getMetalAmounts(AllomancyCapability.BRASS);
            gui.blit(renderX + 58, renderY + 5 + brassY, 37, 1 + brassY, 3, 10 - brassY);

            copperY = 9 - cap.getMetalAmounts(AllomancyCapability.COPPER);
            gui.blit(renderX + 76, renderY + 5 + copperY, 43, 1 + copperY, 3, 10 - copperY);

            bronzeY = 9 - cap.getMetalAmounts(AllomancyCapability.BRONZE);
            gui.blit(renderX + 83, renderY + 5 + bronzeY, 49, 1 + bronzeY, 3, 10 - bronzeY);

            // Draw the gauges second, so that highlights and decorations show over
            // the bar.
            gui.blit(renderX, renderY, 0, 0, 5, 20);
            gui.blit(renderX + 7, renderY, 0, 0, 5, 20);

            gui.blit(renderX + 25, renderY, 0, 0, 5, 20);
            gui.blit(renderX + 32, renderY, 0, 0, 5, 20);

            gui.blit(renderX + 50, renderY, 0, 0, 5, 20);
            gui.blit(renderX + 57, renderY, 0, 0, 5, 20);

            gui.blit(renderX + 75, renderY, 0, 0, 5, 20);
            gui.blit(renderX + 82, renderY, 0, 0, 5, 20);

            if (cap.getMetalBurning(AllomancyCapability.IRON)) {
                gui.blit(renderX, renderY + 5 + ironY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.STEEL)) {
                gui.blit(renderX + 7, renderY + 5 + steelY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.TIN)) {
                gui.blit(renderX + 25, renderY + 5 + tinY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                gui.blit(renderX + 32, renderY + 5 + pewterY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.ZINC)) {
                gui.blit(renderX + 50, renderY + 5 + zincY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.BRASS)) {
                gui.blit(renderX + 57, renderY + 5 + brassY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.COPPER)) {
                gui.blit(renderX + 75, renderY + 5 + copperY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.BRONZE)) {
                gui.blit(renderX + 82, renderY + 5 + bronzeY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }

            if (animationCounter > 6) // Draw the burning symbols...
            {
                animationCounter = 0;
                currentFrame++;
                if (currentFrame > 3) {
                    currentFrame = 0;
                }
            }
        }
    }
}
