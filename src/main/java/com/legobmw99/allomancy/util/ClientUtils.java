package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.network.NetworkHelper;
import com.legobmw99.allomancy.network.packets.UpdateBurnPacket;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ClientUtils {

    private static final Point[] Frames = {new Point(72, 0), new Point(72, 4), new Point(72, 8), new Point(72, 12)};
    private static final ResourceLocation meterLoc = new ResourceLocation("allomancy", "textures/gui/overlay/meter.png");
    private static int animationCounter = 0;
    private static int currentFrame = 0;
    private static Minecraft mc = Minecraft.getInstance();
    private static ClientPlayerEntity player = mc.player;
    private static AllomancyCapability cap;

    /**
     * Adapted from vanilla, allows getting mouseover at given distances
     *
     * @param dist the distance requested
     * @return a RayTraceResult for the requested raytrace
     */
    @Nullable
    public static RayTraceResult getMouseOverExtended(float dist) {
        mc = Minecraft.getInstance();
        float partialTicks = mc.getRenderPartialTicks();
        RayTraceResult objectMouseOver = null;
        Entity pointedEntity;
        Entity entity = mc.getRenderViewEntity();
        if (entity != null) {
            if (mc.world != null) {
                objectMouseOver = entity.pick(dist, partialTicks, false);
                Vec3d vec3d = entity.getEyePosition(partialTicks);
                boolean flag = false;
                int i = 3;
                double d1 = dist * dist;

                if (objectMouseOver != null) {
                    d1 = objectMouseOver.getHitVec().squareDistanceTo(vec3d);
                }

                Vec3d vec3d1 = entity.getLook(1.0F);
                Vec3d vec3d2 = vec3d.add(vec3d1.x * dist, vec3d1.y * dist, vec3d1.z * dist);
                float f = 1.0F;
                AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.scale(dist)).grow(1.0D, 1.0D, 1.0D);
                EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vec3d, vec3d2, axisalignedbb, (e) -> {
                    return true;
                }, d1);
                if (entityraytraceresult != null) {
                    Entity entity1 = entityraytraceresult.getEntity();
                    Vec3d vec3d3 = entityraytraceresult.getHitVec();
                    double d2 = vec3d.squareDistanceTo(vec3d3);
                    if (d2 < d1) {
                        objectMouseOver = entityraytraceresult;
                    }
                }

            }
        }
        return objectMouseOver;

    }

    /**
     * Draws a line from the player (denoted pX,Y,Z) to the given set of
     * coordinates (oX,Y,Z) in a certain color (r,g,b)
     *
     * @param width the width of the line
     */
    public static void drawMetalLine(double pX, double pY, double pZ, double oX, double oY, double oZ, float width,
                                     float r, float g, float b) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glTranslated(-pX, -pY, -pZ);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glLineWidth(width);
        GL11.glColor3f(r, g, b);

        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex3d(pX, pY - 0.5, pZ);
        GL11.glVertex3d(oX, oY, oZ);

        GL11.glEnd();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }


    /**
     * Draws the overlay for the metals
     */
    public static void drawMetalOverlay() {
        player =  mc.player;
        if(!player.isAlive()){
            return;
        }
        cap = AllomancyCapability.forPlayer(player);

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
        MainWindow res = Minecraft.getInstance().mainWindow;

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

        IngameGui gig = new IngameGui(mc);
        mc.getRenderManager().textureManager.bindTexture(meterLoc);
        ITextureObject obj;
        obj = mc.getRenderManager().textureManager.getTexture(meterLoc);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

        /*
         * Misting overlay
         */
        if (cap.getAllomancyPower() >= 0 && cap.getAllomancyPower() < 8) {

            singleMetalY = 9 - cap.getMetalAmounts(cap.getAllomancyPower());
            gig.blit(renderX + 1, renderY + 5 + singleMetalY, 7 + 6 * cap.getAllomancyPower(), 1 + singleMetalY, 3, 10 - singleMetalY);
            gig.blit(renderX, renderY, 0, 0, 5, 20);
            if (cap.getMetalBurning(cap.getAllomancyPower())) {
                gig.blit(renderX, renderY + 5 + singleMetalY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
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
            gig.blit(renderX + 1, renderY + 5 + ironY, 7, 1 + ironY, 3, 10 - ironY);

            steelY = 9 - cap.getMetalAmounts(AllomancyCapability.STEEL);
            gig.blit(renderX + 8, renderY + 5 + steelY, 13, 1 + steelY, 3, 10 - steelY);

            tinY = 9 - cap.getMetalAmounts(AllomancyCapability.TIN);
            gig.blit(renderX + 26, renderY + 5 + tinY, 19, 1 + tinY, 3, 10 - tinY);

            pewterY = 9 - cap.getMetalAmounts(AllomancyCapability.PEWTER);
            gig.blit(renderX + 33, renderY + 5 + pewterY, 25, 1 + pewterY, 3, 10 - pewterY);

            zincY = 9 - cap.getMetalAmounts(AllomancyCapability.ZINC);
            gig.blit(renderX + 51, renderY + 5 + zincY, 31, 1 + zincY, 3, 10 - zincY);

            brassY = 9 - cap.getMetalAmounts(AllomancyCapability.BRASS);
            gig.blit(renderX + 58, renderY + 5 + brassY, 37, 1 + brassY, 3, 10 - brassY);

            copperY = 9 - cap.getMetalAmounts(AllomancyCapability.COPPER);
            gig.blit(renderX + 76, renderY + 5 + copperY, 43, 1 + copperY, 3, 10 - copperY);

            bronzeY = 9 - cap.getMetalAmounts(AllomancyCapability.BRONZE);
            gig.blit(renderX + 83, renderY + 5 + bronzeY, 49, 1 + bronzeY, 3, 10 - bronzeY);

            // Draw the gauges second, so that highlights and decorations show over
            // the bar.
            gig.blit(renderX, renderY, 0, 0, 5, 20);
            gig.blit(renderX + 7, renderY, 0, 0, 5, 20);

            gig.blit(renderX + 25, renderY, 0, 0, 5, 20);
            gig.blit(renderX + 32, renderY, 0, 0, 5, 20);

            gig.blit(renderX + 50, renderY, 0, 0, 5, 20);
            gig.blit(renderX + 57, renderY, 0, 0, 5, 20);

            gig.blit(renderX + 75, renderY, 0, 0, 5, 20);
            gig.blit(renderX + 82, renderY, 0, 0, 5, 20);

            if (cap.getMetalBurning(AllomancyCapability.IRON)) {
                gig.blit(renderX, renderY + 5 + ironY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.STEEL)) {
                gig.blit(renderX + 7, renderY + 5 + steelY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.TIN)) {
                gig.blit(renderX + 25, renderY + 5 + tinY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                gig.blit(renderX + 32, renderY + 5 + pewterY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.ZINC)) {
                gig.blit(renderX + 50, renderY + 5 + zincY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.BRASS)) {
                gig.blit(renderX + 57, renderY + 5 + brassY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.COPPER)) {
                gig.blit(renderX + 75, renderY + 5 + copperY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
            }
            if (cap.getMetalBurning(AllomancyCapability.BRONZE)) {
                gig.blit(renderX + 82, renderY + 5 + bronzeY, Frames[currentFrame].x, Frames[currentFrame].y, 5, 3);
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

    /**
     * Used to toggle a metal's burn state and play a sound effect
     *
     * @param metal      the index of the metal to toggle
     * @param capability the capability being handled
     */
    public static void toggleMetalBurn(byte metal, AllomancyCapability capability) {
        NetworkHelper.sendToServer(new UpdateBurnPacket(metal, !capability.getMetalBurning(metal)));

        if (capability.getMetalAmounts(metal) > 0) {
            capability.setMetalBurning(metal, !capability.getMetalBurning(metal));
        }
        // play a sound effect
        if (capability.getMetalBurning(metal)) {
            player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1,
                    5);
        } else {
            player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1,
                    4);
        }
    }
}
