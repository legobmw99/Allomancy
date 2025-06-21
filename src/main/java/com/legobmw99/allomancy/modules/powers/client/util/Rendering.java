package com.legobmw99.allomancy.modules.powers.client.util;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
            GpuBuffer vertexBuffer =
                    METAL_LINES.getVertexFormat().uploadImmediateVertexBuffer(meshData.vertexBuffer());


            RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();
            if (renderTarget.getColorTextureView() == null) {
                return;
            }
            int indexCount = meshData.drawState().indexCount();
            GpuBuffer gpuBuffer = indices.getBuffer(indexCount);

            GpuBufferSlice[] agpubufferslice = RenderSystem
                    .getDynamicUniforms()
                    .writeTransforms(new DynamicUniforms.Transform(RenderSystem.getModelViewMatrix(),
                                                                   new Vector4f(0.0F, 0.0F, 0.0F, 0.3F),
                                                                   RenderSystem.getModelOffset(),
                                                                   RenderSystem.getTextureMatrix(), width * 2.5F),
                                     new DynamicUniforms.Transform(RenderSystem.getModelViewMatrix(),
                                                                   new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                                                                   RenderSystem.getModelOffset(),
                                                                   RenderSystem.getTextureMatrix(), width));

            try (RenderPass renderPass = RenderSystem
                    .getDevice()
                    .createCommandEncoder()
                    .createRenderPass(() -> "allomancy lines", renderTarget.getColorTextureView(),
                                      OptionalInt.empty(), renderTarget.getColorTextureView(),
                                      OptionalDouble.empty())) {

                renderPass.setPipeline(METAL_LINES);
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setVertexBuffer(0, vertexBuffer);
                renderPass.setIndexBuffer(gpuBuffer, indices.type());
                renderPass.setUniform("DynamicTransforms", agpubufferslice[0]);
                renderPass.drawIndexed(0, 0, indexCount, 1);

                renderPass.setUniform("DynamicTransforms", agpubufferslice[0]);
                renderPass.drawIndexed(0, 0, indexCount, 1);
            }
        }

        RenderSystem.lineWidth(1.0F);
        tesselator.clear();
    }

    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(METAL_LINES);
    }
}
