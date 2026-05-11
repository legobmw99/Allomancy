package com.legobmw99.allomancy.modules.powers.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public class Rendering {


    public record Color(float r, float g, float b) {
    }

    public record Line(Vec3 dest, Color color) {
    }

    public static void drawMetalLines(PoseStack stack, Vec3 player, List<Line> lines, float width) {

        GlStateManager._depthMask(false);
        GlStateManager._disableCull();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

//        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());

        Matrix4f matrix4f = stack.last().pose();
        Matrix3f normalMatrix = stack.last().normal();
        RenderSystem.lineWidth(width);

        for (var line : lines) {
            builder
                    .vertex(matrix4f, (float) player.x, (float) player.y, (float) player.z)
                    .color(line.color.r, line.color.g, line.color.b, 0.6F)
                    .normal(normalMatrix, 0, 1, 0)
                    .endVertex();
            builder
                    .vertex(matrix4f, (float) line.dest.x, (float) line.dest.y, (float) line.dest.z)
                    .color(line.color.r, line.color.g, line.color.b, 0.6F)
                    .normal(normalMatrix, 0, 1, 0)
                    .endVertex();
        }
        tesselator.end();
        RenderSystem.lineWidth(1.0F);
        GlStateManager._enableCull();
        GlStateManager._depthMask(true);

        Minecraft.getInstance().renderBuffers().bufferSource().endBatch(RenderType.lines());
    }

    public static void doneDrawingLines(PoseStack stack) {
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
