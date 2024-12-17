package com.legobmw99.allomancy.modules.powers.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class Rendering {
    private Rendering() {}

    /**
     * Draws a line from the player (denoted pX,Y,Z) to the given set of
     * coordinates (oX,Y,Z) in a certain color (r,g,b)
     *
     * @param player location of the player
     * @param dest   location to draw toward
     * @param width  the width of the line
     */
    public static void drawMetalLine(PoseStack stack,
                                     Vec3 player,
                                     Vec3 dest,
                                     float width,
                                     float r,
                                     float g,
                                     float b) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f matrix4f = stack.last().pose();
        builder.addVertex(matrix4f, (float) player.x, (float) player.y, (float) player.z).setColor(r, g, b, 0.6f);
        builder.addVertex(matrix4f, (float) dest.x, (float) dest.y, (float) dest.z).setColor(r, g, b, 0.6f);
        RenderSystem.lineWidth(width);

        BufferUploader.drawWithShader(builder.buildOrThrow());

    }

    public static void doneDrawingLines(PoseStack stack) {
        stack.popPose();

        RenderSystem.disableBlend();
        RenderSystem.enablePolygonOffset();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();

    }

    public static PoseStack prepareToDrawLines(PoseStack start) {
        RenderSystem.setShader(CoreShaders.RENDERTYPE_LINES);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.disablePolygonOffset();
        RenderSystem.defaultBlendFunc();

        start.pushPose();
        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        start.translate(-view.x, -view.y, -view.z);
        return start;
    }
}
