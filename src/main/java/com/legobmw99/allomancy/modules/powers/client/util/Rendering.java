package com.legobmw99.allomancy.modules.powers.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class Rendering {


    /**
     * Draws a line from the player (denoted pX,Y,Z) to the given set of
     * coordinates (oX,Y,Z) in a certain color (r,g,b)
     *
     * @param player
     * @param dest
     * @param width  the width of the line
     */
    /**Old version
      public static void drawMetalLine(PoseStack stack,

                                     Vec3 player,
                                     Vec3 dest,
                                     float width,
                                     float r,
                                     float g,
                                     float b) {

        //        RenderSystem.lineWidth(width);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();

        builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = stack.last().pose();
        builder
                .vertex(matrix4f, (float) player.x, (float) player.y, (float) player.z)
                .color(r, g, b, 0.6f)
                .endVertex();
        builder.vertex(matrix4f, (float) dest.x, (float) dest.y, (float) dest.z).color(r, g, b, 0.6f).endVertex();
        RenderSystem.lineWidth(width);

        tessellator.end();

    }*/

    /** New version (Without width)
     */
    public static void drawMetalLine(PoseStack stack, Vec3 player, Vec3 dest, float width, float r, float g, float b) {
        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());
        Matrix4f matrix4f = stack.last().pose();
        Matrix3f normalMatrix = stack.last().normal();

        builder.vertex(matrix4f, (float)player.x, (float)player.y, (float)player.z)
                .color(r, g, b, 0.6F)
                .normal(normalMatrix, 0, 1, 0)
                .endVertex();
        builder.vertex(matrix4f, (float)dest.x, (float)dest.y, (float)dest.z)
                .color(r, g, b, 0.6F)
                .normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

    public static void doneDrawingLines(PoseStack stack) {

        Minecraft.getInstance().renderBuffers().bufferSource().endBatch(RenderType.lines());

        //old code (idk if it's still needed but it doesn't seem to break anything)
        stack.popPose();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.disableBlend();
        RenderSystem.enablePolygonOffset();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();

    }

    public static PoseStack prepareToDrawLines(PoseStack stack, float partialTick) {
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.disablePolygonOffset();
        RenderSystem.defaultBlendFunc();

        stack.pushPose();
        Vec3 view = Minecraft.getInstance().cameraEntity.getEyePosition(partialTick);
        stack.translate(-view.x, -view.y, -view.z);
        RenderSystem.applyModelViewMatrix();
        return stack;
    }

}
