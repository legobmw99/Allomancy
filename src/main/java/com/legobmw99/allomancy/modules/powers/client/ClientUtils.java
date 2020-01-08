package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.modules.powers.network.UpdateBurnPacket;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.modules.powers.util.Metal;
import com.legobmw99.allomancy.network.Network;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

@OnlyIn(Dist.CLIENT)
public class ClientUtils {

    private static Minecraft mc = Minecraft.getInstance();
    private static ClientPlayerEntity player = mc.player;

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
    public static void drawMetalLine(Vec3d player, Vec3d dest, float width,
                                     float r, float g, float b) {
        RenderSystem.lineWidth(width);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        // func_225582_a_ = POS
        // func_227885_a_ = COLOR
        buffer.func_225582_a_(player.getX(), player.getY(), player.getZ()).func_227885_a_(r, g, b, 0.8f).endVertex();
        buffer.func_225582_a_(dest.getX(), dest.getY(), dest.getZ()).func_227885_a_(r, g, b, 0.8f).endVertex();
        tessellator.draw();
    }


    /**
     * Used to toggle a metal's burn state and play a sound effect
     *
     * @param metal      the index of the metal to toggle
     * @param capability the capability being handled
     */
    public static void toggleBurn(Metal metal, AllomancyCapability capability) {
        if(!capability.hasPower(metal)){
            return;
        }

        Network.sendToServer(new UpdateBurnPacket(metal, !capability.isBurning(metal)));

        if (capability.getAmount(metal) > 0) {
            capability.setBurning(metal, !capability.isBurning(metal));
        }
        // play a sound effect
        if (capability.isBurning(metal)) {
            player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1,
                    5);
        } else {
            player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1,
                    4);
        }
    }
}
