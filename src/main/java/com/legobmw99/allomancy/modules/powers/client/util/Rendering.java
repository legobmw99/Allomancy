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
import org.joml.Vector3f;

import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public final class Rendering {
    private Rendering() {}


    public record Line(Vec3 dest, int color) {
    }


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
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES)
            .build();


    /**
     * Draws lines from the player to each destination
     *
     * @param source location of the player
     * @param lines  locations to draw toward
     * @param width  the width of the line
     */
    public static void drawMetalLines(PoseStack stack, Vec3 source, List<Line> lines, float width) {
        if (lines.isEmpty()) {
            return;
        }

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(METAL_LINES.getVertexFormatMode(), METAL_LINES.getVertexFormat());

        PoseStack.Pose pose = stack.last();
        Vector3f src = source.toVector3f();
        Vector3f normal = new Vector3f();

        for (var line : lines) {
            Vector3f dest = line.dest.toVector3f();
            dest.normalize(normal);

            builder.addVertex(pose, src).setColor(line.color).setNormal(pose, normal);
            builder.addVertex(pose, dest).setColor(line.color).setNormal(pose, normal);
        }


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

                RenderSystem.lineWidth(width * 2.5f);
                RenderSystem.setShaderColor(0, 0, 0, 0.3f);
                renderPass.drawIndexed(0, indexCount);

                RenderSystem.lineWidth(width);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                renderPass.drawIndexed(0, indexCount);
            }
        }

        RenderSystem.lineWidth(1.0F);
        tesselator.clear();
    }

    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(METAL_LINES);
    }
}
