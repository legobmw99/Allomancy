package com.legobmw99.allomancy.modules.powers.client.util;

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
import org.joml.Matrix4fStack;
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
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
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

            builder.addVertex(pose, src).setColor(line.color).setNormal(pose, normal).setLineWidth(width);
            builder.addVertex(pose, dest).setColor(line.color).setNormal(pose, normal).setLineWidth(width);

        }
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        var dynamic = RenderSystem
                .getDynamicUniforms()
                .writeTransform(matrix4fStack, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());

        try (MeshData meshData = builder.buildOrThrow()) {
            GpuBuffer vertexBuffer =
                    METAL_LINES.getVertexFormat().uploadImmediateVertexBuffer(meshData.vertexBuffer());

            RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();
            if (renderTarget.getColorTextureView() == null) {
                return;
            }
            int indexCount = meshData.drawState().indexCount();
            GpuBuffer gpuBuffer = indices.getBuffer(indexCount);
            try (RenderPass renderPass = RenderSystem
                    .getDevice()
                    .createCommandEncoder()
                    .createRenderPass(() -> "allomancy lines", renderTarget.getColorTextureView(),
                                      OptionalInt.empty(), renderTarget.getDepthTextureView(),
                                      OptionalDouble.empty())) {

                renderPass.setPipeline(METAL_LINES);
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setIndexBuffer(gpuBuffer, indices.type());
                renderPass.setVertexBuffer(0, vertexBuffer);

                renderPass.setUniform("DynamicTransforms", dynamic);
                renderPass.drawIndexed(0, 0, indexCount, 1);

            }
        }

        matrix4fStack.popMatrix();
        tesselator.clear();
    }

    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(METAL_LINES);
    }
}