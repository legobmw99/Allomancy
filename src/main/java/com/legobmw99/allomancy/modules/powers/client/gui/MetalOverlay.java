package com.legobmw99.allomancy.modules.powers.client.gui;

import com.legobmw99.allomancy.Allomancy;
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
    private static final Point[] Frames = {new Point(72, 0), new Point(72, 4), new Point(72, 8), new Point(72, 12)};
    private static final ResourceLocation meterLoc = new ResourceLocation("allomancy", "textures/gui/overlay/meter.png");
    private static int animationCounter = 0;
    private static int currentFrame = 0;

    /**
     * Draws the overlay for the metals
     */
    public static void drawMetalOverlay() {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
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
        MainWindow res = mc.func_228018_at_(); //getMainWindow

        // Set the offsets of the overlay based on config
        switch (PowersConfig.overlay_position.get()) {
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
        mc.getTextureManager().bindTexture(meterLoc);
        Texture obj;
        obj = mc.getTextureManager().func_229267_b_(meterLoc); //getTexture
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

            ironY = 9 - cap.getMetalAmounts(Allomancy.IRON);
            gui.blit(renderX + 1, renderY + 5 + ironY, 7, 1 + ironY, 3, 10 - ironY);

            steelY = 9 - cap.getMetalAmounts(Allomancy.STEEL);
            gui.blit(renderX + 8, renderY + 5 + steelY, 13, 1 + steelY, 3, 10 - steelY);

            tinY = 9 - cap.getMetalAmounts(Allomancy.TIN);
            gui.blit(renderX + 26, renderY + 5 + tinY, 19, 1 + tinY, 3, 10 - tinY);

            pewterY = 9 - cap.getMetalAmounts(Allomancy.PEWTER);
            gui.blit(renderX + 33, renderY + 5 + pewterY, 25, 1 + pewterY, 3, 10 - pewterY);

            zincY = 9 - cap.getMetalAmounts(Allomancy.ZINC);
            gui.blit(renderX + 51, renderY + 5 + zincY, 31, 1 + zincY, 3, 10 - zincY);

            brassY = 9 - cap.getMetalAmounts(Allomancy.BRASS);
            gui.blit(renderX + 58, renderY + 5 + brassY, 37, 1 + brassY, 3, 10 - brassY);

            copperY = 9 - cap.getMetalAmounts(Allomancy.COPPER);
            gui.blit(renderX + 76, renderY + 5 + copperY, 43, 1 + copperY, 3, 10 - copperY);

            bronzeY = 9 - cap.getMetalAmounts(Allomancy.BRONZE);
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

            if (cap.getMetalBurning(Allomancy.IRON)) {
                gui.blit(renderX, renderY + 5 + ironY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(Allomancy.STEEL)) {
                gui.blit(renderX + 7, renderY + 5 + steelY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(Allomancy.TIN)) {
                gui.blit(renderX + 25, renderY + 5 + tinY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(Allomancy.PEWTER)) {
                gui.blit(renderX + 32, renderY + 5 + pewterY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(Allomancy.ZINC)) {
                gui.blit(renderX + 50, renderY + 5 + zincY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(Allomancy.BRASS)) {
                gui.blit(renderX + 57, renderY + 5 + brassY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(Allomancy.COPPER)) {
                gui.blit(renderX + 75, renderY + 5 + copperY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(Allomancy.BRONZE)) {
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
