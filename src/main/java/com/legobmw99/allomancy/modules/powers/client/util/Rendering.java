package com.legobmw99.allomancy.modules.powers.client.util;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.joml.Matrix4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public final class Rendering {
    private Rendering() {}


    private static final RenderSystem.AutoStorageIndexBuffer indices =
            RenderSystem.getSequentialBuffer(VertexFormat.Mode.LINES);
    private static final RenderPipeline METAL_LINES = RenderPipeline
            .builder(RenderPipelines.LINES_SNIPPET)
            .withLocation("pipeline/allomancy_lines")
            .withVertexShader("core/rendertype_lines")
            .withFragmentShader("core/rendertype_lines")
            .withCull(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES)
            .build();


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
        BufferBuilder builder = tessellator.begin(METAL_LINES.getVertexFormatMode(), METAL_LINES.getVertexFormat());

        Matrix4f matrix4f = stack.last().pose();
        builder.addVertex(matrix4f, (float) player.x, (float) player.y, (float) player.z).setColor(r, g, b, 0.6f);
        builder.addVertex(matrix4f, (float) dest.x, (float) dest.y, (float) dest.z).setColor(r, g, b, 0.6f);
        RenderSystem.lineWidth(width);

        try (MeshData meshData = builder.buildOrThrow()) {
            GpuBuffer vertexBuffer = RenderSystem
                    .getDevice()
                    .createBuffer(() -> "Allomancy lines buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE,
                                  meshData.vertexBuffer());

            RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();
            if (renderTarget.getColorTexture() == null) {
                return;
            }
            int indexCount = meshData.drawState().indexCount();
            GpuBuffer gpuBuffer = indices.getBuffer(indexCount);
            try (RenderPass renderPass = RenderSystem
                    .getDevice()
                    .createCommandEncoder()
                    .createRenderPass(renderTarget.getColorTexture(), OptionalInt.empty(),
                                      renderTarget.getDepthTexture(), OptionalDouble.empty())) {

                renderPass.setPipeline(METAL_LINES);
                renderPass.setIndexBuffer(gpuBuffer, indices.type());
                renderPass.setVertexBuffer(0, vertexBuffer);
                renderPass.drawIndexed(0, indexCount);
            }
        }
    }

    public static void doneDrawingLines(PoseStack stack) {
        stack.popPose();
    }

    public static PoseStack prepareToDrawLines(PoseStack start) {
        start.pushPose();
        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        start.translate(-view.x, -view.y, -view.z);
        return start;
    }

    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(METAL_LINES);
    }
}
